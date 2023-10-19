package com.zareshahi.myreport.screen

import android.R
import android.R.attr.name
import android.app.Activity
import android.content.Context
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.entrity.Category
import com.zareshahi.myreport.database.entrity.NoteWithCategory
import com.zareshahi.myreport.database.repository.CategoryRepository
import com.zareshahi.myreport.database.repository.NoteRepository
import com.zareshahi.myreport.util.PersianDateTime
import com.zareshahi.myreport.util.readFromAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import java.io.File
import java.time.LocalDate
import java.util.Objects


class HomeViewModel(
    val noteRepository: NoteRepository,
    val categoryRepository: CategoryRepository,
    val persianDateTime: PersianDateTime
) : ViewModel() {
    private val _reportList = MutableStateFlow<List<NoteWithCategory>>(emptyList())
    val reportList = _reportList.asStateFlow()

    val isShowCategoryBottomSheet = mutableStateOf(false)
    val isShowDeleteCategory = mutableStateOf(false)
    val isShowSearchBottomSheet = mutableStateOf(false)
    val isShowPrintDialog = mutableStateOf(false)
    val selectedCategoryForDelete = mutableStateOf<Category?>(null)
    val categoryInputText = mutableStateOf("")

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList = _categoryList.asStateFlow()

    val searchFromDate = mutableStateOf<LocalDate>(LocalDate.now().minusDays(30))
    val searchToDate = mutableStateOf<LocalDate>(LocalDate.now())
    val searchCategory = mutableStateOf<Category?>(null)
    val searchText = mutableStateOf("")

    fun search() {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.fetchNotes(
                catID = searchCategory.value?.id,
                fromDate = searchFromDate.value,
                toDate = searchToDate.value,
                searchText = searchText.value
            ).distinctUntilChanged()
                .collect {
                    it?.let { reports ->
                        _reportList.value = reports
                    }
                }
        }
    }

    fun saveCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.addNewCategory(Category(name = categoryInputText.value))
            categoryInputText.value = ""
        }
    }

    fun getListCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.fetchAllCategories()
                .distinctUntilChanged()
                .collect {
                    it?.let { cats ->
                        _categoryList.value = cats
                    }
                }
        }
    }

    fun deleteCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedCategoryForDelete.value?.let { category ->
                categoryRepository.deleteCategory(category)
            }
        }
    }

    fun print(context: Context) {
        val temp = readFromAsset(context = context, filename = "report_temp.html")
        val tableData = StringBuilder()
        _reportList.value.forEachIndexed { index, report ->
            tableData.append(
                """
                <tr ${if ((index % 2) == 0) "style=\"background-color:rgb(193, 193, 193);\"" else ""}>
                    <td>${index + 1}</td>
                    <td class="col-note">${report.note.note}</td>
                    <td class="col-duration">${report.note.duration}</td>
                    <td class="col-date">${
                    persianDateTime.convertDateToPersianDate(
                        report.note.createdAt,
                        "j/F/Y l"
                    )
                }</td>
                </tr>
            """.trimIndent()
            )
        }
        val htmlData = temp.replace("**header**", "گزارش ۱")
            .replace(
                "**date**",
                "${persianDateTime.convertDateToPersianDate(searchFromDate.value,"Y/m/j")}-${
                    persianDateTime.convertDateToPersianDate(
                        searchToDate.value,
                        "Y/m/j"
                    )
                }"
            )
            .replace(
                "**category**",
                if (searchCategory.value == null) "پیش فرض" else "${searchCategory.value?.name}"
            ).replace("**keyword**", searchText.value)
            .replace("**is_show_note**", "block")
            .replace("**is_show_duration**", "none")
            .replace("**is_show_date**", "block")
            .replace("**table_data**", tableData.toString())

        showWebview(context,htmlData)


    }

    private fun showWebview(context: Context, htmlData: String) {
        val webView = WebView(context)
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.clearCache(true)
        webView.visibility = View.GONE
        webView.settings.allowFileAccess = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                PrintMethod1()
            }

            private fun PrintMethod1() {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter: PrintDocumentAdapter
                printAdapter =
                    webView.createPrintDocumentAdapter(
                        name.toString()
                    )
                val jobName =  "Report Print"
                val builder = PrintAttributes.Builder()
                builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                printManager.print(jobName, printAdapter, builder.build())
            }

        }
        webView.loadDataWithBaseURL(null, htmlData, "text/HTML", "UTF-8", null)
    }
}
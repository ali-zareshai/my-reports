package com.zareshahi.myreport.screen

import android.R.attr.name
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import java.time.LocalDate


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

    val reportHeader = mutableStateOf("")
    val reportFontSize = mutableStateOf(14)
    val reportIsShowNoteCol = mutableStateOf(true)
    val reportIsShowDurationCol = mutableStateOf(false)
    val reportIsShowDateCol = mutableStateOf(true)
    val reportIsShowTime = mutableStateOf(false)
    val isShowGroupDate = mutableStateOf(false)

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
                    ${if (reportIsShowNoteCol.value) "<td class=\"col-note\">${report.note.note}</td>" else ""}
                    ${if (reportIsShowDurationCol.value) "<td class=\"col-duration\">${
                    if (report.note.duration.equals(
                            ":"
                        )
                    ) "" else report.note.duration
                }</td>" else ""} 
                    ${if (reportIsShowDateCol.value) "<td class=\"col-date\">${
                    persianDateTime.convertDateToPersianDate(
                        report.note.createdAt,
                        "j/F/Y l ${if (reportIsShowTime.value) "H:m" else ""}"
                    )
                }</td>" else ""}
                    
                </tr>
            """.trimIndent()
            )
        }
        val tableHeader ="""
            <th class="col-id">ردیف</th>
            ${if (reportIsShowNoteCol.value) "<th class=\"col-note\">موضوع</th>" else ""}
            ${if (reportIsShowDurationCol.value) "<th class=\"col-duration\">مدت زمان</th>" else ""}   
            ${if (reportIsShowDateCol.value) "<th class=\"col-date\">تاریخ</th>" else ""}
        """.trimIndent()
        val htmlData = temp.replace("**header**", reportHeader.value)
            .replace(
                "**date**",
                "${persianDateTime.convertDateToPersianDate(searchFromDate.value,"Y/m/j")} || ${
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
            .replace("**table_data**", tableData.toString())
            .replace(" **table_header**",tableHeader)
            .replace("**is_show_header**",if (reportHeader.value.isBlank()) "none" else "block")
            .replace("**font-size**","${reportFontSize.value}px;")

        Log.e("report>>",htmlData)
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
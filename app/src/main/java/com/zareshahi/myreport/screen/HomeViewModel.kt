package com.zareshahi.myreport.screen

import android.R.attr.name
import android.content.Context
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Workbook
import java.io.IOException
import java.io.OutputStream
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
    val isShowExcelDialog = mutableStateOf(false)
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
    val reportIsShowGroupDate = mutableStateOf(false)
    val reportIsShowSearchedKeyWord = mutableStateOf(false)
    val reportIsShowSumTimes = mutableStateOf(false)

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

        val tableHeader ="""
            <th class="col-id">ردیف</th>
            ${if (reportIsShowNoteCol.value) "<th class=\"col-note\">موضوع</th>" else ""}
            ${if (reportIsShowDurationCol.value) "<th class=\"col-duration\">مدت زمان</th>" else ""}   
            ${if (reportIsShowDateCol.value) "<th class=\"col-date\">تاریخ</th>" else ""}
        """.trimIndent()
        val tableFooter ="""
            <th class="col-id"></th>
            ${if (reportIsShowNoteCol.value) "<th class=\"col-note\"></th>" else ""}
            ${if (reportIsShowDurationCol.value) "<th class=\"col-duration\">${
            sumDurations(_reportList.value.map {
                it.note.duration?:""
            })
            }</th>" else ""}   
            ${if (reportIsShowDateCol.value) "<th class=\"col-date\"></th>" else ""}
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
            ).replace("**keyword**",if (reportIsShowSearchedKeyWord.value) searchText.value else "")
            .replace("**table_data**", generateHtmlRowData())
            .replace(" **table_header**",tableHeader)
            .replace("**is_show_header**",if (reportHeader.value.isBlank()) "none" else "block")
            .replace("**font-size**","${reportFontSize.value}px;")
            .replace(" **table_footer**",if(reportIsShowDurationCol.value) tableFooter else "")

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

    private fun generateHtmlRowData(): String {
        val tableData = StringBuilder()
        var index=1
        if (reportIsShowGroupDate.value){
            _reportList.value.groupBy {
                it.note.createdAt.toLocalDate()
            }.forEach {
                val notes =it.value
                    .map { it.note.note }
                    .map { "<li>$it</li>" }
                    .joinToString("")
                tableData.append(
                    """
                <tr ${if ((index % 2) == 0) "style=\"background-color:rgb(193, 193, 193);\"" else ""}>
                    <td>${index++}</td>
                    ${if (reportIsShowNoteCol.value) "<td class=\"col-note\"><ol>$notes</ol></td>" else ""}
                    ${if (reportIsShowDurationCol.value) "<td class=\"col-duration\">${sumDurations(it.value.map { 
                        it.note.duration?:""
                    })}</td>" else ""} 
                    ${if (reportIsShowDateCol.value) "<td class=\"col-date\">${
                        persianDateTime.convertDateToPersianDate(
                            it.key,
                            "j/F/Y l"
                        )
                    }</td>" else ""}
                    
                </tr>
            """.trimIndent()
                )
            }


        }else{
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
        }
        return tableData.toString()
    }

    private fun sumDurations(sumList:List<String>): String {
        var sumMinutes =0L
        sumList.forEach {
            if (it.isNotBlank() && it != ":"){
                try {
                    val hm= it.split(":")
                    sumMinutes +=(hm[0].toInt()*60)+hm[1].toInt()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        val h=sumMinutes/60
        val m=sumMinutes - (h*60)
        return "جمع: $h:$m "
    }

    fun report2Excel(context: Context,uri: Uri){
        viewModelScope.launch(Dispatchers.IO){
            val wb: Workbook = HSSFWorkbook()
            var cell: Cell? = null

            val bodyFont: Font = wb.createFont()
            bodyFont.fontHeightInPoints = 12.toShort()
            bodyFont.fontName = "Tahoma"

            val cs1: CellStyle = wb.createCellStyle()
            val cs2: CellStyle = wb.createCellStyle()
            cs1.fillForegroundColor = HSSFColor.LIGHT_YELLOW.index
            cs2.fillForegroundColor = HSSFColor.LIGHT_GREEN.index
            cs1.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
            cs2.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
            cs1.setFont(bodyFont)
            cs2.setFont(bodyFont)
            cs1.borderRight = CellStyle.BORDER_THIN
            cs2.borderRight = CellStyle.BORDER_THIN
            cs1.borderBottom = CellStyle.BORDER_THIN
            cs2.borderBottom = CellStyle.BORDER_THIN

            val font: Font = wb.createFont()
            font.fontHeightInPoints = 15.toShort()
            bodyFont.fontName = "Arial"

            val csHDR: CellStyle = wb.createCellStyle()
            csHDR.fillForegroundColor = HSSFColor.LIME.index
            csHDR.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
            csHDR.setFont(font)
            csHDR.borderRight = CellStyle.BORDER_THIN
            csHDR.borderBottom = CellStyle.BORDER_THIN

            //New Sheet
            val sheet1 = wb.createSheet(if (searchCategory.value == null) "پیش فرض" else "${searchCategory.value?.name}")
            sheet1.isRightToLeft = true

            val row = sheet1.createRow(0)

            cell = row.createCell(0)
            cell.setCellValue("ردیف")
            cell.cellStyle = csHDR

            cell = row.createCell(1)
            cell.setCellValue("تاریخ")
            cell.cellStyle = csHDR

            cell = row.createCell(2)
            cell.setCellValue("ساعت")
            cell.cellStyle = csHDR

            cell = row.createCell(3)
            cell.setCellValue("موضوع")
            cell.cellStyle = csHDR

            cell = row.createCell(4)
            cell.setCellValue("مدت زمان")
            cell.cellStyle = csHDR

            sheet1.setColumnWidth(0, 2000);
            sheet1.setColumnWidth(1, 5000)
            sheet1.setColumnWidth(2, 5000)
            sheet1.setColumnWidth(3, 15000)
            sheet1.setColumnWidth(4, 5000)

            _reportList.value.forEachIndexed{index,item->
                val itmRow = sheet1.createRow(index+1)
                itmRow.createCell(0).setCellValue("${index+1}")

                itmRow.createCell(1)
                    .setCellValue(persianDateTime.convertDateToPersianDate(item.note.createdAt,"Y/m/j"))
                itmRow.createCell(2)
                    .setCellValue(persianDateTime.convertDateToPersianTime(item.note.createdAt))

                itmRow.createCell(3).setCellValue(item.note.note)
                itmRow.createCell(4).setCellValue(if (item.note.duration.equals(":")) "" else item.note.duration)

                val styleRow =if (index % 2 == 0) cs1 else cs2
                itmRow.getCell(0).cellStyle = styleRow
                itmRow.getCell(1).cellStyle = styleRow
                itmRow.getCell(2).cellStyle = styleRow
                itmRow.getCell(3).cellStyle = styleRow
                itmRow.getCell(4).cellStyle = styleRow
            }
            val footerRow = sheet1.createRow(_reportList.value.size+1)
            footerRow.createCell(0).setCellValue("")
            footerRow.createCell(1).setCellValue("")
            footerRow.createCell(2).setCellValue("")
            footerRow.createCell(3).setCellValue("")
            footerRow.createCell(4).setCellValue(sumDurations(_reportList.value.map { it.note.duration?:"" }))

            footerRow.getCell(0).cellStyle = csHDR
            footerRow.getCell(1).cellStyle = csHDR
            footerRow.getCell(2).cellStyle = csHDR
            footerRow.getCell(3).cellStyle = csHDR
            footerRow.getCell(4).cellStyle = csHDR

            var  os:OutputStream?=null
            try {
                os = context.contentResolver.openOutputStream(uri)
                wb.write(os)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                os?.close()
            }
        }
    }
}
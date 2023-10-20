package com.zareshahi.myreport.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zareshahi.myreport.R
import com.zareshahi.myreport.component.CategoryDropMenu
import com.zareshahi.myreport.component.FromToDatePicker
import com.zareshahi.myreport.component.TextInput
import com.zareshahi.myreport.navigation.Routes
import com.zareshahi.myreport.util.PersianDateTime
import com.zareshahi.myreport.util.PrinterDialog
import com.zareshahi.myreport.component.AnimatedContent
import com.zareshahi.myreport.component.BottomCard
import com.zareshahi.myreport.component.MyCard
import com.zareshahi.myreport.component.SimpleTopBar
import com.zareshahi.myreport.util.FilePickerDialog
import com.zareshahi.myreport.util.openExcelFile
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = koinViewModel()) {
    LaunchedEffect(key1 = Unit) {
        homeViewModel.search()
        homeViewModel.getListCategory()
    }
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "گزارش کار",
                isShowBackButton = false,
                actions = {
                    IconButton(onClick = { homeViewModel.isShowSearchBottomSheet.value = true }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterAlt,
                            contentDescription = "فیلتر گزارش ها"
                        )
                    }
                }
            )
        },
        content = { ContentHome(paddingValues = it, navController) },
        bottomBar = {
            BottomBarHome(navController)
        }
    )
    AnimatedContent(trueState = homeViewModel.isShowCategoryBottomSheet.value) {
        if (it) {
            CategoryBottomSheet()
        }
    }

    AnimatedContent(trueState = homeViewModel.isShowDeleteCategory.value) {
        if (it)
            DeleteCategoryDialog()
    }

    AnimatedContent(trueState = homeViewModel.isShowSearchBottomSheet.value) {
        if (it)
            SearchBottomSheet()
    }

    AnimatedContent(trueState = homeViewModel.isShowPrintDialog.value) {
        if (it)
            printDialog()
    }
    AnimatedContent(trueState = homeViewModel.isShowExcelDialog.value) {
        if (it)
            excelDialog()
    }
}

@Composable
fun excelDialog(screenVm: HomeViewModel = koinViewModel()) {
    val  context = LocalContext.current
    FilePickerDialog(
        onCloseClick = { screenVm.isShowExcelDialog.value = false }
    ) { _, uri, openFileAfterCreate ->
        uri?.let {
            screenVm.report2Excel(
                context=context,
                uri=it
            )
        }

        Toast.makeText(context, "فایل با موفقیت ذخیره شد", Toast.LENGTH_LONG).show()
        if (uri != null && openFileAfterCreate)
            openExcelFile(context = context, uri = uri)

        screenVm.isShowExcelDialog.value = false
    }
}

@Composable
fun printDialog(homeViewModel: HomeViewModel = koinViewModel()) {
    val context = LocalContext.current
    PrinterDialog(
        onCloseClick = { homeViewModel.isShowPrintDialog.value =false},
        onConfirmClick = {
            homeViewModel.print(context)
        }
    )
}

@Composable
fun SearchBottomSheet(
    homeViewModel: HomeViewModel = koinViewModel(),
    persianDateTime: PersianDateTime = koinInject()
) {
    val lisCategory = homeViewModel.categoryList.collectAsState().value

    BottomCard(
        fabButtons = { SearchFabButtons() },
        onClose = { homeViewModel.isShowSearchBottomSheet.value = false }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp), horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "جستجو")
        }
        TextInput(
            value = homeViewModel.searchText.value,
            onValueChange = { txt ->
                homeViewModel.searchText.value = txt
            },
            placeholderText = "جستجو"
        )
        Spacer(modifier = Modifier.height(4.dp))
        CategoryDropMenu(
            listCategory = lisCategory,
            defaultSelectedCategory = homeViewModel.searchCategory.value,
            modifier = Modifier.fillMaxWidth(),
            onSelect = {
                homeViewModel.searchCategory.value = it
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        FromToDatePicker(
            defaultFromDate = persianDateTime.convertDateToPersianDate(
                homeViewModel.searchFromDate.value ?: LocalDate.now()
            ),
            defaultToDate = persianDateTime.convertDateToPersianDate(
                homeViewModel.searchToDate.value ?: LocalDate.now()
            ),
            fromDate = {
                homeViewModel.searchFromDate.value = it
            },
            toDate = {
                homeViewModel.searchToDate.value = it
            }
        )
        Spacer(modifier = Modifier.height(4.dp))


    }
}

@Composable
fun DeleteCategoryDialog(homeViewModel: HomeViewModel = koinViewModel()) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { homeViewModel.isShowDeleteCategory.value = false },
        confirmButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    homeViewModel.deleteCategory()
                    homeViewModel.isShowDeleteCategory.value = false
                    Toast.makeText(context, "حذف شد", Toast.LENGTH_LONG).show()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        "بله",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(text = "بله")
                }
                TextButton(onClick = { homeViewModel.isShowDeleteCategory.value = false }) {
                    Icon(imageVector = Icons.Rounded.Cancel, "خیر")
                    Text(text = "خیر")
                }
            }
        },
        title = {
            Text(text = "آیا مطمئن هستید؟")
        },
        text = {
            Text("آیا می خواهید این دسته حذف گردد؟")
        }
    )
}

@Composable
private fun CategoryBottomSheet(homeViewModel: HomeViewModel = koinViewModel()) {
    BottomCard(
        fabButtons = {},
        onClose = { homeViewModel.isShowCategoryBottomSheet.value = false }
    ) {
        val catList = homeViewModel.categoryList.collectAsState().value
        val context = LocalContext.current
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp), horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "مدیریت دسته ها")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextInput(
                    modifier = Modifier.weight(3f),
                    value = homeViewModel.categoryInputText.value,
                    onValueChange = { txt ->
                        homeViewModel.categoryInputText.value = txt
                    },
                    placeholderText = "نام دسته"
                )
                IconButton(onClick = {
                    if (homeViewModel.categoryInputText.value.isEmpty()) {
                        Toast.makeText(
                            context,
                            "نام دسته نمی تواند خالی باشد",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        homeViewModel.saveCategory()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = "ذخیره دسته"
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp),
                thickness = 1.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp, min = 300.dp)
            ) {
                items(items = catList, key = { item -> "${item.id}" }) {
                    MyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.name ?: "--",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(3f)
                                    .padding(7.dp)
                            )
                            IconButton(onClick = {
                                homeViewModel.selectedCategoryForDelete.value = it
                                homeViewModel.isShowDeleteCategory.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "حذف دسته",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun BottomBarHome(navController: NavController, screenVM: HomeViewModel = koinViewModel()) {
    val context = LocalContext.current
    BottomAppBar(
        actions = {
            IconButton(onClick = { screenVM.isShowCategoryBottomSheet.value = true }) {
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = "مدیریت دسته ها")
            }
            IconButton(onClick = {
                screenVM.isShowPrintDialog.value = true
            }) {
                Icon(imageVector = Icons.Rounded.Print, contentDescription = "چاپ گزارش")
            }
            IconButton(onClick = {
                screenVM.isShowExcelDialog.value = true
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_export_excel),
                    contentDescription = "اکسل"
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.ADD_REPORT.route)
                }
            ) {
                Icon(Icons.Filled.Add, "گزارش کار جدید")
            }
        }
    )
}

@Composable
fun ContentHome(
    paddingValues: PaddingValues,
    navController: NavController,
    homeViewModel: HomeViewModel = koinViewModel(),
    persianDateTime: PersianDateTime = koinInject()
) {
    val reportList = homeViewModel.reportList.collectAsState().value
    Box(modifier = Modifier.padding(paddingValues)) {
        if (reportList.isEmpty()){
            NotFound()
        }else{
            LazyColumn {
                itemsIndexed(items = reportList, key = { index, _ -> "$index" }) { index, note ->
                    MyCard(
                        modifier = Modifier
                            .padding(7.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("${Routes.ADD_REPORT.route}?id=${note.note.id}")
                            }
                    ) {
                        Text(
                            text = note.note.note,
                            minLines = 2,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(7.dp)
                        )
                        Row(modifier = Modifier.padding(7.dp)) {
                            Text(
                                text = "${persianDateTime.convertDateToPersianDateWeekDay(note.note.createdAt)}",
                                modifier = Modifier
                                    .padding(7.dp)
                                    .weight(1f)
                            )
                            Text(
                                text = note.category?.name ?: "پیش فرض",
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .padding(7.dp)
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }

    }
}


@Composable
private fun SearchFabButtons(homeViewModel: HomeViewModel = koinViewModel()) {
    FloatingActionButton(
        onClick = {
            homeViewModel.search()
            homeViewModel.isShowSearchBottomSheet.value = false
        },
    ) {
        Icon(
            imageVector = Icons.Rounded.Search, contentDescription = "جستجو"
        )
    }
}

@Composable
private fun  NotFound(){
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Rounded.FindInPage,
            contentDescription = "موردی پیدا نشد",
            modifier = Modifier
                .size(100.dp)
                .padding(7.dp)
        )
        Text(text = "موردی پیدا نشد")
    }
}
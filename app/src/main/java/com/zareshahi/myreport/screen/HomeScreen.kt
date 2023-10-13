package com.zareshahi.myreport.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zareshahi.myreport.component.TextInput
import com.zareshahi.myreport.navigation.Routes
import ir.esfandune.wave.compose.component.core.AnimatedContent
import ir.esfandune.wave.compose.component.core.BottomCard
import ir.esfandune.wave.compose.component.core.MyCard
import ir.esfandune.wave.compose.component.core.SimpleTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = koinViewModel()) {
    LaunchedEffect(key1 = Unit) {
        homeViewModel.search()
    }
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "گزارش کار",
                isShowBackButton = false
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
}

@Composable
private fun CategoryBottomSheet(homeViewModel: HomeViewModel = koinViewModel()) {
    BottomCard(
        fabButtons = {},
        onClose = { homeViewModel.isShowCategoryBottomSheet.value = false }
    ) {
        LaunchedEffect(key1 = Unit) {
            homeViewModel.getListCategory()
        }
        val catList = homeViewModel.categoryList.collectAsState().value
        val context = LocalContext.current
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
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
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)){
                items(items=catList,key={item->"${item.id}"}){
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
                            IconButton(onClick = { homeViewModel.deleteCategory(it) }) {
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
    BottomAppBar(
        actions = {
            IconButton(onClick = { screenVM.isShowCategoryBottomSheet.value = true }) {
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = "مدیریت دسته ها")
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
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val reportList = homeViewModel.reportList.collectAsState().value
    Box(modifier = Modifier.padding(paddingValues)) {
        LazyColumn {
            itemsIndexed(items = reportList, key = { index, _ -> "$index" }) { index, note ->
                MyCard(
                    modifier = Modifier
                        .padding(7.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = note.note)
                }
            }
        }
    }
}

//@Composable
//private fun FabButtons(homeViewModel: HomeViewModel= koinViewModel()) {
//    FloatingActionButton(
//        onClick = {
//
//        },
//    ) {
//        Icon(
//            imageVector = Icons.Rounded.Save, contentDescription = "ذخیره"
//        )
//    }
//}

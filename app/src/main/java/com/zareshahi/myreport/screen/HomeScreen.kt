package com.zareshahi.myreport.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
fun HomeScreen(navController: NavController,homeViewModel: HomeViewModel= koinViewModel()){
    LaunchedEffect(key1 = Unit){
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
    AnimatedContent(trueState = homeViewModel.isShowBottomSheet.value) {
        if (it){
            BottomCard(
                fabButtons = { FabButtons()},
                onClose = { homeViewModel.isShowBottomSheet.value =false}
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextInput(
                        modifier=Modifier.fillMaxWidth().padding(7.dp),
                        value = homeViewModel.inputText.value,
                        onValueChange = {txt->
                            homeViewModel.inputText.value =txt
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBarHome(navController: NavController, homeViewModel: HomeViewModel= koinViewModel()) {
    BottomAppBar(
        actions = {},
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
fun ContentHome(paddingValues: PaddingValues, navController: NavController, homeViewModel: HomeViewModel= koinViewModel()) {
    val reportList =homeViewModel.reportList.collectAsState().value
    Box(modifier = Modifier.padding(paddingValues)) {
        LazyColumn{
            itemsIndexed(items = reportList, key = {index,_->"$index"}){index,note->
                MyCard(modifier = Modifier.padding(7.dp).fillMaxWidth()) {
                    Text(text = note.note)
                }
            }
        }
    }
}

@Composable
private fun FabButtons(homeViewModel: HomeViewModel= koinViewModel()) {
    FloatingActionButton(
        onClick = {

        },
    ) {
        Icon(
            imageVector = Icons.Rounded.Save, contentDescription = "ذخیره"
        )
    }
}

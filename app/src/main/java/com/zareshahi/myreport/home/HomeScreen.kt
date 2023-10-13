package com.zareshahi.myreport.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController,homeViewModel: HomeViewModel= koinViewModel()){
    Column {
        Text(text = "ali")
        TextField(value = "allli", onValueChange = {})
        IconButton(onClick = {
            homeViewModel.test()
        }) {
            Icon(imageVector = Icons.Rounded.Home, contentDescription = "home")
        }
    }

}
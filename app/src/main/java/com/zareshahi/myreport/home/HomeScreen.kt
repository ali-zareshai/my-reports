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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    Column {
        Text(text = "ali")
        TextField(value = "allli", onValueChange = {})
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Rounded.Home, contentDescription = "home")
        }
    }

}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.zareshahi.myreport.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection


@Composable
fun SimpleTopBar(
    title: String,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    actions: @Composable RowScope.() -> Unit = {},
    onBackClick: () -> Unit={},
    isShowBackButton:Boolean=true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        CenterAlignedTopAppBar(
            colors = colors,
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.W600
                )
            },
            navigationIcon = {
                if (isShowBackButton){
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                }
            },
            actions = actions

        )
    }
}



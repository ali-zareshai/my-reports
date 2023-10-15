package com.zareshahi.myreport.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.zareshahi.myreport.database.entrity.Category
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoryDropMenu(
    listCategory:List<Category>,
    onSelect:(Category)->Unit,
    modifier: Modifier=Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val isOpenDropMenu = remember {
            mutableStateOf(false)
        }
        val selectedCategory = remember {
            mutableStateOf<Category?>(null)
        }
        Text(text = "دسته بندی:")
        TextButton(
            modifier = Modifier.padding(7.dp),
            onClick = { isOpenDropMenu.value = true }
        ) {
            Text(text = selectedCategory.value?.name ?: "پیش فرض")
        }
        DropdownMenu(
            expanded = isOpenDropMenu.value,
            onDismissRequest = { isOpenDropMenu.value = false }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                listCategory.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name ?: "نامشخص",
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        onClick = {
                            onSelect(category)
                            selectedCategory.value =category
                            isOpenDropMenu.value = false
                        },
                        modifier = Modifier.padding(3.dp)
                    )
                }
            }
        }
    }

}
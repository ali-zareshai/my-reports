package com.zareshahi.myreport.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun MyCheckbox(modifier: Modifier=Modifier,title:String, checked:Boolean, enabled:Boolean=true, onCheckedChange:(Boolean)->Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable {
                if (enabled)
                    onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled
        )
        Spacer(modifier = Modifier.size(7.dp))
        Text(
            text = title,
            Modifier.weight(1f)
        )
    }
}

@Composable
fun WaveRtlCheckbox(modifier: Modifier=Modifier,title:String, checked:Boolean, enabled:Boolean=true, onCheckedChange:(Boolean)->Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                if (enabled)
                    onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl){
            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                modifier = Modifier
            )

        }
    }
}
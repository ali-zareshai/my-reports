@file:OptIn(ExperimentalMaterial3Api::class)

package com.zareshahi.myreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    name: String, onClick: () -> Unit,
    leadingIcon: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    textAlign: TextAlign? = null,
    enabled: Boolean = true
) {

    Box(
        modifier
            .background(
                shape = RoundedCornerShape(12.dp),
                color = color
            )
            .clickable(enabled = enabled) { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TextFieldDefaults.textFieldWithoutLabelPadding()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        )
        {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
            Text(name, Modifier.weight(1f), textAlign = textAlign)
            if (trailingIcon != null) {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                trailingIcon()
            }
        }
    }
}
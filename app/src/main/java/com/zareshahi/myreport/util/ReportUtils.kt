package com.zareshahi.myreport.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader


fun readFromAsset(context:Context,filename:String): String {
    val reader = BufferedReader(InputStreamReader(context.assets.open(filename)))
    val sb = StringBuilder()
    var mLine = reader.readLine()
    while (mLine != null) {
        sb.append(mLine)
        mLine = reader.readLine()
    }
    reader.close()
    return sb.toString()
}

@Composable
fun PrinterDialog(
    onCloseClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCloseClick() },
        icon = { Icon(Icons.Rounded.Print, contentDescription = "print") },
        title = { Text(text = "چاپ", fontWeight = FontWeight.W300) },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = { onConfirmClick() }) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = "تایید"
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.Start
            ) {

            }
        }
    )
}

@Composable
fun FilePickerDialog(
    onCloseClick: () -> Unit,
    mimeType: String = "application/vnd.ms-excel",
    formatFile: String = "xls",
    onConfirmClick: (fileName: String, uri: Uri?, openFileAfterCreate:Boolean) -> Unit
) {
    val openFile = rememberSaveable {
        mutableStateOf(false)
    }

    val fileName = rememberSaveable {
        mutableStateOf("")
    }

    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = mimeType
    intent.putExtra(Intent.EXTRA_TITLE, "${fileName.value}.${formatFile}")

    val selectFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { actResult ->
            if (actResult.resultCode == Activity.RESULT_OK) {
                actResult.data?.let { data ->
                    onConfirmClick(fileName.value, data.data, openFile.value)
                }
            }
        }
    AlertDialog(
        onDismissRequest = { onCloseClick() },
        title = { Text(text = "انتخاب نام", fontWeight = FontWeight.W300) },
        icon = { Icon(Icons.Rounded.AttachFile, contentDescription = "file_picker") },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                IconButton(
                    onClick = {
                        if (!fileName.value.isNullOrBlank()) {
                            selectFileLauncher.launch(intent)
                        }
                    },
                    enabled = !fileName.value.isNullOrBlank()
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = "تایید")
                }
            }
        },
        text = {
            Column(modifier = Modifier.padding(2.dp), horizontalAlignment = Alignment.Start) {
                TextField(
                    value = fileName.value,
                    onValueChange = {
                        fileName.value = it
                    },
                    placeholder = { Text(text = "نام فایل")}
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "باز کردن فایل پس از ذخیره")
                    Checkbox(
                        checked = openFile.value,
                        onCheckedChange = { openFile.value = it })
                }


            }
        }
    )

}
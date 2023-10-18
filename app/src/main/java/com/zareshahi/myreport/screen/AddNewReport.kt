package com.zareshahi.myreport.screen

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import com.zareshahi.myreport.component.CategoryDropMenu
import com.zareshahi.myreport.component.TextInput
import com.zareshahi.myreport.database.entrity.Category
import com.zareshahi.myreport.navigation.Routes
import ir.esfandune.wave.compose.component.core.AnimatedContent
import ir.esfandune.wave.compose.component.core.MyCard
import ir.esfandune.wave.compose.component.core.SimpleTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewReport(
    id: Long?,
    navController: NavController,
    screenVM: AddNewReportViewModel = koinViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        screenVM.getListCategory()
        id?.let {
            screenVM.getReportForEdit(it)
        }
    }
    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "گزارش کار جدید",
                onBackClick = { navController.popBackStack() },
                isShowBackButton = true,
                actions = {
                    if (screenVM.isEditMode.value) {
                        IconButton(onClick = { screenVM.isShowDeleteNoteDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "حذف گزارش",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        content = { ContentAdd(paddingValues = it, navController) },
        bottomBar = {
            BottomBarAdd(navController)
        }
    )

    JalaliDatePickerDialog(
        openDialog = screenVM.isShowDatePicker,
        onSelectDay = {
            Log.d("Date", "onSelect: ${it.day} ${it.monthString} ${it.year}")
        },
        onConfirm = {
            screenVM.selectedZoneDateTime.value = it.toGregorian().toZonedDateTime()
            screenVM.selectedDate.value = "${it.day}/${it.monthString}/${it.year}"
        }
    )

    AnimatedContent(trueState = screenVM.isShowTimePicker.value) {
        if (it) {
            TimePickerDialog(
                onDismissRequest = { screenVM.isShowTimePicker.value = false },
                onTimeChange = {
                    screenVM.selectedLocalTime.value = it
                    screenVM.selectedTime.value = "${it.hour}:${it.minute}"
                    screenVM.isShowTimePicker.value = false
                },
                is24HourFormat = true
            )
        }
    }

    AnimatedContent(trueState = screenVM.isShowDeleteNoteDialog.value) {
        if (it) {
            AlertDialog(
                onDismissRequest = { screenVM.isShowDeleteNoteDialog.value = false },
                confirmButton = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = {
                            screenVM.delete()
                            screenVM.isShowDeleteNoteDialog.value = false
                            Toast.makeText(context, "حذف شد", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                "بله",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(text = "بله")
                        }
                        TextButton(onClick = { screenVM.isShowDeleteNoteDialog.value = false }) {
                            Icon(imageVector = Icons.Rounded.Cancel, "خیر")
                            Text(text = "خیر")
                        }
                    }
                },
                title = {
                    Text(text = "آیا مطمئن هستید؟")
                },
                text = {
                    Text("آیا می خواهید این گزارش حذف گردد؟")
                }
            )
        }
    }
}

@Composable
fun BottomBarAdd(navController: NavController, screenVM: AddNewReportViewModel = koinViewModel()) {
    val context = LocalContext.current
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (screenVM.listWorks.value.isEmpty()) {
                        Toast.makeText(context, "لیست خالی است", Toast.LENGTH_LONG).show()
                    } else {
                        screenVM.save()
                        navController.navigate(Routes.HOME.route)
                    }

                }
            ) {
                Icon(Icons.Filled.Save, "ذخیره")
            }
        }
    )
}

@Composable
fun ContentAdd(
    paddingValues: PaddingValues,
    navController: NavController,
    screenVM: AddNewReportViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lisCategory =screenVM.categoryList.collectAsState().value
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    ) {
        MyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth()
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { screenVM.isShowDatePicker.value = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "تاریخ:", fontSize = 19.sp)
                        Text(
                            text = screenVM.selectedDate.value.ifEmpty { "انتخاب نشده" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { screenVM.isShowTimePicker.value = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "زمان:", fontSize = 19.sp)
                        Text(
                            text = screenVM.selectedTime.value.ifEmpty { "انتخاب نشده" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp),
                thickness = 1.5.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)
            ) {
                CategoryDropMenu(
                    listCategory=lisCategory,
                    defaultSelectedCategory=screenVM.selectedCategory.value,
                    modifier = Modifier.weight(1f),
                    onSelect = {
                        screenVM.selectedCategory.value = it
                    }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "مدت زمان:")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextField(
                                value = if (screenVM.durationMinuteTime.value == null) "" else screenVM.durationMinuteTime.value.toString(),
                                onValueChange = {
                                    if (it.isNotBlank() && it.toInt() < 60)
                                        screenVM.durationMinuteTime.value = it
                                    else
                                        screenVM.durationMinuteTime.value = ""
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.padding(7.dp)
                            )
                            Text(text = "دقیقه")
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextField(
                                value = if (screenVM.durationHoursTime.value == null) "" else screenVM.durationHoursTime.value.toString(),
                                onValueChange = {
                                    if (it.length <= 3)
                                        screenVM.durationHoursTime.value = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.padding(7.dp)
                            )
                            Text(text = "ساعت")
                        }

                    }
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp),
            thickness = 1.5.dp,
            color = MaterialTheme.colorScheme.primary
        )

        TextInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp),
            value = screenVM.newTxt.value,
            onValueChange = {
                screenVM.newTxt.value = it
            }
        )
        Spacer(modifier = Modifier.height(7.dp))
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            onClick = {
                if (screenVM.isEditMode.value) {
                    screenVM.edit()
                    Toast.makeText(context, "ویرایش شد", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                } else
                    screenVM.addNewWork()
            }
        ) {
            if (screenVM.isEditMode.value) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = "آپدیت")
                Text(text = "آپدیت")
            } else {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "اضافه")
                Text(text = "اضافه کردن")
            }
        }


    }

    MyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)
    ) {
        val listWork = screenVM.listWorks.collectAsState().value
        LazyColumn {
            itemsIndexed(items = listWork, key = { index, item -> "$index" }) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp)
                ) {
//                        Text(text = "${index+1}")
                    Text(
                        text = item.note ?: "--",
                        fontSize = 21.sp,
                        modifier = Modifier.weight(3f)
                    )
                    IconButton(onClick = { screenVM.deleteItemFromList(item) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}




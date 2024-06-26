package com.zareshahi.myreport.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import com.zareshahi.myreport.util.PersianDateTime
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun FromToDatePicker(
    defaultFromDate:String?,
    defaultToDate:String?,
    fromDate:(LocalDate)->Unit,
    toDate:(LocalDate)->Unit
){
    val isShowDialog = remember {
        mutableStateOf(false)
    }
    val fromDate = remember {
        mutableStateOf(defaultFromDate)
    }
    val selected = remember {
        mutableStateOf("from")
    }
    val toDate = remember {
        mutableStateOf(defaultToDate)
    }
    JalaliDatePickerDialog(
        openDialog = isShowDialog,
        onSelectDay = {
            Log.d("Date", "onSelect: ${it.day} ${it.monthString} ${it.year}")
        },
        onConfirm = {
            if (selected.value=="from"){
                fromDate.value ="${it.day}/${it.monthString}/${it.year}"
                fromDate(it.toGregorian().toZonedDateTime().toLocalDate())
            } else if(selected.value=="to"){
                toDate.value ="${it.day}/${it.monthString}/${it.year}"
                toDate(it.toGregorian().toZonedDateTime().toLocalDate())
            }
        }
    )
    Row( 
        modifier= Modifier
            .fillMaxWidth()
            .padding(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyButton(
            name = "از تاریخ:\n ${fromDate.value}",
            onClick = {
                isShowDialog.value =true
                selected.value ="from"
            },
            modifier = Modifier
                .weight(1f)
                .padding(7.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = "تاریخ")
            }
        )

        Spacer(modifier = Modifier.width(7.dp))
        MyButton(
            name = "تا تاریخ:\n ${toDate.value}",
            onClick = {
                isShowDialog.value =true
                selected.value ="to"
            },
            modifier = Modifier
                .weight(1f)
                .padding(7.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = "تاریخ")
            }
        )
    }
}
package com.zareshahi.myreport.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.repository.NoteRepository
import com.zareshahi.myreport.model.TempNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import java.time.ZonedDateTime
import com.zareshahi.myreport.database.entrity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AddNewReportViewModel(val noteRepository: NoteRepository): ViewModel() {
    val isShowDatePicker = mutableStateOf(false)
    val isShowTimePicker = mutableStateOf(false)

    val selectedDate = mutableStateOf("")
    val selectedTime = mutableStateOf("")
    val selectedZoneDateTime = mutableStateOf<ZonedDateTime?>(null)
    val selectedLocalTime = mutableStateOf<LocalTime?>(null)

    val newTxt = mutableStateOf("")
    private val _listWorks = MutableStateFlow<List<TempNote>>(emptyList())
    val listWorks =_listWorks.asStateFlow()

    fun addNewWork(){
        if(newTxt.value.isEmpty())
            return
        val listWork =_listWorks.value.toMutableList()
        val temp =TempNote(
            zonedDateTime = selectedZoneDateTime.value,
            localTime = selectedLocalTime.value,
            note = newTxt.value
        )
        listWork.add(temp)
        _listWorks.value =listWork
        newTxt.value =""
    }

    fun save(){
        viewModelScope.launch(Dispatchers.IO){
            listWorks.value.forEach {item->
                val note =Note(
                    note = item.note?:"--",
                    createdAt = LocalDateTime.of(item.zonedDateTime?.toLocalDate(),item.localTime)
                )
                noteRepository.addNewNote(note)
            }
        }
    }

    fun deleteItemFromList(tempNote: TempNote){
        val listWork =_listWorks.value.toMutableList()
        listWork.remove(tempNote)
        _listWorks.value =listWork
    }



}
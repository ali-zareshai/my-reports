package com.zareshahi.myreport.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.entrity.Category
import com.zareshahi.myreport.database.repository.NoteRepository
import com.zareshahi.myreport.model.TempNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import java.time.ZonedDateTime
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AddNewReportViewModel(val noteRepository: NoteRepository,val categoryRepository: CategoryRepository): ViewModel() {
    val isShowDatePicker = mutableStateOf(false)
    val isShowTimePicker = mutableStateOf(false)

    val selectedDate = mutableStateOf("")
    val selectedTime = mutableStateOf("")
    val selectedZoneDateTime = mutableStateOf<ZonedDateTime>(ZonedDateTime.now())
    val selectedLocalTime = mutableStateOf<LocalTime>(LocalTime.now())
    val selectedCategory = mutableStateOf<Category?>(null)

    val durationTime = mutableStateOf<Int?>(null)
    val durationType = mutableStateOf(1)

    val newTxt = mutableStateOf("")
    private val _listWorks = MutableStateFlow<List<TempNote>>(emptyList())
    val listWorks =_listWorks.asStateFlow()

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList =_categoryList.asStateFlow()

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
                    createdAt = LocalDateTime.of(item.zonedDateTime?.toLocalDate(),item.localTime),
                    catID = selectedCategory.value?.id,
                    duration = ((durationTime.value?:0)*durationType.value).toLong()
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

    fun getListCategory(){
        viewModelScope.launch(Dispatchers.IO){
            categoryRepository.fetchAllCategories()
                .distinctUntilChanged()
                .collect{
                    it?.let {cats->
                        _categoryList.value =cats
                    }
                }
        }
    }



}
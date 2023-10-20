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
import com.zareshahi.myreport.database.entrity.NoteWithCategory
import com.zareshahi.myreport.database.repository.CategoryRepository
import com.zareshahi.myreport.util.PersianDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDateTime
import java.time.ZoneId

class AddNewReportViewModel(val noteRepository: NoteRepository,
                            val categoryRepository: CategoryRepository): ViewModel() {
    val persianDateTime: PersianDateTime? by inject(PersianDateTime::class.java)
    val isShowDatePicker = mutableStateOf(false)
    val isShowTimePicker = mutableStateOf(false)
    val isShowDeleteNoteDialog = mutableStateOf(false)
    val selectedNoteCategoryForEdit = mutableStateOf<NoteWithCategory?>(null)

    val isEditMode = mutableStateOf(false)

    val selectedDate = mutableStateOf(persianDateTime?.getCurrentDate()?:"")
    val selectedTime = mutableStateOf(persianDateTime?.getCurrentTime()?:"")
    val selectedZoneDateTime = mutableStateOf<ZonedDateTime>(ZonedDateTime.now())
    val selectedLocalTime = mutableStateOf<LocalTime>(LocalTime.now())
    val selectedCategory = mutableStateOf<Category?>(null)

    val durationMinuteTime = mutableStateOf("")
    val durationHoursTime = mutableStateOf("")

    val newTxt = mutableStateOf("")
    private val _listWorks = MutableStateFlow<List<TempNote>>(emptyList())
    val listWorks =_listWorks.asStateFlow()

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList =_categoryList.asStateFlow()

    fun addNewWork(){
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
                    duration = "${durationHoursTime.value}:${durationMinuteTime.value}"
                )
                noteRepository.addNewNote(note)
            }
        }
    }

    fun edit(){
        viewModelScope.launch(Dispatchers.IO){
            selectedNoteCategoryForEdit.value?.let {note1->
                val noteEdited =note1.note.apply {
                    note = newTxt.value
                    createdAt = LocalDateTime.of(selectedZoneDateTime.value.toLocalDate(),selectedLocalTime.value)
                    catID = selectedCategory.value?.id
                    duration = "${durationHoursTime.value}:${durationMinuteTime.value}"
                }
                noteRepository.updateNote(noteEdited)
            }

        }
    }

    fun delete(){
        viewModelScope.launch(Dispatchers.IO){
            selectedNoteCategoryForEdit.value?.let {
                noteRepository.deleteNote(it.note)
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

    fun getReportForEdit(id:Long){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.fetchNoteWithCategoryByID(id)
                .distinctUntilChanged()
                .collect{
                    it?.let {note->
                        selectedNoteCategoryForEdit.value =note
                        isEditMode.value =true
                        newTxt.value =note.note.note
                        selectedDate.value =persianDateTime?.convertDateToPersianDate(note.note.createdAt)?:""
                        selectedTime.value =persianDateTime?.convertDateToPersianTime(note.note.createdAt)?:""
                        selectedZoneDateTime.value =ZonedDateTime.of(note.note.createdAt, ZoneId.systemDefault())
                        selectedLocalTime.value =note.note.createdAt.toLocalTime()
                        selectedCategory.value =note.category
                        val durations =note.note.duration?.split(":")
                        durationMinuteTime.value =durations?.getOrNull(1)?:""
                        durationHoursTime.value =durations?.getOrNull(0)?:""

                    }
                }
        }
    }



}
package com.zareshahi.myreport.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeViewModel(val noteRepository: NoteRepository):ViewModel() {
    private val _reportList = MutableStateFlow<List<Note>>(emptyList())
    val reportList =_reportList.asStateFlow()

    val isShowBottomSheet = mutableStateOf(false)

    val inputText = mutableStateOf("")

    fun search(){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.fetchNotesByCatID(1L)
                .distinctUntilChanged()
                .collect{
                    it?.let {reports->
                        _reportList.value =reports
                    }
                }
        }
    }
}
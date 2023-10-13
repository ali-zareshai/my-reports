package com.zareshahi.myreport.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.AppDatabase
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel(val noteRepository: NoteRepository):ViewModel() {
    private val _reportList = MutableStateFlow<List<Note>>(emptyList())
    val reportList =_reportList.asStateFlow()

//    val searchWord = mutableStateOf("")

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
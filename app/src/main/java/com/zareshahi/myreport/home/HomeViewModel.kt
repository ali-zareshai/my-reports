package com.zareshahi.myreport.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.AppDatabase
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel(val noteRepository: NoteRepository):ViewModel() {
    fun test(){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.addNewNote(Note(note = "test33"))
        }
    }
}
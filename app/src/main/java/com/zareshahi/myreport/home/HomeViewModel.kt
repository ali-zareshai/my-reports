package com.zareshahi.myreport.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.AppDatabase
import com.zareshahi.myreport.database.entrity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel(val appDatabase: AppDatabase?):ViewModel() {
    fun test(){
        viewModelScope.launch(Dispatchers.IO){
            Log.e("hhh1",appDatabase?.isOpen.toString())
            appDatabase?.noteDao()?.addNewNote(Note(note = "test22"))
            Log.e("hhh2",appDatabase?.isOpen.toString())
        }
    }
}
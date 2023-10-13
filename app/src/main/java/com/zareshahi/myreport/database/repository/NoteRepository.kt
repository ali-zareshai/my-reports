package com.zareshahi.myreport.database.repository

import com.zareshahi.myreport.database.dao.NoteDao
import com.zareshahi.myreport.database.entrity.Note

class NoteRepository(val noteDao: NoteDao) {
    suspend fun addNewNote(note: Note) =noteDao.addNewNote(note)
}
package com.zareshahi.myreport.database.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.zareshahi.myreport.database.dao.NoteDao
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.entrity.NoteWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class NoteRepository(val noteDao: NoteDao) {
    suspend fun addNewNote(note: Note) =noteDao.addNewNote(note)

    fun fetchNotesByCatID(catID:Long)=noteDao.fetchNotesByCatID(catID)

    fun fetchAllNotes()=noteDao.fetchAllNotes()

    fun fetchNoteWithCategoryByID(id:Long)=noteDao.fetchNoteWithCategoryByID(id)

    suspend fun updateNote(note: Note)=noteDao.updateNote(note)

    suspend fun deleteNote(note: Note)=noteDao.deleteNote(note)

    fun fetchNotes(catID:Long?): Flow<List<NoteWithCategory>?> {
        val query ="""
            SELECT * FROM tb_note 
            WHERE 1 ${if(catID==null) "" else "AND category_id=$catID"} 
            ORDER BY created_at
        """.trimIndent()
        return noteDao.fetchNotes(SimpleSQLiteQuery(query)).flowOn(Dispatchers.IO)
    }
}
package com.zareshahi.myreport.database.repository

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.zareshahi.myreport.database.dao.NoteDao
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.entrity.NoteWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.LocalDateTime

class NoteRepository(val noteDao: NoteDao) {
    suspend fun addNewNote(note: Note) =noteDao.addNewNote(note)

    fun fetchNotesByCatID(catID:Long)=noteDao.fetchNotesByCatID(catID)

    fun fetchAllNotes()=noteDao.fetchAllNotes()

    fun fetchNoteWithCategoryByID(id:Long)=noteDao.fetchNoteWithCategoryByID(id)

    suspend fun updateNote(note: Note)=noteDao.updateNote(note)

    suspend fun deleteNote(note: Note)=noteDao.deleteNote(note)

    fun fetchNotes(catID:Long?, fromDate: LocalDate?, toDate: LocalDate?, searchText:String?): Flow<List<NoteWithCategory>?> {
        val query ="""
            SELECT * FROM tb_note 
            WHERE 1 ${if(catID==null) " AND category_id is null" else " AND category_id=$catID"} 
            ${if (fromDate==null) "" else " AND created_at>='$fromDate'"}
            ${if (toDate==null) "" else " AND created_at<='${toDate.plusDays(1)}'"}
            ${if (searchText.isNullOrBlank()) "" else " AND note like '%$searchText%'"}
            ORDER BY created_at
        """.trimIndent().replace("\\s+".toRegex()," ").trim()
        Log.e("query>>",query)
        return noteDao.fetchNotes(SimpleSQLiteQuery(query)).flowOn(Dispatchers.IO)
    }
}
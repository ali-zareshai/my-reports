package com.zareshahi.myreport.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zareshahi.myreport.database.entrity.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM tb_note WHERE category_id=:cat ORDER BY created_at")
    fun fetchNotesByCatID(cat:Long): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewNote(note: Note)
}
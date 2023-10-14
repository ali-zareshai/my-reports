package com.zareshahi.myreport.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.entrity.NoteWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM tb_note WHERE category_id=:cat ORDER BY created_at")
    fun fetchNotesByCatID(cat:Long): Flow<List<Note>?>

    @Query("SELECT * FROM tb_note ORDER BY created_at")
    fun fetchAllNotes(): Flow<List<Note>?>

    @RawQuery(observedEntities = [Note::class])
    fun fetchNotes(simpleSQLiteQuery: SimpleSQLiteQuery):Flow<List<NoteWithCategory>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)
}
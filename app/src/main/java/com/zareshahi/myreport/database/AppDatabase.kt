package com.zareshahi.myreport.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zareshahi.myreport.database.dao.NoteDao
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.util.Converters

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase:RoomDatabase() {
    abstract fun noteDao():NoteDao

}
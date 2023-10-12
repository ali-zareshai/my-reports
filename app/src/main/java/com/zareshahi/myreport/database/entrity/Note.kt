package com.zareshahi.myreport.database.entrity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tb_note")
data class Note constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:Long=0L,
    @ColumnInfo(name = "category_id")
    var catID:Long=1L,
    @ColumnInfo(name = "note")
    var note:String,
    @ColumnInfo(name="created_at")
    var createdAt:LocalDateTime= LocalDateTime.now()
)
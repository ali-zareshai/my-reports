package com.zareshahi.myreport.database.entrity

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithCategory(
    @Embedded
    var note: Note,
    @Relation(parentColumn = "category_id",  entityColumn= "id")
    var category: Category?
)

package com.zareshahi.myreport.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zareshahi.myreport.database.entrity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM tb_category")
    fun fetchAllCategories(): Flow<List<Category>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}
package com.zareshahi.myreport.database.repository

import com.zareshahi.myreport.database.dao.CategoryDao
import com.zareshahi.myreport.database.entrity.Category

class CategoryRepository(val categoryDao: CategoryDao) {
    fun fetchAllCategories()=categoryDao.fetchAllCategories()
    suspend fun addNewCategory(category: Category)=categoryDao.addNewCategory(category)
    suspend fun deleteCategory(category: Category)=categoryDao.deleteCategory(category)
}
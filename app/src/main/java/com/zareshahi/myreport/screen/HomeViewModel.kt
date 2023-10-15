package com.zareshahi.myreport.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zareshahi.myreport.database.entrity.Category
import com.zareshahi.myreport.database.entrity.Note
import com.zareshahi.myreport.database.entrity.NoteWithCategory
import com.zareshahi.myreport.database.repository.CategoryRepository
import com.zareshahi.myreport.database.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(val noteRepository: NoteRepository,val categoryRepository: CategoryRepository):ViewModel() {
    private val _reportList = MutableStateFlow<List<NoteWithCategory>>(emptyList())
    val reportList =_reportList.asStateFlow()

    val isShowCategoryBottomSheet = mutableStateOf(false)
    val isShowDeleteCategory = mutableStateOf(false)
    val isShowSearchBottomSheet = mutableStateOf(false)
    val selectedCategoryForDelete = mutableStateOf<Category?>(null)
    val categoryInputText = mutableStateOf("")

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList =_categoryList.asStateFlow()

    val searchFromDate = mutableStateOf<LocalDate?>(null)
    val searchToDate = mutableStateOf<LocalDate?>(null)
    val searchCategory = mutableStateOf<Category?>(null)
    val searchText = mutableStateOf("")

    fun search(){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.fetchNotes(catID = null)
                .distinctUntilChanged()
                .collect{
                    it?.let {reports->
                        _reportList.value =reports
                    }
                }
        }
    }

    fun saveCategory(){
        viewModelScope.launch(Dispatchers.IO){
            categoryRepository.addNewCategory(Category(name = categoryInputText.value))
            categoryInputText.value = ""
        }
    }

    fun getListCategory(){
        viewModelScope.launch(Dispatchers.IO){
            categoryRepository.fetchAllCategories()
                .distinctUntilChanged()
                .collect{
                    it?.let {cats->
                        _categoryList.value =cats
                    }
                }
        }
    }

    fun deleteCategory(){
        viewModelScope.launch(Dispatchers.IO){
            selectedCategoryForDelete.value?.let {category->
                categoryRepository.deleteCategory(category)
            }
        }
    }
}
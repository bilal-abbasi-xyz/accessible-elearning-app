package com.bilals.elearningapp.ui.contentCreation.browsing.categoryList

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bilals.elearningapp.data.model.CourseCategory
import com.bilals.elearningapp.data.repository.CourseCategoryRepository
import com.bilals.elearningapp.navigation.ScreenRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryListViewModel(
    private val repository: CourseCategoryRepository,
    private val navController: NavController
) : ViewModel() {

    val courseCategories: StateFlow<List<CourseCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun searchCategoryAndNavigate(input: String) {
        val cleanedInput = input.lowercase().trim()

        viewModelScope.launch(Dispatchers.IO) {
            val matchedCategory = repository.searchCategoryByName(cleanedInput)

            if (matchedCategory != null) {
                withContext(Dispatchers.Main) {
                    navController.navigate(
                        ScreenRoutes.CourseList.createRoute(
                            matchedCategory.id,
                            matchedCategory.name
                        )
                    )
                }
            } else {
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.syncCategories()  //  Check if Room is empty, fetch from Firebase if needed
        }
//        repository.listenForUpdates()  //  Start listening for real-time updates
    }
}


package com.bilals.elearningapp.ui.contentCreation.browsing.lecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.repository.LectureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//import com.bilals.elearningapp.data.DummyDataProvider

class ViewLectureViewModel(
    private val repository: LectureRepository,
    lectureId: String
) : ViewModel() {

    // Use MutableStateFlow to hold a single lecture (nullable)
    private val _lecture = MutableStateFlow<Lecture?>(null)
    val lecture: StateFlow<Lecture?> = _lecture

    init {
        // Launch a coroutine to fetch the lecture by ID
        viewModelScope.launch {
            val fetchedLecture = repository.getLectureById(lectureId)
            _lecture.value = fetchedLecture
        }
    }
}


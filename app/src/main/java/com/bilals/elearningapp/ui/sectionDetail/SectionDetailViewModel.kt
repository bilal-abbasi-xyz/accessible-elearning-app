package com.bilals.elearningapp.ui.sectionDetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.bilals.elearningapp.data.DummyDataProvider
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.Resource
import com.bilals.elearningapp.data.model.quiz.Quiz
import com.bilals.elearningapp.data.repository.LectureRepository
import com.bilals.elearningapp.data.repository.QuizRepository
import com.bilals.elearningapp.data.repository.ResourceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//import com.bilals.elearningapp.navigation.NavDataManager

class SectionDetailViewModel(
    private val lectureRepository: LectureRepository,  // Lecture repository
    private val quizRepository: QuizRepository,      // Quiz repository
    private val resourceRepository: ResourceRepository,  // Resource repository
    private val sectionId: String  // The section ID
) : ViewModel() {

    // StateFlow for lectures, quizzes, and resources
    val lectures: StateFlow<List<Lecture>> = lectureRepository.getLectures(sectionId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//
    val quizzes: StateFlow<List<Quiz>> = quizRepository.getQuizzes(sectionId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val resources: StateFlow<List<Resource>> = resourceRepository.getResources(sectionId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Fetch lectures, quizzes, and resources and store them if needed
        viewModelScope.launch {
            lectureRepository.syncLectures(sectionId)
            quizRepository.syncQuizzes(sectionId)
            resourceRepository.syncResources(sectionId)
        }

        // Listen for lecture, quiz, and resource updates in real-time
//        lectureRepository.listenForLectureUpdates(sectionId)
//        quizRepository.listenForQuizUpdates(sectionId)
//        resourceRepository.listenForResourceUpdates(sectionId)
    }
}

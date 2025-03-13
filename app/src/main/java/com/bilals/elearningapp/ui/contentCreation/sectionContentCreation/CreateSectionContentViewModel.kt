package com.bilals.elearningapp.ui.contentCreation.sectionContentCreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.LectureType
import com.bilals.elearningapp.data.model.Resource
import com.bilals.elearningapp.data.model.quiz.Quiz
import com.bilals.elearningapp.data.repository.LectureRepository
import com.bilals.elearningapp.data.repository.QuizRepository
import com.bilals.elearningapp.data.repository.ResourceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SectionContentViewModel(
    private val quizRepository: QuizRepository,
    private val lectureRepository: LectureRepository,
    private val resourceRepository: ResourceRepository,
    private val sectionId: String
) : ViewModel() {

    val quizzes: StateFlow<List<Quiz>> = quizRepository.getQuizzes(sectionId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val lectures: StateFlow<List<Lecture>> = lectureRepository.getLectures(sectionId)
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
    }

    fun createNewQuiz(quizName: String) {
        val newQuiz = Quiz(
            id = UUID.randomUUID().toString(),
            sectionId = sectionId,
            name = quizName
        )

        viewModelScope.launch {
            quizRepository.addQuiz(newQuiz)
        }
    }

    fun createNewLecture(lectureName: String) {
        val newLecture = Lecture(
            id = UUID.randomUUID().toString(),
            sectionId = sectionId,
            name = lectureName,
            content = "",
            type = LectureType.TEXT
        )

        viewModelScope.launch {
            lectureRepository.addLecture(newLecture)
        }
    }

    fun createNewResource(resourceName: String) {
        val newResource = Resource(
            id = UUID.randomUUID().toString(),
            sectionId = sectionId,
            name = resourceName,
            filePath = ""
        )

        viewModelScope.launch {
            resourceRepository.addResource(newResource)
        }
    }


}

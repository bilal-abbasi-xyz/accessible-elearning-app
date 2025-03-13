package com.bilals.elearningapp.ui.contentCreation.courseCreation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import com.bilals.elearningapp.data.DummyDataProvider
import com.bilals.elearningapp.data.model.Section
//import com.bilals.elearningapp.navigation.NavDataManager

class CourseCreationViewModel : ViewModel() {

    private val _courseName = MutableLiveData("")
    val courseName: LiveData<String> get() = _courseName

    private val _sections = MutableLiveData<List<Section>>()
    val sections: LiveData<List<Section>> get() = _sections

    private var courseId: String? = null

    init {
//        val courseId = NavDataManager.selectedCourseId.value
        if (courseId != null) {
//            _sections.value = DummyDataProvider().getSectionsByCourseId(courseId)
        }
    }

    private fun loadCourseDetails() {
        courseId?.let {
//            val course = DummyDataProvider().getCourseById(it)
//            _courseName.value = course?.name ?: ""
//            _sections.value = course?.sections ?: emptyList()
        }
    }

    fun addSection(sectionName: String) {
//        val courseId = NavDataManager.selectedCourseId.value
        if (courseId != null) {
//            val newSection = Section(
//                id = UUID.randomUUID().toString(), // Generate a unique ID
//                name = sectionName
//            )

            // Fetch the course from DummyDataProvider and add the new section
//            DummyDataProvider().addSectionToCourse(courseId, newSection.name)

            // Update LiveData to trigger recomposition
//            _sections.value = DummyDataProvider().getSectionsByCourseId(courseId)
        }
    }
}

package com.bilals.elearningapp.ui.contentCreation.browsing.courseDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.bilals.elearningapp.data.DummyDataProvider
import com.bilals.elearningapp.data.model.Section
import com.bilals.elearningapp.data.repository.SectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//import com.bilals.elearningapp.navigation.NavDataManager
class CourseDetailViewModel(
    private val sectionRepository: SectionRepository,  // Section repository
    private val courseId: String  // The course ID
) : ViewModel() {

    // StateFlow for sections
    val sections: StateFlow<List<Section>> = sectionRepository.getSections(courseId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {


        // Fetch sections and store them if needed
        viewModelScope.launch {
            sectionRepository.syncSections(courseId)
        }

        // Listen for section updates in real-time
//        sectionRepository.listenForSectionUpdates(courseId)
    }


}

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.Section
import com.bilals.elearningapp.data.repository.SectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CreateSectionViewModel(
    private val sectionRepository: SectionRepository,
    private val courseRepository: CourseRepository,
    private val courseId: String
) : ViewModel() {

    val sections: StateFlow<List<Section>> = sectionRepository.getSections(courseId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createSection(sectionName: String) {
        viewModelScope.launch {
            val newSection = Section(
                id = UUID.randomUUID().toString(),
                name = sectionName,
                courseId = courseId
            )
            sectionRepository.addSection(newSection) // Saves to Firestore & Room
        }
    }

    fun publishCourse(courseId: String) {
        viewModelScope.launch {
            val course = courseRepository.getCourseById(courseId)
            if (course != null) {
                val updatedCourse = course.copy(isPublished = true)
                courseRepository.updateCourse(updatedCourse)
            }
        }
    }

    init {
        viewModelScope.launch {
            sectionRepository.syncSections(courseId)  // ✅ Check Room, fetch from Firebase if needed
        }
    }
}

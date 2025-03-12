import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.Course
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseListViewModel(
    private val repository: CourseRepository,
    categoryId: String
) : ViewModel() {

    val courses: StateFlow<List<Course>> = repository.getCourses(categoryId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            repository.syncCourses(categoryId)  // ✅ Check Room, fetch from Firebase if needed
        }
//        repository.listenForUpdates(categoryId)  // ✅ Start real-time updates
    }
}

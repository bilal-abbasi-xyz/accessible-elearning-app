import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.CourseDao
import com.bilals.elearningapp.data.model.Course
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.bilals.elearningapp.stt.TextMatchingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CourseRepository(
    private val courseDao: CourseDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) // ✅ Initialize DatabaseSyncManager

    fun getCourses(categoryId: String): Flow<List<Course>> {
        return courseDao.getCoursesByCategory(categoryId)
    }

    fun getAllCourses(): Flow<List<Course>> {
        return courseDao.getAllCourses()
    }

    suspend fun syncCourses(categoryId: String) {
        dbSyncManager.syncCourses(categoryId)
    }

    suspend fun searchCourseByName(input: String): Course? {
        val originalInput = input.lowercase()
        val normalizedInput = TextMatchingUtils.replaceSynonyms(originalInput)

        val allCourses = courseDao.getAllCourses().firstOrNull() ?: emptyList()

        allCourses.firstOrNull { it.name.lowercase() == originalInput }?.let { return it }
        allCourses.firstOrNull { it.name.lowercase().contains(originalInput) }?.let { return it }

        for (course in allCourses) {
            val courseNameWithSynonyms = TextMatchingUtils.replaceSynonyms(course.name.lowercase())
            if (courseNameWithSynonyms.contains(normalizedInput)) return course
        }

        val bestMatch = allCourses.minByOrNull {
            TextMatchingUtils.levenshteinDistance(it.name.lowercase(), normalizedInput)
        }

        return if (bestMatch != null &&
            TextMatchingUtils.levenshteinDistance(bestMatch.name.lowercase(), normalizedInput) <= 3
        ) {
            bestMatch
        } else {
            null
        }
    }

    suspend fun updateCourse(course: Course) {
        firebaseService.updateCourse(course.categoryId, course)
    }

    suspend fun getCourseById(courseId: String): Course? {
        return courseDao.getCourseById(courseId)
    }


    fun listenForUpdates(categoryId: String) {
        firebaseService.listenForCourseUpdates(categoryId) { courses ->
            CoroutineScope(Dispatchers.IO).launch {
                courseDao.clearCoursesByCategory(categoryId) // ✅ Remove old data
                courseDao.insertCourses(courses)  // ✅ Insert new data
            }
        }
    }
}

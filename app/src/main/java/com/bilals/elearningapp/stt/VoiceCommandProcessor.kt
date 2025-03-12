package com.bilals.elearningapp.stt

import CourseRepository
import android.widget.Toast
import androidx.navigation.NavController
import com.bilals.elearningapp.data.repository.CourseCategoryRepository
import com.bilals.elearningapp.data.repository.LectureRepository
import com.bilals.elearningapp.data.repository.QuizRepository
import com.bilals.elearningapp.data.repository.ResourceRepository
import com.bilals.elearningapp.data.repository.SectionRepository
import com.bilals.elearningapp.navigation.ScreenRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoiceCommandProcessor(
    private val navController: NavController,
    private val categoryRepository: CourseCategoryRepository,
    private val courseRepository: CourseRepository,
    private val sectionRepository: SectionRepository,
    private val quizRepository: QuizRepository,
    private val lectureRepository: LectureRepository,
    private val resourceRepository: ResourceRepository
) {
    fun processCommand(spokenText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // First, search for a category
            val category = categoryRepository.searchCategoryByName(spokenText)

            if (category != null) {
                withContext(Dispatchers.Main) {
                    navController.navigate(
                        ScreenRoutes.CourseList.createRoute(
                            category.id, category.name
                        )
                    )
                }
                return@launch // ✅ If category found, stop execution here
            }

            // If no category found, search for a course
            val course = courseRepository.searchCourseByName(spokenText)
            if (course != null) {
                // Get the category name from the categoryId

                withContext(Dispatchers.Main) {
                    navController.navigate(
                        ScreenRoutes.CourseDetail.createRoute(
                            course.id, course.name
                        )
                    )

                }
                return@launch // ✅ If course found, stop execution here
            }

            // If no course found, search for a section
            val section = sectionRepository.searchSectionByName(spokenText)
            if (section != null) {

                withContext(Dispatchers.Main) {
                    navController.navigate(
                        ScreenRoutes.SectionDetail.createRoute(
                            section.id, section.name
                        )
                    )

                }
                return@launch
            }

            // If no section found, search for a quiz
            val quiz = quizRepository.searchQuizByName(spokenText)
            if (quiz != null) {
                withContext(Dispatchers.Main) {
                    navController.navigate(ScreenRoutes.AttemptQuiz.createRoute(quiz.id, quiz.name))
                }
                return@launch
            }

            // If no quiz found, search for a lecture
            val lecture = lectureRepository.searchLectureByName(spokenText)
            if (lecture != null) {
                withContext(Dispatchers.Main) {
                    navController.navigate(
                        ScreenRoutes.ViewLecture.createRoute(
                            lecture.id,
                            lecture.name
                        )
                    )


                }
                return@launch
            }

            // If no lecture found, search for a resource
            val resource = resourceRepository.searchResourceByName(spokenText)
            if (resource != null) {
                withContext(Dispatchers.Main) {

                    navController.navigate(
                        ScreenRoutes.ViewResource.createRoute(
                            resource.id, resource.name
                        )
                    )

                }
                return@launch
            }

            // If no category, course, section, quiz, lecture, or resource found
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    navController.context, "Sorry, command not recognized.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
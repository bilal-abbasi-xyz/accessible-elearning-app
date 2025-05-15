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
import com.bilals.elearningapp.tts.SpeechService
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
            val categoryKeywords = listOf("category", "categories")
            val courseKeywords = listOf("course", "courses", "core")
            val sectionKeywords = listOf("section", "sections")
            val quizKeywords = listOf("quiz", "quizzes", "test", "assignment")
            val lectureKeywords = listOf("lecture", "lectures")
            val resourceKeywords = listOf("resource", "resources")

            val cleanedText = removeKeywords(
                spokenText,
                categoryKeywords + courseKeywords + sectionKeywords + quizKeywords + lectureKeywords + resourceKeywords
            )

            // Now, search with cleaned text
            val searchResult = when {
                categoryKeywords.any {
                    spokenText.contains(
                        it, ignoreCase = true
                    )
                } -> searchCategory(cleanedText)

                courseKeywords.any { spokenText.contains(it, ignoreCase = true) } -> searchCourse(
                    cleanedText
                )

                sectionKeywords.any { spokenText.contains(it, ignoreCase = true) } -> searchSection(
                    cleanedText
                )

                quizKeywords.any { spokenText.contains(it, ignoreCase = true) } -> searchQuiz(
                    cleanedText
                )

                lectureKeywords.any { spokenText.contains(it, ignoreCase = true) } -> searchLecture(
                    cleanedText
                )

                resourceKeywords.any {
                    spokenText.contains(
                        it, ignoreCase = true
                    )
                } -> searchResource(cleanedText)

                else -> searchGeneral(spokenText) // Default search if no specific keyword found
            }

            // Inside the CoroutineScope(Dispatchers.IO).launch block:
            searchResult?.let { route ->
                // Ensure navigation is on the main thread
                withContext(Dispatchers.Main) {
                    val currentRoute = navController.currentDestination?.route

                    // If the route contains parameters, extract only the base path before checking
                    val normalizedCurrentRoute = currentRoute?.substringBefore("/{")

                    if (normalizedCurrentRoute != route.substringBefore("/{")) {
                        navController.navigate(route) {
                            launchSingleTop =
                                true // Prevent multiple instances of the same destination
                            restoreState = true     // Restore state if available
                        }
                    }
                }
            } ?: run {
                // Determine the appropriate error message based on spokenText
                val errorMessage = when {
                    spokenText.contains("lecture", ignoreCase = true) -> "Sorry, no lecture found."
                    spokenText.contains("course", ignoreCase = true) -> "Sorry, no course found."
                    spokenText.contains("section", ignoreCase = true) -> "Sorry, no section found."
                    spokenText.contains("quiz", ignoreCase = true) -> "Sorry, no quiz found."
                    spokenText.contains(
                        "resource", ignoreCase = true
                    ) -> "Sorry, no resource found."

                    spokenText.contains(
                        "category", ignoreCase = true
                    ) -> "Sorry, no category found."

                    else -> "Sorry, command not recognized."
                }

                // Ensure Toast is shown on the main thread
                withContext(Dispatchers.Main) {
//                    SpeechService.announce(navController.context, errorMessage)
                    Toast.makeText(navController.context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    suspend fun searchCategory(spokenText: String): String? {
        val category = categoryRepository.searchCategoryByName(spokenText)
        return category?.let { ScreenRoutes.CourseList.createRoute(it.id, it.name) }
    }

    suspend fun searchCourse(spokenText: String): String? {
        val course = courseRepository.searchCourseByName(spokenText)
        return course?.let { ScreenRoutes.CourseDetail.createRoute(it.id, it.name) }
    }

    suspend fun searchSection(spokenText: String): String? {
        val section = sectionRepository.searchSectionByName(spokenText)
        return section?.let { ScreenRoutes.SectionDetail.createRoute(it.id, it.name) }
    }

    suspend fun searchQuiz(spokenText: String): String? {
        val quiz = quizRepository.searchQuizByName(spokenText)
        return quiz?.let { ScreenRoutes.AttemptQuiz.createRoute(it.id, it.name) }
    }

    suspend fun searchLecture(spokenText: String): String? {
        val lecture = lectureRepository.searchLectureByName(spokenText)
        return lecture?.let { ScreenRoutes.ViewLecture.createRoute(it.id, it.name) }
    }

    suspend fun searchResource(spokenText: String): String? {
        val resource = resourceRepository.searchResourceByName(spokenText)
        return resource?.let { ScreenRoutes.ViewResource.createRoute(it.id, it.name) }
    }

    suspend fun searchGeneral(spokenText: String): String? {
        val category = categoryRepository.searchCategoryByName(spokenText)
        return category?.let { ScreenRoutes.CourseList.createRoute(it.id, it.name) }
            ?: courseRepository.searchCourseByName(spokenText)
                ?.let { ScreenRoutes.CourseDetail.createRoute(it.id, it.name) }
            ?: sectionRepository.searchSectionByName(spokenText)
                ?.let { ScreenRoutes.SectionDetail.createRoute(it.id, it.name) }
            ?: quizRepository.searchQuizByName(spokenText)
                ?.let { ScreenRoutes.AttemptQuiz.createRoute(it.id, it.name) }
            ?: lectureRepository.searchLectureByName(spokenText)
                ?.let { ScreenRoutes.ViewLecture.createRoute(it.id, it.name) }
            ?: resourceRepository.searchResourceByName(spokenText)
                ?.let { ScreenRoutes.ViewResource.createRoute(it.id, it.name) }
    }

    // Helper function to remove the keywords from spokenText
    fun removeKeywords(spokenText: String, keywords: List<String>): String {
        var cleanedText = spokenText
        for (keyword in keywords) {
            cleanedText = cleanedText.replace(Regex("(?i)\\b$keyword\\b"), "").trim()
        }
        return cleanedText
    }

}
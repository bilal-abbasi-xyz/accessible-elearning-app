package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.QuizDao
import com.bilals.elearningapp.data.model.quiz.Quiz
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.bilals.elearningapp.stt.TextMatchingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class QuizRepository(
    private val quizDao: QuizDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) // ✅ Initialize DatabaseSyncManager

    // Fetch quizzes by sectionId from Room (local database)
    fun getQuizzes(sectionId: String): Flow<List<Quiz>> {
        return quizDao.getQuizzesBySection(sectionId)
    }

    suspend fun addQuiz(quiz: Quiz) {
        // Save to Firestore
        firebaseService.addQuiz(quiz)

        // Save to Room (for offline support)
        quizDao.insertQuizzes(listOf(quiz))
    }


    suspend fun searchQuizByName(input: String): Quiz? {
        val originalInput = input.lowercase()
        val normalizedInput = TextMatchingUtils.replaceSynonyms(originalInput)

        val allQuizzes = quizDao.getAllQuizzes().firstOrNull() ?: emptyList()

        // First, check for an exact match
        allQuizzes.firstOrNull { it.name.lowercase() == originalInput }?.let { return it }

        // Next, check for a partial match (contains)
        allQuizzes.firstOrNull { it.name.lowercase().contains(originalInput) }?.let { return it }

        // Check for a match with synonyms
        for (quiz in allQuizzes) {
            val quizNameWithSynonyms = TextMatchingUtils.replaceSynonyms(quiz.name.lowercase())
            if (quizNameWithSynonyms.contains(normalizedInput)) return quiz
        }

        // If no match found, use Levenshtein distance to find the closest match
        val bestMatch = allQuizzes.minByOrNull {
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


    // Fetch and store quizzes if they are not already in the local Room database
    suspend fun syncQuizzes(sectionId: String) {
        dbSyncManager.syncQuizzes(sectionId)
    }

    // Listen for quiz updates in real-time from Firebase and update Room
    fun listenForQuizUpdates(sectionId: String) {
        firebaseService.listenForQuizUpdates(sectionId) { quizzes ->
            CoroutineScope(Dispatchers.IO).launch {
                quizDao.clearQuizzesBySection(sectionId) // Remove old data
                quizDao.insertQuizzes(quizzes)  // Insert new quizzes
            }
        }
    }



}

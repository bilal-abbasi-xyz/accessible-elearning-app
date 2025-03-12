package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.QuestionDao
import com.bilals.elearningapp.data.model.quiz.Question
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class QuestionRepository(
    private val questionDao: QuestionDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) // ✅ Initialize DatabaseSyncManager

    // Fetch questions by quizId from Room (local database)
    fun getQuestions(quizId: String): Flow<List<Question>> {
        return questionDao.getQuestionsByQuiz(quizId)
    }

    suspend fun addQuestion(question: Question) {
        // Save to Firestore
        firebaseService.addQuestion(question)

        // Save to Room (for offline support)
        questionDao.insertQuestions(listOf(question))
    }


    // Fetch and store questions if they are not already in the local Room database
    suspend fun syncQuestions(quizId: String) {
        dbSyncManager.syncQuestions(quizId)
    }


}

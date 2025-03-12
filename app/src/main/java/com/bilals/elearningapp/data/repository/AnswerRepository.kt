package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.AnswerDao
import com.bilals.elearningapp.data.model.quiz.Answer
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import kotlinx.coroutines.flow.Flow

class AnswerRepository(
    private val answerDao: AnswerDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) // Initialize DatabaseSyncManager

    // Fetch answers by questionId from Room (local database)
    fun getAnswers(questionId: String): Flow<List<Answer>> {
        return answerDao.getAnswersByQuestion(questionId)
    }

    suspend fun addAnswer(answer: Answer) {
        // Save to Firestore
        firebaseService.addAnswer(answer)

        // Save to Room (for offline support)
        answerDao.insertAnswers(listOf(answer))
    }


    // Sync answers for a given question from remote data source (Firebase)
    suspend fun syncAnswers(questionId: String) {
        dbSyncManager.syncAnswers(questionId)
    }


}

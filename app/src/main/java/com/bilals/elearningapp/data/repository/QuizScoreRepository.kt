package com.bilals.elearningapp.data.repository

import com.bilals.elearningapp.data.local.QuizScoreDao
import com.bilals.elearningapp.data.model.quiz.QuizScore
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton

class QuizScoreRepository(
    private val quizScoreDao: QuizScoreDao
) {
    private val firebaseService = FirebaseServiceSingleton.instance

    suspend fun updateQuizScore(userId: String, quizId: String, score: Int, maxPoints: Int) {
        val quizScore =
            QuizScore(quizId = quizId, userId = userId, score = score, maxPoints = maxPoints)

        // Update Firestore
        firebaseService.updateQuizScore(userId, quizId, score, maxPoints)

        // Update Room Database
        quizScoreDao.insertOrUpdate(quizScore)
    }

    suspend fun getQuizScore(userId: String, quizId: String): QuizScore? {
        return quizScoreDao.getQuizScore(userId, quizId)
    }
}

package com.bilals.elearningapp.data.local

import androidx.room.*
import com.bilals.elearningapp.data.model.quiz.QuizScore

@Dao
interface QuizScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(quizScore: QuizScore)

    @Query("SELECT * FROM quiz_scores WHERE userId = :userId AND quizId = :quizId")
    suspend fun getQuizScore(userId: String, quizId: String): QuizScore?

    @Query("SELECT * FROM quiz_scores WHERE userId = :userId")
    suspend fun getAllQuizScoresForUser(userId: String): List<QuizScore>

    @Query("DELETE FROM quiz_scores WHERE userId = :userId AND quizId = :quizId")
    suspend fun deleteQuizScore(userId: String, quizId: String)
}

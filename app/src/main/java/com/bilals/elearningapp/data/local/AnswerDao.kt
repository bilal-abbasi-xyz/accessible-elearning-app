package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bilals.elearningapp.data.model.quiz.Answer
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {

    // Fetch answers by questionId
    @Query("SELECT * FROM answers WHERE questionId = :questionId")
    fun getAnswersByQuestion(questionId: String): Flow<List<Answer>>

    // Get the count of answers by questionId
    @Query("SELECT COUNT(*) FROM answers WHERE questionId = :questionId")
    suspend fun getAnswerCount(questionId: String): Int

    // Insert answers into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<Answer>)

    // Clear all answers by questionId
    @Query("DELETE FROM answers WHERE questionId = :questionId")
    suspend fun clearAnswersByQuestion(questionId: String)

    // Get answer by ID
    @Query("SELECT * FROM answers WHERE id = :answerId")
    suspend fun getAnswerById(answerId: String): Answer?

    @Delete
    suspend fun deleteAnswers(answers: List<Answer>)

    @Update
    suspend fun updateAnswer(answer: Answer)
}


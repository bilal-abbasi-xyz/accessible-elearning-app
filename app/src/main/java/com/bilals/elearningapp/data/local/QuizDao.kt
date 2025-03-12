package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilals.elearningapp.data.model.quiz.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    // Fetch quizzes by sectionId
    @Query("SELECT * FROM quizzes WHERE sectionId = :sectionId")
    fun getQuizzesBySection(sectionId: String): Flow<List<Quiz>>

    // Get the count of quizzes by sectionId
    @Query("SELECT COUNT(*) FROM quizzes WHERE sectionId = :sectionId")
    suspend fun getQuizCount(sectionId: String): Int

    // Insert quizzes into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<Quiz>)

    // Clear all quizzes by sectionId
    @Query("DELETE FROM quizzes WHERE sectionId = :sectionId")
    suspend fun clearQuizzesBySection(sectionId: String)

    // Get quiz by ID
    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizById(quizId: String): Quiz?

    @Delete
    suspend fun deleteQuizzes(quizzes: List<Quiz>)

    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): Flow<List<Quiz>>
}

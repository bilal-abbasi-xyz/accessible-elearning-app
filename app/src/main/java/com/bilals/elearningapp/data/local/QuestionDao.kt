package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.quiz.Question
import com.bilals.elearningapp.data.model.quiz.Quiz
import kotlinx.coroutines.flow.Flow


@Dao
interface QuestionDao {

    // Fetch questions by quizId
    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    fun getQuestionsByQuiz(quizId: String): Flow<List<Question>>

    // Get the count of questions by quizId
    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionCount(quizId: String): Int

    // Insert questions into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    // Clear all questions by quizId
    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun clearQuestionsByQuiz(quizId: String)

    // Get question by ID
    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionById(questionId: String): Question?

    @Delete
    suspend fun deleteQuestions(questions: List<Question>)
}

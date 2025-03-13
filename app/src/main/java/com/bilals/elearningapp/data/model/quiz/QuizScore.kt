package com.bilals.elearningapp.data.model.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_scores")
data class QuizScore(
    @PrimaryKey val quizId: String = "",  // Unique quiz ID
    val userId: String = "",              // The user who attempted the quiz
    val score: Int = 0,                  // The score obtained
    val maxPoints: Int = 0               // Total possible points for that quiz
)

package com.bilals.elearningapp.data.model.quiz
import java.io.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Answer(
    @PrimaryKey val id: String = "",
    val text: String = "",
    val isCorrect: Boolean = false,
    val questionId: String = ""// Foreign key
)

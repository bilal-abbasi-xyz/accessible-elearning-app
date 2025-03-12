package com.bilals.elearningapp.data.model.quiz
import java.io.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.bilals.elearningapp.data.model.Section

@Entity(
    tableName = "quizzes",
    foreignKeys = [
        ForeignKey(
            entity = Section::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Quiz(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val sectionId: String = "" // Foreign key
)


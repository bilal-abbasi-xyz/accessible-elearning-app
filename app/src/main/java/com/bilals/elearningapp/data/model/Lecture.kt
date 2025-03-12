package com.bilals.elearningapp.data.model
import java.io.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lectures",
    foreignKeys = [
        ForeignKey(
            entity = Section::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Lecture(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val content: String = "", // Markdown for text OR URL for audio
    val type: LectureType = LectureType.TEXT,
    val sectionId: String = "" // Foreign key
)

enum class LectureType {
    TEXT, AUDIO
}

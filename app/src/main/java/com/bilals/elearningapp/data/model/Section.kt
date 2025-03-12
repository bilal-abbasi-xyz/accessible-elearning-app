package com.bilals.elearningapp.data.model
import java.io.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Section(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val courseId: String = "" // Foreign key
)

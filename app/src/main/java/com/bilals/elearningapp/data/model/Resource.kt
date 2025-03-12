package com.bilals.elearningapp.data.model
import java.io.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    foreignKeys = [
        ForeignKey(
            entity = Section::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Resource(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val filePath: String = "",
    val sectionId: String = ""// Foreign key
)

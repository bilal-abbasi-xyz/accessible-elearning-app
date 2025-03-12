package com.bilals.elearningapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_categories")
data class CourseCategory(
    @PrimaryKey val id: String = "",  // Default value for id
    val name: String = ""            // Default value for name
)

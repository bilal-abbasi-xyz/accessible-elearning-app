package com.bilals.elearningapp.data.model

import java.io.Serializable

data class Grade(
    val studentId: String,
    val courseId: String,
    val totalPoints: Int
) : Serializable
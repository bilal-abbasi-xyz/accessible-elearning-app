package com.bilals.elearningapp.data.model.user

import java.io.Serializable

data class StudentProfile(
    val enrolledCourses: List<String> = emptyList()
) : Serializable

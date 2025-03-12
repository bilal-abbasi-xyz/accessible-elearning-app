package com.bilals.elearningapp.data.model.user

import java.io.Serializable

data class InstructorProfile(
    val createdCourses: List<String> = emptyList() // Courses the instructor has created
) : Serializable

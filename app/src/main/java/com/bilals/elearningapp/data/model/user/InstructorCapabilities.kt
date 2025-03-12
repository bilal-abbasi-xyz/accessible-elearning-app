package com.bilals.elearningapp.data.model.user

class InstructorCapabilities(
    val createdCourses: MutableList<String> = mutableListOf()
) : RoleCapabilities {

    fun createCourse(courseName: String) {
        createdCourses.add(courseName)
    }

    fun requestCourseApproval(courseName: String) {
        // Logic to request approval for the course
    }

    fun accessFeedback(courseName: String): List<String> {
        // Logic to fetch feedback for the course
        return listOf() // Mock feedback
    }

    override fun performRoleSpecificAction() {
        println("Instructor-specific action performed")
    }
}
package com.bilals.elearningapp.data.model.user

class StudentCapabilities(
    val enrolledCourses: MutableList<String> = mutableListOf()
) : RoleCapabilities {

    fun enrollCourse(courseName: String) {
        enrolledCourses.add(courseName)
    }

    override fun performRoleSpecificAction() {
        println("Student-specific action performed")
    }
}

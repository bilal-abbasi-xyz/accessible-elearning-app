package com.bilals.elearningapp.di

import CourseRepository
import android.content.Context
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.repository.CourseCategoryRepository
import com.bilals.elearningapp.data.repository.LectureRepository
import com.bilals.elearningapp.data.repository.QuizRepository
import com.bilals.elearningapp.data.repository.ResourceRepository
import com.bilals.elearningapp.data.repository.SectionRepository
import com.bilals.elearningapp.data.repository.UserRepository

class AppContainer(context: Context) {
    private val database: ElearningDatabase = ElearningDatabase.getDatabase(context)

    val categoryRepository: CourseCategoryRepository = CourseCategoryRepository(database.courseCategoryDao(), context)
    val courseRepository: CourseRepository = CourseRepository(database.courseDao(), context)
    val sectionRepository: SectionRepository = SectionRepository(database.sectionDao(), context)
    val quizRepository: QuizRepository = QuizRepository(database.quizDao(), context)
    val lectureRepository: LectureRepository = LectureRepository(database.lectureDao(), context)
    val resourceRepository: ResourceRepository = ResourceRepository(database.resourceDao(), context)
    val userRepository: UserRepository = UserRepository(database.userDao())
}

package com.bilals.elearningapp.data.local

import androidx.room.*
import com.bilals.elearningapp.data.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE categoryId = :categoryId")
    fun getCoursesByCategory(categoryId: String): Flow<List<Course>>

    @Query("SELECT COUNT(*) FROM courses WHERE categoryId = :categoryId")
    suspend fun getCourseCount(categoryId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Query("DELETE FROM courses WHERE categoryId = :categoryId")
    suspend fun clearCoursesByCategory(categoryId: String)

    @Delete
    suspend fun deleteCourses(courses: List<Course>)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): Course?

}

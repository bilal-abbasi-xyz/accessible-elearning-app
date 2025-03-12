package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilals.elearningapp.data.model.CourseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseCategoryDao {
    @Query("SELECT * FROM course_categories")
    fun getAllCategories(): Flow<List<CourseCategory>>

    @Query("SELECT COUNT(*) FROM course_categories")
    suspend fun getCategoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CourseCategory>)

    @Query("DELETE FROM course_categories")
    suspend fun clearAll()

    @Query("SELECT * FROM course_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CourseCategory?

    @Delete
    suspend fun deleteCategories(categories: List<CourseCategory>) // ✅ Delete multiple categories
}


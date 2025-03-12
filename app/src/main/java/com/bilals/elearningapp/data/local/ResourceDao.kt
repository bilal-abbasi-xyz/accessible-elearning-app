package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilals.elearningapp.data.model.Resource
import com.bilals.elearningapp.data.model.quiz.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {

    // Fetch resources by sectionId
    @Query("SELECT * FROM resources WHERE sectionId = :sectionId")
    fun getResourcesBySection(sectionId: String): Flow<List<Resource>>

    // Get the count of resources by sectionId
    @Query("SELECT COUNT(*) FROM resources WHERE sectionId = :sectionId")
    suspend fun getResourceCount(sectionId: String): Int

    // Insert resources into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<Resource>)

    // Clear all resources by sectionId
    @Query("DELETE FROM resources WHERE sectionId = :sectionId")
    suspend fun clearResourcesBySection(sectionId: String)

    // Get resource by ID
    @Query("SELECT * FROM resources WHERE id = :resourceId")
    suspend fun getResourceById(resourceId: String): Resource?

    @Delete
    suspend fun deleteResources(resources: List<Resource>)

    @Query("SELECT * FROM resources")
    fun getAllResources(): Flow<List<Resource>>
}

package com.bilals.elearningapp.data.local

import androidx.room.*
import com.bilals.elearningapp.data.model.Section
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    // Fetch all sections by courseId
    @Query("SELECT * FROM sections WHERE courseId = :courseId")
    fun getSectionsByCourse(courseId: String): Flow<List<Section>>

    // Get the count of sections by courseId
    @Query("SELECT COUNT(*) FROM sections WHERE courseId = :courseId")
    suspend fun getSectionCount(courseId: String): Int

    // Insert sections into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<Section>)

    // Clear all sections by courseId
    @Query("DELETE FROM sections WHERE courseId = :courseId")
    suspend fun clearSectionsByCourse(courseId: String)

    // Delete sections from the database
    @Delete
    fun deleteSections(sections: List<Section>)

    @Query("SELECT * FROM sections WHERE name = :sectionName")
    fun getSectionByName(sectionName: String): Flow<List<Section>>


    // Fetch all sections
    @Query("SELECT * FROM sections")
    fun getAllSections(): Flow<List<Section>>

    @Query("SELECT * FROM sections WHERE id = :sectionId")
    suspend fun getSectionById(sectionId: String): Section?
}

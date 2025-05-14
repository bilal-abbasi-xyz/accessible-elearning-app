package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.quiz.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {

    // Fetch lectures by sectionId
    @Query("SELECT * FROM lectures WHERE sectionId = :sectionId")
    fun getLecturesBySection(sectionId: String): Flow<List<Lecture>>

    // Get the count of lectures by sectionId
    @Query("SELECT COUNT(*) FROM lectures WHERE sectionId = :sectionId")
    suspend fun getLectureCount(sectionId: String): Int

    @Update
    suspend fun updateLecture(lecture: Lecture)

    // Insert lectures into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectures(lectures: List<Lecture>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: Lecture)

    // Clear all lectures by sectionId
    @Query("DELETE FROM lectures WHERE sectionId = :sectionId")
    suspend fun clearLecturesBySection(sectionId: String)

    // Get lecture by ID
    @Query("SELECT * FROM lectures WHERE id = :lectureId")
    suspend fun getLectureById(lectureId: String): Lecture?

    // Delete lectures (List)
    @Delete
    suspend fun deleteLectures(lectures: List<Lecture>)

    // Delete a single lecture
    @Delete
    suspend fun deleteLecture(lecture: Lecture)

    // Get all lectures
    @Query("SELECT * FROM lectures")
    fun getAllLectures(): Flow<List<Lecture>>
}


package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.LectureDao
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.bilals.elearningapp.stt.TextMatchingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LectureRepository(
    private val lectureDao: LectureDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) // ✅ Initialize DatabaseSyncManager

    // Fetch lectures by sectionId from Room (local database)
    fun getLectures(sectionId: String): Flow<List<Lecture>> {
        return lectureDao.getLecturesBySection(sectionId)
    }

    suspend fun getLectureById(lectureId: String): Lecture? {
        return lectureDao.getLectureById(lectureId)
    }

    suspend fun addLecture(lecture: Lecture) {
        firebaseService.addLecture(lecture)

        lectureDao.insertLectures(listOf(lecture))
    }


    // Fetch and store lectures if they are not already in the local Room database
    suspend fun syncLectures(sectionId: String) {
        dbSyncManager.syncLectures(sectionId)
    }

    suspend fun searchLectureByName(input: String): Lecture? {
        val originalInput = input.lowercase()
        val normalizedInput = TextMatchingUtils.replaceSynonyms(originalInput)

        val allLectures = lectureDao.getAllLectures().firstOrNull() ?: emptyList()

        // First, check for an exact match
        allLectures.firstOrNull { it.name.lowercase() == originalInput }?.let { return it }

        // Next, check for a partial match (contains)
        allLectures.firstOrNull { it.name.lowercase().contains(originalInput) }?.let { return it }

        // Check for a match with synonyms
        for (lecture in allLectures) {
            val lectureNameWithSynonyms = TextMatchingUtils.replaceSynonyms(lecture.name.lowercase())
            if (lectureNameWithSynonyms.contains(normalizedInput)) return lecture
        }

        // If no match found, use Levenshtein distance to find the closest match
        val bestMatch = allLectures.minByOrNull {
            TextMatchingUtils.levenshteinDistance(it.name.lowercase(), normalizedInput)
        }

        return if (bestMatch != null &&
            TextMatchingUtils.levenshteinDistance(bestMatch.name.lowercase(), normalizedInput) <= 3
        ) {
            bestMatch
        } else {
            null
        }
    }


    // Listen for lecture updates in real-time from Firebase and update Room
    fun listenForLectureUpdates(sectionId: String) {
        firebaseService.listenForLectureUpdates(sectionId) { lectures ->
            CoroutineScope(Dispatchers.IO).launch {
                lectureDao.clearLecturesBySection(sectionId) // Remove old data
                lectureDao.insertLectures(lectures)  // Insert new lectures
            }
        }
    }
}

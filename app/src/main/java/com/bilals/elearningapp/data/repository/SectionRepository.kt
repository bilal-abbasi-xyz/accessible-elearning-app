package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.SectionDao
import com.bilals.elearningapp.data.model.Section
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.bilals.elearningapp.stt.TextMatchingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SectionRepository(
    private val sectionDao: SectionDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) // ✅ Initialize DatabaseSyncManager


    // Fetch sections by courseId from Room (local database)
    fun getSections(courseId: String): Flow<List<Section>> {
        return sectionDao.getSectionsByCourse(courseId)
    }


    suspend fun addSection(section: Section) {
        // Save to Firestore
        firebaseService.addSection(section)

        // Save to Room (for offline support)
        sectionDao.insertSections(listOf(section))
    }

    // Fetch and store sections if they are not already in the local Room database
    suspend fun syncSections(courseId: String) {
        dbSyncManager.syncSections(courseId)
    }

    // Function to locally delete a section by its ID
    suspend fun deleteSectionById(sectionId: String) {
        // Step 1: Get the section by ID
        val section = sectionDao.getSectionById(sectionId)

        // Step 2: If section is found, delete it
        section?.let {
            sectionDao.deleteSections(listOf(it))
        }
    }


    // Function to delete a section by its name
    suspend fun deleteSectionByName(sectionName: String) {
        // Step 1: Get the section(s) by name
        val sections = sectionDao.getSectionByName(sectionName).first() // Collect the flow to get the list

        // Step 2: If sections are found, delete them
        if (sections.isNotEmpty()) {
            sectionDao.deleteSections(sections)
        }
    }


    suspend fun searchSectionByName(input: String): Section? {
        val originalInput = input.lowercase()
        val normalizedInput = TextMatchingUtils.replaceSynonyms(originalInput)

        val allSections = sectionDao.getAllSections().firstOrNull() ?: emptyList()

        // First, check for an exact match
        allSections.firstOrNull { it.name.lowercase() == originalInput }?.let { return it }

        // Next, check for a partial match (contains)
        allSections.firstOrNull { it.name.lowercase().contains(originalInput) }?.let { return it }

        // Check for a match with synonyms
        for (section in allSections) {
            val sectionNameWithSynonyms = TextMatchingUtils.replaceSynonyms(section.name.lowercase())
            if (sectionNameWithSynonyms.contains(normalizedInput)) return section
        }

        // If no match found, use Levenshtein distance to find the closest match
        val bestMatch = allSections.minByOrNull {
            TextMatchingUtils.levenshteinDistance(it.name.lowercase(), normalizedInput)
        }

        // If a close enough match is found, return it
        return if (bestMatch != null &&
            TextMatchingUtils.levenshteinDistance(bestMatch.name.lowercase(), normalizedInput) <= 3
        ) {
            bestMatch
        } else {
            null
        }
    }

    suspend fun getSectionById(sectionId: String): Section? {
        return sectionDao.getSectionById(sectionId)
    }

    // Listen for section updates in real-time from Firebase and update Room
    fun listenForSectionUpdates(courseId: String) {
        firebaseService.listenForSectionUpdates(courseId) { sections ->
            CoroutineScope(Dispatchers.IO).launch {
                sectionDao.clearSectionsByCourse(courseId) // Remove old data
                sectionDao.insertSections(sections)  // Insert new sections
            }
        }
    }
}

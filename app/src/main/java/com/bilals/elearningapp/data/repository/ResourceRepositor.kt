package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.LectureDao
import com.bilals.elearningapp.data.local.ResourceDao
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.Resource
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.bilals.elearningapp.stt.TextMatchingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ResourceRepository(
    private val resourceDao: ResourceDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance
    private val dbSyncManager = DatabaseSyncManager(context) //  Initialize DatabaseSyncManager

    // Fetch resources by sectionId from Room (local database)
    fun getResources(sectionId: String): Flow<List<Resource>> {
        return resourceDao.getResourcesBySection(sectionId)
    }

    // Fetch and store resources if they are not already in the local Room database
    suspend fun syncResources(sectionId: String) {
        dbSyncManager.syncResources(sectionId)
    }

    suspend fun addResource(resource: Resource) {
        // Save to Firestore
        firebaseService.addResource(resource)

        // Save to Room (for offline support)
        resourceDao.insertResources(listOf(resource))
    }


    suspend fun searchResourceByName(input: String): Resource? {
        val originalInput = input.lowercase()
        val normalizedInput = TextMatchingUtils.replaceSynonyms(originalInput)

        val allResources = resourceDao.getAllResources().firstOrNull() ?: emptyList()

        // First, check for an exact match
        allResources.firstOrNull { it.name.lowercase() == originalInput }?.let { return it }

        // Next, check for a partial match (contains)
        allResources.firstOrNull { it.name.lowercase().contains(originalInput) }?.let { return it }

        // Check for a match with synonyms
        for (resource in allResources) {
            val resourceNameWithSynonyms = TextMatchingUtils.replaceSynonyms(resource.name.lowercase())
            if (resourceNameWithSynonyms.contains(normalizedInput)) return resource
        }

        // If no match found, use Levenshtein distance to find the closest match
        val bestMatch = allResources.minByOrNull {
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


    // Listen for resource updates in real-time from Firebase and update Room
    fun listenForResourceUpdates(sectionId: String) {
        firebaseService.listenForResourceUpdates(sectionId) { resources ->
            CoroutineScope(Dispatchers.IO).launch {
                resourceDao.clearResourcesBySection(sectionId) // Remove old data
                resourceDao.insertResources(resources)  // Insert new resources
            }
        }
    }
}

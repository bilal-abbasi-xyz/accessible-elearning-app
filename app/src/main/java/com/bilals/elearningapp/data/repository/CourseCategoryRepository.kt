package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.CourseCategoryDao
import com.bilals.elearningapp.data.model.CourseCategory
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.bilals.elearningapp.stt.TextMatchingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class CourseCategoryRepository(
    private val categoryDao: CourseCategoryDao,
    context: Context
) {
    private val firebaseService = FirebaseServiceSingleton.instance // Use the singleton instance
    private val dbSyncManager = DatabaseSyncManager(context) //  Initialize DatabaseSyncManager

    val allCategories: Flow<List<CourseCategory>> = categoryDao.getAllCategories()

    suspend fun syncCategories() {
        dbSyncManager.syncCategories() //  Call syncCategories from DatabaseSyncManager
    }

    fun listenForUpdates() {
        firebaseService.listenForCategoryUpdates { categories ->
            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.insertCategories(categories)  //  Insert or update changed categories
            }
        }
    }
    suspend fun getCategoryById(categoryId: String): CourseCategory? {
        return categoryDao.getCategoryById(categoryId)
    }

    suspend fun searchCategoryByName(input: String): CourseCategory? {
        val originalInput = input.lowercase()
        val normalizedInput = TextMatchingUtils.replaceSynonyms(originalInput)

        //  Convert Flow<List<CourseCategory>> to List<CourseCategory>
        val allCategories = categoryDao.getAllCategories().firstOrNull() ?: emptyList()

        //  Now, we can use firstOrNull, forEach, minByOrNull correctly
        allCategories.firstOrNull { it.name.lowercase() == originalInput }?.let { return it }
        allCategories.firstOrNull { it.name.lowercase().contains(originalInput) }?.let { return it }

        for (category in allCategories) {
            val categoryNameWithSynonyms =
                TextMatchingUtils.replaceSynonyms(category.name.lowercase())
            if (categoryNameWithSynonyms.contains(normalizedInput)) return category
        }

        //  4. Fuzzy Matching (ONLY if similarity is high enough)
        val bestMatch = allCategories.minByOrNull {
            TextMatchingUtils.levenshteinDistance(it.name.lowercase(), normalizedInput)
        }

        return if (bestMatch != null &&
            TextMatchingUtils.levenshteinDistance(bestMatch.name.lowercase(), normalizedInput) <= 1
        ) {
            // 🎯 Only return if distance is <= 3 (adjust threshold as needed)
            bestMatch
        } else {
            null // ❌ No good match, return null
        }
    }

}


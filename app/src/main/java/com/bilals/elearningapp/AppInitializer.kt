package com.bilals.elearningapp

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppInitializer : Application() {

    override fun onCreate() {
        super.onCreate()

        // Run the sync task when the app starts
        CoroutineScope(Dispatchers.IO).launch {
            syncDatabaseIfNeeded(this@AppInitializer)
        }
    }

    private suspend fun syncDatabaseIfNeeded(context: Context) {

        val databaseSyncManager = DatabaseSyncManager(context)
        // Call the update function to sync the database
        databaseSyncManager.updateDatabaseFromFirebase()

    }
}

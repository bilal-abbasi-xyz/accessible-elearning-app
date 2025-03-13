package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.PublicChatMessageDao
import com.bilals.elearningapp.data.model.PublicChatMessage
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class PublicChatRepository(
    private val publicChatMessageDao: PublicChatMessageDao,
    context: Context
) {
    private val dbSyncManager = DatabaseSyncManager(context) // Handles offline sync
    private val firebaseService = FirebaseServiceSingleton.instance // Firestore access

    // ✅ Sync messages between Firestore & Room (on app launch)
    suspend fun syncPublicChatMessages() {
        dbSyncManager.syncPublicChatMessages()
    }

    // ✅ Get messages from Room (local database)
    fun getMessages(): Flow<List<PublicChatMessage>> {
        return publicChatMessageDao.getMessages()
    }

    // ✅ Fetch messages from Firestore and store in Room
    suspend fun fetchMessages() {
        val messages = firebaseService.getPublicMessages() // Fetch from Firestore
        publicChatMessageDao.insertMessages(messages) // Store in Room DB
    }


    // ✅ Send message (Text or Audio)
    fun sendMessage(message: PublicChatMessage) {
        firebaseService.sendPublicMessage(message) { success ->
            if (success) {
                CoroutineScope(Dispatchers.IO).launch {
                    publicChatMessageDao.insertMessages(listOf(message)) // Save locally
                }
            }
        }
    }

    // ✅ Clear all public messages (if needed)
    suspend fun clearAllMessages() {
        publicChatMessageDao.clearAllMessages()
    }

    // ✅ Listen for real-time updates from Firestore
    fun listenForMessageUpdates() {
        firebaseService.listenForPublicMessages { newMessage, changeType ->
            CoroutineScope(Dispatchers.IO).launch {
                when (changeType) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        publicChatMessageDao.insertMessages(listOf(newMessage)) // Insert/update
                    }
                    DocumentChange.Type.REMOVED -> {
                        publicChatMessageDao.deleteMessageById(newMessage.id) // Remove from Room
                    }
                }
            }
        }
    }

}
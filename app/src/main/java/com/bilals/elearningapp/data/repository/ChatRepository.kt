package com.bilals.elearningapp.data.repository

import android.content.Context
import com.bilals.elearningapp.DatabaseSyncManager
import com.bilals.elearningapp.data.local.ChatMessageDao
import com.bilals.elearningapp.data.model.ChatMessage
import com.bilals.elearningapp.data.remote.FirebaseServiceSingleton
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatRepository(
    private val chatMessageDao: ChatMessageDao,
    context: Context
) {
    private val dbSyncManager = DatabaseSyncManager(context) // ✅ Initialize DatabaseSyncManager

    private val firebaseService = FirebaseServiceSingleton.instance

    suspend fun syncChatMessages(courseId: String) {
        dbSyncManager.syncChatMessages(courseId)
    }

    // ✅ Get messages from Room (local database)
    fun getMessages(courseId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessages(courseId)
    }

    // ✅ Fetch messages from Firestore and store in Room
    suspend fun fetchMessages(courseId: String) {
        val messages = firebaseService.getMessages(courseId) // Get from Firestore
        chatMessageDao.insertMessages(messages) // Store in Room DB
    }

    fun listenForMessageUpdates(courseId: String) {
        firebaseService.listenForMessages(courseId) { newMessage, changeType ->
            CoroutineScope(Dispatchers.IO).launch {
                when (changeType) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        chatMessageDao.insertMessages(listOf(newMessage)) // ✅ Insert/update
                    }

                    DocumentChange.Type.REMOVED -> {
                        chatMessageDao.deleteMessageById(newMessage.id) // ✅ Delete from Room
                    }
                }
            }
        }
    }


    // ✅ Send message (Text or Audio)
    fun sendMessage(courseId: String, message: ChatMessage) {
        firebaseService.sendMessage(courseId, message) { success ->
            if (success) {
                CoroutineScope(Dispatchers.IO).launch {
                    chatMessageDao.insertMessages(listOf(message)) // Save locally
                }
            }
        }
    }

    // ✅ Clear all messages for a course (if needed)
    suspend fun clearMessages(courseId: String) {
        chatMessageDao.clearMessages(courseId)
    }
}

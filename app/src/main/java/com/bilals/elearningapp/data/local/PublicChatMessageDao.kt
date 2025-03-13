package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilals.elearningapp.data.model.PublicChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicChatMessageDao {
    @Query("SELECT * FROM public_chat_messages ORDER BY timestamp ASC")
    fun getMessages(): Flow<List<PublicChatMessage>> // Fetch all public messages

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<PublicChatMessage>) // Insert/update messages

    @Query("DELETE FROM public_chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String) // Delete a specific message

    @Query("DELETE FROM public_chat_messages WHERE id IN (:messageIds)")
    suspend fun deleteMessages(messageIds: List<String>) // Delete multiple messages

    @Query("DELETE FROM public_chat_messages")
    suspend fun clearAllMessages() // Clear entire public chat history
}

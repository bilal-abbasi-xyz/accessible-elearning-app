package com.bilals.elearningapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilals.elearningapp.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE courseId = :courseId ORDER BY timestamp ASC")
    fun getMessages(courseId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Query("DELETE FROM chat_messages WHERE courseId = :courseId")
    suspend fun clearMessages(courseId: String)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)

    // Delete multiple messages by their ids
    @Query("DELETE FROM chat_messages WHERE id IN (:messageIds)")
    suspend fun deleteMessages(messageIds: List<String>)


}

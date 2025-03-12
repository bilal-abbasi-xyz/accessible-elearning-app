package com.bilals.elearningapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val courseId: String = "",
    val senderId: String = "",

    @get:PropertyName("isAudio") @set:PropertyName("isAudio")
    var isAudio: Boolean = false,  // "text" or "audio"

    val content: String = "",  // For text messages
    val audioUrl: String? = null,  // For audio messages
    val timestamp: Long = System.currentTimeMillis()
)

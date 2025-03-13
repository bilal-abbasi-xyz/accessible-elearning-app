package com.bilals.elearningapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import java.util.UUID

@Entity(tableName = "public_chat_messages")
data class PublicChatMessage(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val senderId: String = "",

    @get:PropertyName("isAudio") @set:PropertyName("isAudio")
    var isAudio: Boolean = false,  // Distinguishes text and audio messages

    val content: String = "",  // Text message content
    val audioUrl: String? = null,  // Audio file URL (if any)
    val timestamp: Long = System.currentTimeMillis()
)

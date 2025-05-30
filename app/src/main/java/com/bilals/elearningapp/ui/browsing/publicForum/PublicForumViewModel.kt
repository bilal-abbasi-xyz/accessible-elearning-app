package com.bilals.elearningapp.ui.browsing.publicForum

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.PublicChatMessage
import com.bilals.elearningapp.data.repository.PublicChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//import com.bilals.elearningapp.data.model.Message

class PublicForumViewModel(
    private val repository: PublicChatRepository
) : ViewModel() {

    // StateFlow to hold the list of public chat messages
    private val _messages = MutableStateFlow<List<PublicChatMessage>>(emptyList())
    val messages: StateFlow<List<PublicChatMessage>> = _messages

    init {
        viewModelScope.launch {
            repository.syncPublicChatMessages() // Sync messages from Firebase to Room
        }
        fetchMessages()
        listenForUpdates()
    }

    // Fetch messages from Room
    private fun fetchMessages() {
        viewModelScope.launch {
            repository.getMessages().collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    // Listen for real-time updates from Firebase (repository handles the update of local Room DB)
    private fun listenForUpdates() {
        repository.listenForMessageUpdates()
    }

    // Send a new public chat message
    fun sendMessage(content: String,
                    audioPath: String? = null,
                    senderId: String) {
        viewModelScope.launch {
            val message = PublicChatMessage(
                id        = System.currentTimeMillis().toString(),
                senderId  = senderId,
                content   = content,
                audioUrl  = audioPath,            // null for text, non-null for audio
                timestamp = System.currentTimeMillis(),
                isAudio   = (audioPath != null)
            )
            repository.sendMessage(message)
        }
    }
}

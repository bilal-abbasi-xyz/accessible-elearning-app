package com.bilals.elearningapp.ui.contentCreation.browsing.courseForum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.ChatMessage
import com.bilals.elearningapp.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseForumViewModel(
    private val repository: ChatRepository,
    courseId: String
) : ViewModel() {

    //  StateFlow to hold messages list
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    init {
        viewModelScope.launch {
            repository.syncChatMessages(courseId)  //  Sync when screen opens
        }
        fetchMessages(courseId)
        listenForUpdates(courseId) //  Now passing courseId correctly
    }

    //  Fetch messages from Room (local database)
    private fun fetchMessages(courseId: String) {
        viewModelScope.launch {
            repository.getMessages(courseId).collect { chatMessages ->
                _messages.value = chatMessages
            }
        }
    }

    private fun listenForUpdates(courseId: String) {
        repository.listenForMessageUpdates(courseId)
    }


    fun sendMessage(
        courseId: String,
        content: String,
        audioPath: String? = null,
        senderId: String
    ) {
        viewModelScope.launch {
            val message = ChatMessage(
                id        = System.currentTimeMillis().toString(),
                courseId  = courseId,
                senderId  = senderId,
                content   = content,
                audioUrl  = audioPath,            // null for text, non-null for audio
                timestamp = System.currentTimeMillis(),
                isAudio   = (audioPath != null)
            )
            repository.sendMessage(courseId, message)
        }
    }

    //  Send Audio Message (TODO: Implement audio handling)
    fun sendAudioMessage(courseId: String, audioUrl: String) {
        viewModelScope.launch {
            val message = ChatMessage(
                id = System.currentTimeMillis().toString(),
                courseId = courseId,
                senderId = "yourUserId",
                content = audioUrl, // Store audio URL as content
                timestamp = System.currentTimeMillis(),
                isAudio = true
            )
            repository.sendMessage(courseId, message)
        }
    }
}

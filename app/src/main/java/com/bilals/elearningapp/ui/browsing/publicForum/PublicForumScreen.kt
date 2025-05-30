package com.bilals.elearningapp.ui.browsing.publicForum

import android.Manifest
import android.media.MediaRecorder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.SessionManager
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.model.PublicChatMessage
import com.bilals.elearningapp.data.repository.PublicChatRepository
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.contentCreation.browsing.courseForum.AudioBubble
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PublicForumScreen(
    navController: NavController,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val database = ElearningDatabase.getDatabase(context)
    val publicChatMessageDao = remember { database.publicChatMessageDao() }
    val repository = remember { PublicChatRepository(publicChatMessageDao, context) }
    val viewModel = remember { PublicForumViewModel(repository) }

    val messages by viewModel.messages.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val userId = SessionManager.getUserIdFromPreferences(context)

    // Request mic-permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> /* you can show a toast if denied */ }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

// Recording state
    var isRecording by remember { mutableStateOf(false) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp), // leave space for bottom nav
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AppBar
            AppBar(title = "Public Forum") { navController.popBackStack() }
            Spacer(modifier = Modifier.height(16.dp))

            // List messages in a LazyColumn
            val listState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = listState
            ) {
                items(messages) { message ->

                    var senderName by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(message.senderId) {
                        Log.d("ChatMessage", "Sender ID: ${message.senderId}, User ID: ${userId}")

                        appContainer.userRepository.getUserById(message.senderId) { user ->
                            senderName = user?.name
                            Log.d(
                                "ChatMessage",
                                "Fetched User: ${user?.name}, Sender ID: ${message.senderId}"
                            )
                        }
                    }

                    if (message.audioUrl != null) {
                        AudioBubble(
                            audioUrl = message.audioUrl,
                            timestamp = message.timestamp,
                            isSentByUser = message.senderId == userId,
                            senderName = senderName ?: "Unknown"
                        )
                    } else {
                        PublicChatBubble(
                            message = message,
                            isSentByUser = message.senderId == userId,
                            senderName = senderName ?: "Unknown",
                            onItemClick = {
                                // Handle the click event here, such as navigating to a message detail screen or showing actions
                                Log.d("ChatBubble", "Message clicked: ${message.content}")
                            }
                        )
                    }
                }
            }

            // Scroll to the bottom whenever messages change
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message input area
            if (userId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp)),
                        placeholder = { Text("Type a message...") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // 2) Mic toggle
                    IconButton(onClick = {
                        if (!isRecording) {
                            audioFilePath = context.externalCacheDir
                                ?.absolutePath + "/record_${System.currentTimeMillis()}.3gp"
                            recorder = MediaRecorder().apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                setOutputFile(audioFilePath)
                                prepare(); start()
                            }
                            isRecording = true
                            SpeechService.announce(context, "Recording started.")
                        } else {
                            coroutineScope.launch(Dispatchers.IO) {
                                recorder?.run { stop(); release() }
                                withContext(Dispatchers.Main) {
                                    isRecording = false
                                    SpeechService.announce(
                                        context,
                                        "Recording stopped, sending."
                                    )
                                    audioFilePath?.let { path ->
                                        viewModel.sendMessage("", path, userId)
                                        audioFilePath = null
                                    }
                                }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop recording" else "Start recording",
                            tint = if (isRecording) Color.Red else Color.Black,
                            modifier = Modifier.semantics {
                                stateDescription =
                                    if (isRecording) "Recording in progress" else "Tap to start recording"
                            }
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (messageText.text.isNotBlank()) {
                                viewModel.sendMessage(messageText.text.trim(), "", userId)
                                messageText = TextFieldValue("")
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black
                        )
                    }
                }
            } else {
                Text(
                    text = "Log in to send messages",
                    style = AppTypography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(ScreenRoutes.Login.route) },
                    textAlign = TextAlign.Center
                )
            }
        }
//        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
//            BottomNavBar(navController = navController)
//        }
    }
}
@Composable
fun PublicChatBubble(
    message: PublicChatMessage,
    isSentByUser: Boolean,
    onItemClick: (String) -> Unit,
    senderName: String,
) {
    val bubbleColor = if (isSentByUser) Color(0xFF007AFF) else Color.Green
    val textColor = if (isSentByUser) Color.White else Color.Black
    val formattedTimestamp = SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // 1) Sender Name row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {
                    contentDescription = if (isSentByUser) "You" else senderName
                }
                .focusable(true),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Text(
                text = if (isSentByUser) "You" else senderName,
                style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                color = Color.Gray
            )
        }

        // 2) Public chat bubble row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clearAndSetSemantics {
//                    role = Role.Button
                    contentDescription = message.content
//                    onClick {
//                        onItemClick(message.id)
//                        true
//                    }
                }
                .focusable(true),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .background(bubbleColor, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 16.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 3) Timestamp row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {
                    contentDescription = formattedTimestamp
                }
                .focusable(true),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Text(
                text = formattedTimestamp,
                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
                color = Color.Gray
            )
        }
    }
}

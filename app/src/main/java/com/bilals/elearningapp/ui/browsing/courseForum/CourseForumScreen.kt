package com.bilals.elearningapp.ui.contentCreation.browsing.courseForum

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
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
import com.bilals.elearningapp.data.model.ChatMessage
import com.bilals.elearningapp.data.repository.ChatRepository
import com.bilals.elearningapp.serviceLocator.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CourseForumScreen(
    navController: NavController,
    courseId: String,
    courseName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val accessibilityManager = LocalAccessibilityManager.current
    val database = ElearningDatabase.getDatabase(context)
    val chatMessageDao = remember { database.chatMessageDao() }
    val repository = remember { ChatRepository(chatMessageDao, context) }
    val viewModel = remember { CourseForumViewModel(repository, courseId) }

    val messages by viewModel.messages.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val userId = SessionManager.getUserIdFromPreferences(context)

    // Request microphone permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> /* handle denied */ }
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
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(title = "Forum: $courseName") { navController.popBackStack() }
            Spacer(modifier = Modifier.height(16.dp))

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
                        appContainer.userRepository.getUserById(message.senderId) { user ->
                            senderName = user?.name
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
                        ChatBubble(
                            message = message,
                            isSentByUser = message.senderId == userId,
                            senderName = senderName ?: "Unknown",
                            onItemClick = {}
                        )
                    }
                }
            }
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (userId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1) Text input
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp)),
                        placeholder = { Text("Type a message...") },
                        keyboardOptions = KeyboardOptions.Default,
                        keyboardActions = KeyboardActions(onSend = {
                            // send text on IME-send
                            if (messageText.text.isNotBlank()) {
                                coroutineScope.launch {
                                    viewModel.sendMessage(
                                        courseId,
                                        messageText.text.trim(),
                                        null,        // no audio
                                        userId
                                    )
                                    messageText = TextFieldValue("")
                                }
                            }
                        })
                    )

                    Spacer(Modifier.width(8.dp))

                    // 2) Mic toggle: start ⇄ stop & send
                    IconButton(onClick = {
                        if (!isRecording) {
                            audioFilePath = context.externalCacheDir
                                ?.absolutePath + "/record_${'$'}{System.currentTimeMillis()}.3gp"
                            recorder = MediaRecorder().apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                setOutputFile(audioFilePath)
                                prepare()
                                start()
                            }
                            isRecording = true
                            SpeechService.announce(context, "Recording started.")
                        } else {
                            // offload the stop/release work
                            coroutineScope.launch(Dispatchers.IO) {
                                recorder?.run {
                                    stop()
                                    release()
                                }
                                withContext(Dispatchers.Main) {
                                    isRecording = false
                                    SpeechService.announce(context, "Recording stopped, sending.")
                                    audioFilePath?.let { path ->
                                        viewModel.sendMessage(courseId, "", path, userId)
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
                                    if (isRecording) "Recording in progress"
                                    else "Tap to start recording"
                            }
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // 3) Pure text-send button
                    IconButton(onClick = {
                        if (messageText.text.isNotBlank()) {
                            coroutineScope.launch {
                                viewModel.sendMessage(
                                    courseId,
                                    messageText.text.trim(),
                                    null,     // force no audio
                                    userId
                                )
                                messageText = TextFieldValue("")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send text message")
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
//            BottomNavBar(navController)
//        }
    }
}
@Composable
fun AudioBubble(
    audioUrl: String,
    timestamp: Long,
    isSentByUser: Boolean,
    senderName: String
) {
    val formattedTs = SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(timestamp))
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // 1) Sender row — full-width focus & read
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

        // 2) Audio bubble — exactly as before
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clearAndSetSemantics {
                    role = Role.Button
                    contentDescription = "Audio message"
                    onClick {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(context, Uri.parse(audioUrl))
                                prepare(); start()
                                setOnCompletionListener {
                                    release(); mediaPlayer = null
                                }
                            }
                        } else {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                            mediaPlayer = null
                        }
                        true
                    }
                }
                .focusable(true),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Box(
                Modifier
                    .background(
                        if (isSentByUser) Color(0xFF007AFF) else Color.Green,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (mediaPlayer?.isPlaying == true)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isSentByUser) Color.White else Color.Black
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "",
                        style = MaterialTheme.typography.body1.copy(fontSize = 12.sp),
                        color = if (isSentByUser) Color.White else Color.Black
                    )
                }
            }
        }

        // 3) Timestamp row — full-width focus & read
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {
                    contentDescription = formattedTs
                }
                .focusable(true),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Text(
                text = formattedTs,
                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isSentByUser: Boolean,
    onItemClick: (String) -> Unit,
    senderName: String,
) {
    val bubbleColor = if (isSentByUser) Color(0xFF007AFF) else Color.Green
    val textColor = if (isSentByUser) Color.White else Color.Black
    val formattedTimestamp = SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(message.timestamp))

    Column(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // 1) Sender Name row — full-width focus & read
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {
                    // just expose the name as text
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

        // 2) Message bubble row — full-width button
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
                Modifier
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

        // 3) Timestamp row — full-width focus & read
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



//  Background Gradient
@Composable
fun gradientBackground() = Brush.verticalGradient(listOf(Color(0xFFEAEAEA), Color(0xFFCCCCCC)))

package com.bilals.elearningapp.ui.contentCreation.browsing.courseForum

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.model.ChatMessage
import com.bilals.elearningapp.data.repository.ChatRepository
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.SessionManager
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.BottomNavBar
import kotlinx.coroutines.launch
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
    val database = ElearningDatabase.getDatabase(context)
    val chatMessageDao = remember { database.chatMessageDao() }
    val repository = remember { ChatRepository(chatMessageDao, context) }
    val viewModel = remember { CourseForumViewModel(repository, courseId) }

    val messages by viewModel.messages.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val userId = SessionManager.getUserIdFromPreferences(context)

//
//
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp), // Keep padding for the bottom navigation
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AppBar
            AppBar(title = "Forum Messages") { navController.popBackStack() }

            Spacer(modifier = Modifier.height(16.dp))

            val listState = rememberLazyListState()

// In the LazyColumn
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = listState,
                reverseLayout = false
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


                    ChatBubble(
                        message,
                        isSentByUser = message.senderId == userId,
                        senderName = senderName ?: "Unknown",
                        onItemClick = {
                            // Handle the click event here, such as navigating to a message detail screen or showing actions
                            Log.d("ChatBubble", "Message clicked: ${message.content}")
                        }
                    )
                }
            }

// After sending a message, scroll to the bottom
            LaunchedEffect(messages.size) {
                // Only attempt to scroll if there are messages in the list
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1) // Scroll to the last item
                }
            }


            Spacer(modifier = Modifier.height(8.dp))


            if (userId != null) {  // If user is logged in

                // ✅ Message Input & Send Button
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

                    // ✅ Send Text Button
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (messageText.text.isNotBlank()) {
                                viewModel.sendMessage(courseId, messageText.text.trim(), userId)
                                messageText = TextFieldValue("")
                            }
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black
                        )
                    }

//                    // ✅ Record & Send Audio Button
//                    IconButton(onClick = {
//                        // TODO: Implement audio recording logic
//                    }) {
//                        Icon(
//                            Icons.Default.Mic,
//                            contentDescription = "Record Audio",
//                            tint = Color.Black
//                        )
//                    }
                }
            } else {
                Text(
                    text = "Log in to send messages",
                    style = AppTypography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            navController.navigate(ScreenRoutes.Login.route)
                        },
                    textAlign = TextAlign.Center
                )
            }
        }

//         Bottom Navigation Bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
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
    val alignment = if (isSentByUser) Alignment.End else Alignment.Start

    val dateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTimestamp = dateFormatter.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        // Sender Name (Aligned correctly based on user or other person)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 0.dp)
                .semantics { this.isTraversalGroup = true },
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Box(
            ) {
                Text(
                    text = if (isSentByUser) "You" else senderName,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray
                )
            }
        }

        // Chat Bubble (Full-width Clickable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 0.dp)
                .semantics { this.isTraversalGroup = true },
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
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Normal),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Timestamp (Aligned correctly based on user or other person)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 0.dp)
                .semantics { this.isTraversalGroup = true },
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Box(
            ) {
                Text(
                    text = formattedTimestamp,
                    style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
                    color = Color.Gray
                )
            }
        }
    }
}

// ✅ Background Gradient
@Composable
fun gradientBackground() = Brush.verticalGradient(listOf(Color(0xFFEAEAEA), Color(0xFFCCCCCC)))

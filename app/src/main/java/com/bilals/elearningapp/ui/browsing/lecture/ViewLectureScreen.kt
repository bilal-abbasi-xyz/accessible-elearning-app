package com.bilals.elearningapp.ui.contentCreation.browsing.lecture

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
//import com.bilals.elearningapp.data.database.ElearningDatabase
import com.bilals.elearningapp.data.model.LectureType
import com.bilals.elearningapp.data.repository.LectureRepository
//import com.bilals.elearningapp.services.SpeechService
import com.bilals.elearningapp.ui.uiComponents.AppBar
import io.noties.markwon.Markwon
import android.widget.TextView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bilals.elearningapp.data.local.ElearningDatabase
import io.noties.markwon.SoftBreakAddsNewLinePlugin
@Composable
fun ViewLectureScreen(
    navController: NavController,
    lectureId: String,
    lectureName: String
) {
    val context = LocalContext.current
    // Initialize DAO & repository
    val database = ElearningDatabase.getDatabase(context)
    val lectureDao = remember { database.lectureDao() }
    val lectureRepo = remember { LectureRepository(lectureDao, context) }

    // ViewModel loading lecture
    val viewModel = remember { ViewLectureViewModel(lectureRepo, lectureId) }
    val lecture by viewModel.lecture.collectAsState()

    Scaffold(
        topBar = { AppBar(title = lectureName) { navController.popBackStack() } }
    ) { paddingValues ->
        lecture?.let { lec ->
            when (lec.type) {
                LectureType.TEXT -> {
                    // Build Markwon (with soft-break plugin if you still want single-newline handling)
                    val markwon = remember {
                        Markwon.builder(context)
                            .usePlugin(SoftBreakAddsNewLinePlugin.create())
                            .build()
                    }

                    // Decode literal “\n\n” into real double line-breaks
                    val decodedContent = remember(lec.content) {

                        lec.content.replace("\n", "\n\n")
                        lec.content.replace("\\n", "\n\n")

                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                TextView(ctx).apply {
                                    setTextColor(Color.Black.toArgb())
                                }
                            },
                            update = { tv ->
                                markwon.setMarkdown(tv, decodedContent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                LectureType.AUDIO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        AudioPlayer(url = lec.content)
                    }
                }
            }
        }
    }
}

@Composable
fun AudioPlayer(url: String) {
    val context = LocalContext.current
    val mediaItem = MediaItem.fromUri(Uri.parse(url))


    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = { exoPlayer.playWhenReady = !exoPlayer.isPlaying }) {
            Icon(
                imageVector = if (exoPlayer.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play Audio",
                tint = Color.Black
            )
        }
    }
}


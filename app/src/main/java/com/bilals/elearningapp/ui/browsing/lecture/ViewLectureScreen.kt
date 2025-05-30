package com.bilals.elearningapp.ui.contentCreation.browsing.lecture

import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.tts.SpeechService
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun ViewLectureScreen(
    navController: NavController,
    lectureId: String,
    lectureName: String
) {
    val context = LocalContext.current
    val database = ElearningDatabase.getDatabase(context)
    val lectureDao = remember { database.lectureDao() }
    val lectureRepo = remember { LectureRepository(lectureDao, context) }
    val viewModel = remember { ViewLectureViewModel(lectureRepo, lectureId) }
    val lecture by viewModel.lecture.collectAsState()

    lecture?.let { lec ->
        val markwon = remember {
            Markwon.builder(context)
                .usePlugin(SoftBreakAddsNewLinePlugin.create())
                .build()
        }
        val decodedContent = remember(lec.content) {
            lec.content
                .replace("\\n", "\n\n")
                .replace("\n", "\n\n")
        }
        val sentences = remember(decodedContent) {
            decodedContent.trim().split(Regex("(?<=[.!?])\\s+"))
        }
        var currentIndex by remember { mutableStateOf(0) }
        var isPlaying by remember { mutableStateOf(false) }

        // Initialize TTS
        val tts = remember {
            TextToSpeech(context) { /* no-op */ }
        }
        DisposableEffect(Unit) {
            onDispose { tts.shutdown() }
        }

        fun clean(s: String) = s.replace(Regex("[#*\\-+]+"), "").trim()

        suspend fun speakAndAwait(sentence: String) {
            suspendCancellableCoroutine<Unit> { cont ->
                val id = sentence.hashCode().toString()
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {}
                    override fun onDone(utteranceId: String) {
                        if (utteranceId == id && cont.isActive) cont.resume(Unit) {}
                    }
                    override fun onError(utteranceId: String) {
                        if (utteranceId == id && cont.isActive) cont.resume(Unit) {}
                    }
                })
                tts.speak(clean(sentence), TextToSpeech.QUEUE_FLUSH, null, id)
            }
        }

        LaunchedEffect(isPlaying) {
            if (isPlaying) {
                while (isPlaying) {
                    val sentence = sentences.getOrNull(currentIndex) ?: break
                    speakAndAwait(sentence)
                    if (currentIndex < sentences.lastIndex) {
                        currentIndex++
                    } else {
                        isPlaying = false
                        SpeechService.announce(context, "Done")
                    }
                }
            }
        }

        Scaffold(
            topBar = { AppBar(title = lectureName) { navController.popBackStack() } },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val btnModifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                    val btnShape = RoundedCornerShape(6.dp)
                    val padding = PaddingValues(horizontal = 24.dp)

                    Button(
                        onClick = {
                            isPlaying = false
                            if (currentIndex > 0) currentIndex--
                            SpeechService.announce(context, clean(sentences[currentIndex]))
                        },
                        modifier = btnModifier,
                        shape = btnShape,
                        contentPadding = padding
                    ) {
                        Text("Previous sentence")
                    }

                    Button(
                        onClick = {
                            if (isPlaying) {
                                isPlaying = false
                                SpeechService.announce(context, "Paused")
                            } else {
                                isPlaying = true
                            }
                        },
                        modifier = btnModifier,
                        shape = btnShape,
                        contentPadding = padding
                    ) {
                        Text(if (isPlaying) "Pause reading" else "Play reading")
                    }

                    Button(
                        onClick = {
                            isPlaying = false
                            if (currentIndex < sentences.lastIndex) currentIndex++
                            SpeechService.announce(context, clean(sentences[currentIndex]))
                        },
                        modifier = btnModifier,
                        shape = btnShape,
                        contentPadding = padding
                    ) {
                        Text("Next sentence")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .clearAndSetSemantics {}
            ) {
                AndroidView(
                    factory = { ctx ->
                        TextView(ctx).apply { setTextColor(Color.Black.toArgb()) }
                    },
                    update = { tv ->
                        markwon.setMarkdown(tv, decodedContent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                )
            }
        }
    }
}

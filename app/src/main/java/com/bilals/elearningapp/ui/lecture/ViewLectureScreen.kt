package com.bilals.elearningapp.ui.lecture

//import com.bilals.elearningapp.navigation.NavDataManager
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.bilals.elearningapp.R
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.model.LectureType
import com.bilals.elearningapp.data.repository.LectureRepository
import com.bilals.elearningapp.ui.uiComponents.AppBar
import io.noties.markwon.Markwon

@Composable
fun ViewLectureScreen(navController: NavController, lectureId: String, lectureName: String) {
    val context = LocalContext.current
    val database = ElearningDatabase.getDatabase(context)
// Get the DAO from the database instance
    val lectureDao = remember { database.lectureDao() }

    val lectureRepo = remember { LectureRepository(lectureDao, context) }

    val viewModel = remember { ViewLectureViewModel(lectureRepo, lectureId) }

    val lecture by viewModel.lecture.collectAsState()

    AppBar(title = lectureName) { navController.popBackStack() }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        lecture?.let { lec ->


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 30.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                when (lec.type) {
                    LectureType.TEXT -> MarkdownText(content = lec.content.trimIndent())
                    LectureType.AUDIO -> AudioPlayer(url = lec.content)
                }
            }
        }
    }
}

@Composable
fun MarkdownText(content: String) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }

    val formattedContent = content.replace("\\n", "\n") // Ensure proper line breaks

    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                movementMethod = LinkMovementMethod.getInstance()
                setTextIsSelectable(true) // Allow text selection

                typeface = ResourcesCompat.getFont(ctx, R.font.lexend)
                textSize = 18f // Change font size
                setTextColor(ContextCompat.getColor(ctx, R.color.black))
                markwon.setMarkdown(this, formattedContent)
            }
        },
        update = {
            it.text = markwon.toMarkdown(formattedContent)
        } // Ensure correct Markdown conversion
    )
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


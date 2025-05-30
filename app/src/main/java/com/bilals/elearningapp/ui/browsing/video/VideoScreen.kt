package com.bilals.elearningapp.ui.browsing.video

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bilals.elearningapp.ui.uiComponents.AppCard
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import android.app.Application
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun VideoScreen(
    navController: NavController,
    videoUrl: String,
    vm: VideoViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    // Load the media once
    LaunchedEffect(videoUrl) {
        vm.loadVideo(videoUrl)
    }

    // Observe play state
    val isPlaying by vm.isPlaying.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Video surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        MATCH_PARENT, MATCH_PARENT
                    )
                    player = vm.player
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // 2) Controls column
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Back 10s
            AppCard(
                onClick = { vm.seekBack10s() },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Go back 10 seconds"
                        heading()
                    }
            ) {
                Text(
                    text = "◀︎ 10s",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Play / Pause
            AppCard(
                onClick = { vm.togglePlayPause() },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = if (isPlaying) "Pause video" else "Play video"
                        heading()
                    }
            ) {
                Text(
                    text = if (isPlaying) "Pause" else "Play",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Forward 10s
            AppCard(
                onClick = { vm.seekForward10s() },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Go forward 10 seconds"
                        heading()
                    }
            ) {
                Text(
                    text = "10s ▶︎",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

package com.bilals.elearningapp.ui.browsing.video


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

// replace your old imports with:
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoViewModel(
    app: Application
) : AndroidViewModel(app) {

    val player: ExoPlayer = ExoPlayer.Builder(app).build().apply {
        playWhenReady = false
    }
    private val _isPlaying = MutableStateFlow(false)

    /** true when video is currently playing */
    val isPlaying: StateFlow<Boolean> = _isPlaying

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                viewModelScope.launch {
                    _isPlaying.emit(isPlaying)
                }
            }
        })
    }


    /** Load a new video URL into the player */
    fun loadVideo(url: String) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
    }

    /** Toggle between play and pause */
    fun togglePlayPause() {
        if (player.isPlaying) player.pause()
        else player.play()
    }

    /** Seek forward by exactly 10 seconds */
    fun seekForward10s() {
        val newPos = (player.currentPosition + 10_000L)
            .coerceAtMost(player.duration)
        player.seekTo(newPos)
    }

    /** Seek backward by exactly 10 seconds */
    fun seekBack10s() {
        val newPos = (player.currentPosition - 10_000L)
            .coerceAtLeast(0L)
        player.seekTo(newPos)
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}

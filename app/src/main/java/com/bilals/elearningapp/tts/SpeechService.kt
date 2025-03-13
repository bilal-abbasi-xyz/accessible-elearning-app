package com.bilals.elearningapp.tts

import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

object SpeechService {

    fun announce(context: Context, message: String) {
        // Cancel any ongoing TTS speech first
        TTSManager.cancel()

        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        Log.e("test", "in announce")

        if (accessibilityManager.isEnabled) {
            Log.e("test", "accessibility manager is enabled")
            val event = AccessibilityEvent.obtain().apply {
                eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                text.add(message)
            }
            accessibilityManager.sendAccessibilityEvent(event)
        } else {
            TTSManager.speakText(message)
        }
    }

}
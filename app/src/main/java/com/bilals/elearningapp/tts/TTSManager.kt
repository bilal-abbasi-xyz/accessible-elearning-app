package com.bilals.elearningapp.tts

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

object TTSManager : TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!TTSManager::tts.isInitialized) {
            tts = TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
            isInitialized = true
        }
    }

    fun speakText(text: String) {
        if (isInitialized) {
            Log.e("test", "TTS initialized.")
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()

            tts.setAudioAttributes(audioAttributes)
            val message = text
            tts.speak(message, TextToSpeech.QUEUE_ADD, null, "")
        } else {
            Log.e("test", "TTS not initialized.")
        }
    }

    fun shutdown() {
        if (TTSManager::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
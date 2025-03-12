package com.bilals.elearningapp.stt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
object STTManager {
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isInitialized = false
    private var onResult: ((String) -> Unit)? = null
    private var onEndOfSpeech: (() -> Unit)? = null
    private var onListeningStopped: (() -> Unit)? = null

    fun initialize(context: Context) {
        if (!isInitialized) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d("STT", "Ready for speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d("STT", "Speech started")
                    }

                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        Log.d("STT", "Speech ended")
                        onEndOfSpeech?.invoke()
                        onListeningStopped?.invoke()
                    }

                    override fun onError(error: Int) {
                        Log.e("STT", "Error: $error")
                        onListeningStopped?.invoke()
                    }

                    override fun onResults(results: Bundle?) {
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                            Log.d("STT", "Recognized text: ${it[0]}")
                            onResult?.invoke(it[0])
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            isInitialized = true
        }
    }

    fun startListening(
        context: Context,
        resultCallback: (String) -> Unit,
        endOfSpeechCallback: () -> Unit,
        listeningStoppedCallback: () -> Unit
    ) {
        onResult = resultCallback
        onEndOfSpeech = endOfSpeechCallback
        onListeningStopped = listeningStoppedCallback

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening for command...")
        }
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        if (isInitialized) {
            speechRecognizer.stopListening()
        }
    }

    fun shutdown() {
        if (isInitialized) {
            speechRecognizer.destroy()
        }
    }
}
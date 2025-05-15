package com.bilals.elearningapp.stt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

object STTManager {
    private var appContext: Context? = null
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isInitialized = false
    private var isListening = false

    private var onResult: ((String) -> Unit)? = null
    private var onEndOfSpeech: (() -> Unit)? = null
    private var onListeningStopped: (() -> Unit)? = null

    // Handler for our health‐check “watchdog”
    private val healthCheckHandler = Handler(Looper.getMainLooper())
    private val HEALTH_CHECK_INTERVAL_MS = 30_000L

    private val healthCheckRunnable = object : Runnable {
        override fun run() {
            // Only restart if initialized but not actively listening
            if (isInitialized && !isListening) {
                Log.d("STT", "Health check: restarting recognizer")
                restartRecognizer()
            }
            healthCheckHandler.postDelayed(this, HEALTH_CHECK_INTERVAL_MS)
        }
    }

    fun initialize(context: Context) {
        // Save application context for later
        appContext = context.applicationContext

        if (!isInitialized) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { /*…*/ }
                    override fun onBeginningOfSpeech() { /*…*/ }

                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isListening = false
                        onEndOfSpeech?.invoke()
                        onListeningStopped?.invoke()
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        onListeningStopped?.invoke()
                    }

                    override fun onResults(results: Bundle?) {
                        results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            ?.let { onResult?.invoke(it) }
                        isListening = false
                        onListeningStopped?.invoke()
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            isInitialized = true
            startHealthCheck()
        }
    }

    private fun startHealthCheck() {
        healthCheckHandler.removeCallbacks(healthCheckRunnable)
        healthCheckHandler.postDelayed(healthCheckRunnable, HEALTH_CHECK_INTERVAL_MS)
    }

    private fun restartRecognizer() {
        // Tear down and re-init
        shutdown()
        appContext?.let { initialize(it) }
    }

    fun startListening(
        context: Context,
        resultCallback: (String) -> Unit,
        endOfSpeechCallback: () -> Unit,
        listeningStoppedCallback: () -> Unit
    ) {
        if (!isInitialized) initialize(context)

        onResult = resultCallback
        onEndOfSpeech = endOfSpeechCallback
        onListeningStopped = {
            isListening = false
            listeningStoppedCallback()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening for command...")
        }

        isListening = true
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        if (isInitialized && isListening) {
            isListening = false
            speechRecognizer.stopListening()
        }
    }

    fun shutdown() {
        healthCheckHandler.removeCallbacks(healthCheckRunnable)
        if (isInitialized) {
            speechRecognizer.destroy()
            isInitialized = false
            isListening = false
        }
    }
}

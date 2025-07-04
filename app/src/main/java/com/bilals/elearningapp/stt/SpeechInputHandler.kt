package com.bilals.elearningapp.stt

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.TTSManager

class SpeechInputHandler(
    private val context: Context,
    private val navController: NavController,
    private val recognizedText: MutableState<String>,
    private val commandProcessor: VoiceCommandProcessor


) {

    var onSpeechHeading: (() -> Unit)? = null
    var onSpeechBold: (() -> Unit)? = null
    var onSpeechList: (() -> Unit)? = null
    var onSpeechNewLine: (() -> Unit)? = null

    var isListening = false
        private set

    private val screenRoutes = mapOf(
        "settings" to ScreenRoutes.Settings.route,
        "profile settings" to ScreenRoutes.ProfileSettings.route,
        "voice settings" to ScreenRoutes.VoiceSettings.route,
        "login" to ScreenRoutes.Login.route,
        "sign up" to ScreenRoutes.SignUp.route,
        "categories" to ScreenRoutes.CategoryList.route,
        "home screen" to ScreenRoutes.Home.route,
        "public forum" to ScreenRoutes.PublicForum.route,
        "training" to ScreenRoutes.Training.route,
        "reports" to ScreenRoutes.Report.route,
        "report" to ScreenRoutes.Report.route,
        "page UI" to ScreenRoutes.UISettings.route
    )

    private val commandVariations = mapOf(
        "settings" to listOf("settings", "open settings", "modify settings", "show settings"),
        "profile settings" to listOf(
            "profile settings",
            "edit profile",
            "update profile",
            "profile"
        ),
        "voice settings" to listOf(
            "voice settings",
            "adjust voice",
            "change voice",
            "voice commands"
        ),
        "login" to listOf("login", "sign in", "log in", "log into my account"),
        "sign up" to listOf("sign up", "register", "create an account", "join now"),
        "categories" to listOf("categories", "browse courses", "show courses"),
        "public forum" to listOf("public forum", "public", "forum", "form", "public form"),
        "home screen" to listOf("home screen", "home", "ham"),
        "training" to listOf("guide", "training", "tutorial"),
        "reports" to listOf("report generation", "report generation", "report"),
        "report" to listOf("reports generation", "reports")
    )

    private val synonyms = mapOf(
        "settings" to listOf(
            "settings",
            "setting",
            "options",
            "options",
            "adjustments",
            "preferences"
        ),
        "profile" to listOf("profile", "account"),
        "voice" to listOf("voice", "audio", "sound"),
        "categories" to listOf("category", "topics", "topic"),
        "commands" to listOf("commands", "command", "hotkeys"),
        "public forum" to listOf("public forum", "chat", "forum", "public"),

        "home screen" to listOf("home screen", "home", "ham"),
        "training" to listOf("guide", "training", "tutorial"),
        "reports" to listOf("report generation", "report")
    )


    fun startListening() {
        if (isListening) return

        // Cancel any ongoing speech
        TTSManager.cancel()

        isListening = true
        STTManager.startListening(
            context = context,
            resultCallback = { spokenText ->
                recognizedText.value = spokenText
                processVoiceCommand(spokenText)
            },
            endOfSpeechCallback = {},
            listeningStoppedCallback = { isListening = false }
        )
//        SpeechService.announce(context, "Listening")

        Toast.makeText(context, "Listening for command...", Toast.LENGTH_SHORT).show()
    }

    fun stopListening() {
        if (!isListening) return

        isListening = false
        STTManager.stopListening()
//        SpeechService.announce(context, "Stopped listening")

//        Toast.makeText(context, "Stopped listening", Toast.LENGTH_SHORT).show()
    }

    private fun processVoiceCommand(spokenText: String) {
        // 1) check if we’re on CreateLectureScreen
        val currentRoute = navController.currentDestination
            ?.route
            ?.substringBefore("/{")
        if (currentRoute == ScreenRoutes.CreateLecture.route) {
            when (spokenText.lowercase().trim()) {
                "heading" -> onSpeechHeading?.invoke().also { return }
                "bold" -> onSpeechBold?.invoke().also { return }
                "list" -> onSpeechList?.invoke().also { return }
                "new line" -> onSpeechNewLine?.invoke().also { return }
            }
        }


        val bestMatch = findBestMatch(spokenText)

        if (bestMatch != null) {
            val route = screenRoutes[bestMatch]  // Use corrected mapping
            if (route != null) {
                val currentRoute = navController.currentDestination?.route

// If the route contains parameters, extract only the base path before checking
                val normalizedCurrentRoute = currentRoute?.substringBefore("/{")

                if (normalizedCurrentRoute != route.substringBefore("/{")) {
                    navController.navigate(route) {
                        launchSingleTop = true // Prevent multiple instances of the same destination
                        restoreState = true     // Restore state if available
                    }
                }

                return
            }
        }

        // If no predefined match, check categories from the database
        commandProcessor.processCommand(spokenText)
    }

    private fun findBestMatch(input: String): String? {
        val words = input.lowercase().trim().split(" ")

        //  Ensure bidirectional synonym mapping
        //  Ensure bidirectional synonym mapping
        val bidirectionalSynonyms = mutableMapOf<String, MutableList<String>>()

        synonyms.forEach { (key, values) ->
            values.forEach { value ->
                // Add original → synonym
                bidirectionalSynonyms.getOrPut(key) { mutableListOf() }.add(value)

                // Add synonym → original (making it bidirectional)
                bidirectionalSynonyms.getOrPut(value) { mutableListOf() }.add(key)
            }
        }


        // Generate all possible replacements
        val expandedInputs = mutableSetOf<String>()
        fun generateCombinations(currentWords: List<String>, index: Int) {
            if (index == words.size) {
                expandedInputs.add(currentWords.joinToString(" ")) // Store the combination
                return
            }
            val word = words[index]

            // Get synonyms (including bidirectional mappings)
            val replacements = bidirectionalSynonyms[word] ?: listOf(word)

            for (replacement in replacements) {
                generateCombinations(currentWords + replacement, index + 1)
            }
        }

        // 🔹 Expand using synonyms
        generateCombinations(emptyList(), 0)

        // 🔹 Debug: Print all generated variations
        Log.d("VoiceCommand", "Generated Variations: $expandedInputs")

        //  Now check for an exact match in commandVariations
        for (expanded in expandedInputs) {
            for ((command, variations) in commandVariations) {
                if (expanded in variations) {
                    return command // Found a matching command
                }
            }
        }

        return null // No match found
    }


}

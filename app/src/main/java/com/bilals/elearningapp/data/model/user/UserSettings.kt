package com.bilals.elearningapp.data.model.user

import com.bilals.elearningapp.data.model.PageUISettings
import com.bilals.elearningapp.data.model.VoiceSettings

data class UserSettings(
    val userId: String, // Unique identifier for the user
    var voiceSettings: VoiceSettings = VoiceSettings(), // Voice settings for the user
    var pageUISettings: PageUISettings = PageUISettings() // UI settings for the user
)

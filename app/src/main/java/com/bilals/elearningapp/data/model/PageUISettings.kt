package com.bilals.elearningapp.data.model

import com.bilals.elearningapp.ui.uiSettings.ColorSchemeOption

data class PageUISettings(
    val zoomLevel: Float = 1.0f,
    val highContrast: Boolean = false,
    val colorScheme: ColorSchemeOption = ColorSchemeOption.LIGHT
)
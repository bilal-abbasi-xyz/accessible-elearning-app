package com.bilals.elearningapp.ui.settings.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UISettingsViewModel : ViewModel() {
    // e.g. backed by DataStore or just in‐mem for now:
    var chosenFontIndex by mutableStateOf(0)
        private set

    fun setFontIndex(i: Int) { chosenFontIndex = i }
}
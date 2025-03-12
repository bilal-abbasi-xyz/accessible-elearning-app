package com.bilals.elearningapp.ui.uiSettings

import androidx.lifecycle.ViewModel
//import com.bilals.elearningapp.data.DummyDataProvider
import com.bilals.elearningapp.data.model.PageUISettings
import com.bilals.elearningapp.data.model.user.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ColorSchemeOption(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    BLUE("Blue"),
    GREEN("Green")
}

class UISettingsViewModel : ViewModel() {

//    private val dummyDataProvider = DummyDataProvider()

//    private val userSettings: UserSettings = dummyDataProvider.userSettings2

//    private val _pageUISettings = MutableStateFlow(userSettings.pageUISettings)
//    val pageUISettings: StateFlow<PageUISettings> = _pageUISettings

    fun updateZoomLevel(newZoomLevel: Float) {
//        _pageUISettings.value = _pageUISettings.value.copy(zoomLevel = newZoomLevel)
    }

    fun toggleHighContrast() {
//        _pageUISettings.value = _pageUISettings.value.copy(highContrast = !_pageUISettings.value.highContrast)
    }

    fun changeColorScheme(newColorScheme: ColorSchemeOption) {
//        _pageUISettings.value = _pageUISettings.value.copy(colorScheme = newColorScheme)
    }
}
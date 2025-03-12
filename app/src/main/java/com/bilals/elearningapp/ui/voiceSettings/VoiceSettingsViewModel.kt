package com.bilals.elearningapp.ui.voiceSettings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.bilals.elearningapp.data.DummyDataProvider
import com.bilals.elearningapp.data.model.user.UserSettings
import com.bilals.elearningapp.data.model.VoiceCommand
import com.bilals.elearningapp.data.model.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceSettingsViewModel : ViewModel() {
//
//    val dummyDataProvider = DummyDataP/rovider()
////    val userSettings: UserSettings = dummyDataProvider.userSettings1
//
////    private val _voiceCommands = MutableStateFlow(userSettings.voiceSettings.voiceCommands)
//    val voiceCommands: StateFlow<List<VoiceCommand>> = _voiceCommands
//
//    private val _listeningState = mutableStateOf(false)
//    val listeningState: State<Boolean> = _listeningState
//
//    private var currentCommandKey: String? = null
//
//    fun onChangeVoiceCommand(commandKey: String) {
//        currentCommandKey = commandKey
//        _listeningState.value = true
//    }
//
//    fun onSaveListeningCommand() {
//
//        val newVoiceCommand = "New Command for ${currentCommandKey?.replaceFirstChar {
//            if (it.isLowerCase()) it.titlecase(
//                Locale.getDefault()
//            ) else it.toString()
//        } ?: "Unknown"}"
//
//        currentCommandKey?.let {
//
//            val updatedCommands = _voiceCommands.value.toMutableList()
//            updatedCommands.find { it.key == currentCommandKey }?.let {
//                updatedCommands[updatedCommands.indexOf(it)] = it.copy(command = newVoiceCommand)
//            }
//
//            _voiceCommands.value = updatedCommands
//            userSettings.voiceSettings.voiceCommands = updatedCommands
//        }
//
//        _listeningState.value = false
//    }
//
//    fun onCancelListeningCommand() {
//        _listeningState.value = false
//    }
//
//    fun saveSettings() {
//
//        viewModelScope.launch {
//
//        }
//    }
}
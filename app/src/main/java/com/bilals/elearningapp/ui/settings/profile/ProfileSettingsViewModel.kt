package com.bilals.elearningapp.ui.settings.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileSettingsViewModel : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun onSaveClicked(
        newUserName: String,
        oldPassword: String,
        newPassword: String,
        repeatNewPassword: String
    ) {

        _errorMessage.value = null
        _successMessage.value = null

        if (newUserName.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || repeatNewPassword.isEmpty()) {
            _errorMessage.value = "All fields must be filled"
            return
        }

        if (newPassword != repeatNewPassword) {
            _errorMessage.value = "New passwords do not match"
            return
        }

        if (newPassword.length < 6) {
            _errorMessage.value = "New password must be at least 6 characters long"
            return
        }

        viewModelScope.launch {

            kotlinx.coroutines.delay(1000)

            println("New Username: $newUserName")
            println("New Password: $newPassword")

            _successMessage.value = "Profile settings saved successfully!"
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
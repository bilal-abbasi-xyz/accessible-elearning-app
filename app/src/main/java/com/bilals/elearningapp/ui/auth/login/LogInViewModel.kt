package com.bilals.elearningapp.ui.auth.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.repository.AuthRepository
import com.bilals.elearningapp.ui.auth.SessionManager
import kotlinx.coroutines.launch


class LogInScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val usernameOrEmail = mutableStateOf("")
    val password = mutableStateOf("")

    val loginError = mutableStateOf<String?>(null)

    fun onLogInButtonClick(onLoginSuccess: () -> Unit, context: Context) {
        loginError.value = null

        if (usernameOrEmail.value.isNotEmpty() && password.value.isNotEmpty()) {
            viewModelScope.launch {
                val result =
                    authRepository.loginWithEmailAndPassword(usernameOrEmail.value.trim(), password.value.trim())
                result.fold(
                    onSuccess = { userId ->
                        // userId is the String (UID) returned from loginWithEmailAndPassword
                        SessionManager.saveUserIdToPreferences(userId, context)

                        onLoginSuccess()
                    },
                    onFailure = { exception ->
                        loginError.value = exception.message ?: "Login failed."
                    }
                )
            }
        } else {
            loginError.value = "Please fill in both fields."
        }
    }


    fun onForgotPasswordClick() {

        Log.d("LogInScreenViewModel", "Forgot password clicked.")
    }
}
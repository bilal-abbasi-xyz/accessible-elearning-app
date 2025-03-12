package com.bilals.elearningapp.ui.auth.signup

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    val navigateToHome = mutableStateOf(false)

    private val emailPattern = Patterns.EMAIL_ADDRESS

    fun onCreateAccountClicked(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {

        errorMessage.value = null
        successMessage.value = null

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.value = "All fields must be filled"
            return
        }

        if (name.length < 3) {
            errorMessage.value = "Username must be at least 3 characters long"
            return
        }

        if (!isValidEmail(email)) {
            errorMessage.value = "Please enter a valid email address"
            return
        }

        if (password != confirmPassword) {
            errorMessage.value = "Passwords do not match"
            return
        }

        if (password.length < 6) {
            errorMessage.value = "Password must be at least 6 characters long"
            return
        }

        if (!isValidPassword(password)) {
            errorMessage.value = "Password must contain at least one letter and one number"
            return
        }

        viewModelScope.launch {
            authRepository.registerUser(email.trim(), password.trim(), name.trim()) { success ->
                if (success) {
                    // Registration was successful
                    successMessage.value = "Account created successfully!"
                    navigateToHome.value = true
                } else {
                    // Registration failed
                    errorMessage.value = "Sign-up failed."
                }
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return emailPattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {

        val letterRegex = Regex(".*[a-zA-Z].*")
        val digitRegex = Regex(".*[0-9].*")
        return letterRegex.matches(password) && digitRegex.matches(password)
    }
}
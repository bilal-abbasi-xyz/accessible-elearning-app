package com.bilals.elearningapp.ui.auth.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.data.repository.AuthRepository
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogInScreen(
    navController: NavController
) {

    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        Log.d("UserInfo", "Current user: ${user.displayName}, Email: ${user.email}")
        // Sign out the user if already signed in
        FirebaseAuth.getInstance().signOut()
        Log.d("UserInfo", "User signed out successfully.")
    } else {
        Log.d("UserInfo", "No current user logged in.")
    }


    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val authRepository = AuthRepository(firebaseAuth)

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LogInScreenViewModel::class.java)) {
                return LogInScreenViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val viewModel: LogInScreenViewModel = viewModel(factory = factory)

    val errorMessage = viewModel.loginError.value

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppBar(title = "Login") { navController.popBackStack() }
        Spacer(modifier = Modifier.height(24.dp))


        TextField(
            value = viewModel.usernameOrEmail.value,
            onValueChange = { viewModel.usernameOrEmail.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(16.dp))

        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.onLogInButtonClick(
                        onLoginSuccess = {
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                            SpeechService.announce(context, "Login successful!")
                            navController.popBackStack(ScreenRoutes.Login.route, inclusive = true)
                        }, context
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continue",
                    style = AppTypography.bodyLarge,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(ScreenRoutes.SignUp.route) {
                        popUpTo(ScreenRoutes.Login.route) { inclusive = true }
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create Account",
                    style = AppTypography.bodyLarge,
                    color = Color.White
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreviewAlt() {
    val navController = rememberNavController()

    LogInScreen(navController)
}
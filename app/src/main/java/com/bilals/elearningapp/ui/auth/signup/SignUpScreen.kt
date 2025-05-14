package com.bilals.elearningapp.ui.auth.signup

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val authRepository = AuthRepository(firebaseAuth)

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val viewModel: SignUpViewModel = viewModel(factory = factory)

    val userName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val errorMessage = viewModel.errorMessage.value
    val successMessage = viewModel.successMessage.value
    val navigateToHome = viewModel.navigateToHome.value

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {

            Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
            SpeechService.announce(context, "Account created!")
            navController.navigate(ScreenRoutes.Home.route) {
                popUpTo(ScreenRoutes.SignUp.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppBar(title = "Sign Up") { navController.popBackStack() }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = userName.value,
            onValueChange = { userName.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        successMessage?.let {
            Text(
                text = it,
                color = Color.Green,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.onCreateAccountClicked(
                        userName.value,
                        email.value,
                        password.value,
                        confirmPassword.value
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
                    text = "Create Account",
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
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.SignUp.route) { inclusive = true }
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
                    text = "Already have an account?",
                    style = AppTypography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    
    SignUpScreen(navController)
}
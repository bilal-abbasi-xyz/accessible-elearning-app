package com.bilals.elearningapp.ui.profileSettings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard

@Composable
fun ProfileSettingsScreen(navController: NavHostController) {

    val viewModel: ProfileSettingsViewModel = viewModel()

    val newUserName = remember { mutableStateOf("") }
    val oldPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val repeatNewPassword = remember { mutableStateOf("") }

    val errorMessage = viewModel.errorMessage.collectAsState().value
    val successMessage = viewModel.successMessage.collectAsState().value

    val context = LocalContext.current

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

            viewModel.clearMessages()
        }
        val announcement =
            "Edit profile screen. Navigate between fields to edit your username and password. 4 items available."
        SpeechService.announce(context, announcement)
    }

    val labelNewUserName = "New Username"
    val labelOldPassword = "Old Password"
    val labelNewPassword = "New Password"
    val labelRepeatNewPassword = "Repeat New Password"

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppBar("Profile Settings") { navController.popBackStack() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            // AppBar

            Spacer(modifier = Modifier.height(16.dp))

            // Username Text Field
            OutlinedTextField(
                value = newUserName.value,
                onValueChange = { newUserName.value = it },
                label = { Text(labelNewUserName) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                singleLine = true
            )

            // Old Password Text Field
            OutlinedTextField(
                value = oldPassword.value,
                onValueChange = { oldPassword.value = it },
                label = { Text(labelOldPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            // New Password Text Field
            OutlinedTextField(
                value = newPassword.value,
                onValueChange = { newPassword.value = it },
                label = { Text(labelNewPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            // Repeat New Password Text Field
            OutlinedTextField(
                value = repeatNewPassword.value,
                onValueChange = { repeatNewPassword.value = it },
                label = { Text(labelRepeatNewPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            // Error Message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Changes Button inside AppCard
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onSaveClicked(
                            newUserName.value,
                            oldPassword.value,
                            newPassword.value,
                            repeatNewPassword.value
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSettingsScreenPreview() {
    val navController = rememberNavController()
    ProfileSettingsScreen(navController)
}

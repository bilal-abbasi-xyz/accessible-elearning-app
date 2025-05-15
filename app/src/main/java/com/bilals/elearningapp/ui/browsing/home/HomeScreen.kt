package com.bilals.elearningapp.ui.contentCreation.browsing.home

import HomeViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.SessionManager
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.HorizontalDividerWithDots
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {

    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel()
    val buttonLabels by viewModel.buttonLabels.observeAsState(emptyList())

    // Declare a state for the login/logout button
    val logInOutButtonLabel = remember { mutableStateOf("Log in") }

    // Update button label based on user login status
    LaunchedEffect(user) {
        logInOutButtonLabel.value = if (user != null) "Log out" else "Log in"
    }
    val userId = SessionManager.getUserIdFromPreferences(context)
    Log.d("UserInHomeScreen", "User: $user")

    // Handle log in/out actions
    val handleLogInOut = {
        if (user != null) {
            // Log out the user
            FirebaseAuth.getInstance().signOut()
            SessionManager.clearUserIdFromPreferences(context)
            Toast.makeText(context, "Logout successful!", Toast.LENGTH_SHORT).show()
            logInOutButtonLabel.value = "Log in"  // Change button label to "Log in"
        } else {
            // Navigate to the login screen
            navController.navigate(ScreenRoutes.Login.route)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.announceHomeScreen(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppBar(title = "Home") {
                navController.popBackStack()
            }

            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDividerWithDots()

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(40.dp) // Increased space between rows
            ) {

                // Include the log in/logout button inside the grid of buttons
                val updatedButtonLabels = buttonLabels.toMutableList().apply {
                    add(logInOutButtonLabel.value) // Add the log in/out button at the end of the list
                }

                val rows = updatedButtonLabels.chunked(2) // Group buttons into pairs of two

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Each item takes equal width
                            ) {
                                HomeScreenButton(label) {
                                    if (label == "Log in" || label == "Log out") {
                                        handleLogInOut() // Handle the login/logout action
                                    } else {
                                        navigateToScreen(label, navController)
                                    }
                                }
                            }
                        }

                        // If only one item in last row, add empty space to balance
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDividerWithDots()
        }
    }
}

@Composable
fun HomeScreenButton(label: String, onClick: () -> Unit) {
    val cardWidth = LocalConfiguration.current.screenWidthDp.dp / 2 + 10.dp // Two items per row
//    val cardWidth = 20.dp

    val cardHeight = cardWidth / 1.618f // Golden ratio height

    AppCard(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable {
                onClick()
            }
            .semantics { contentDescription = "" } // ⛔ Prevents TalkBack from reading automatically

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val adjustedTextSize = if (label.length > 15) 12.sp else 17.sp

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = when (label) {
                            "Browse Courses" -> Icons.Default.Book
                            "Settings" -> Icons.Default.Settings
                            "Public Forum" -> Icons.Default.ChatBubble
                            "Login" -> Icons.Default.Person
                            "Report" -> Icons.Default.Description
                            else -> Icons.AutoMirrored.Filled.Help
                        },
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = adjustedTextSize),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun navigateToScreen(label: String, navController: NavController) {
    when (label) {
        "Browse Courses" -> navController.navigate(ScreenRoutes.CategoryList.route)
        "Settings" -> navController.navigate(ScreenRoutes.Settings.route)
        "Public Forum" -> navController.navigate(ScreenRoutes.PublicForum.route)
        "Training" -> navController.navigate(ScreenRoutes.Training.route)
        "Login" -> navController.navigate(ScreenRoutes.Login.route)
        "Reports" -> navController.navigate(ScreenRoutes.Report.route)
    }
}



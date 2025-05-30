package com.bilals.elearningapp.ui.settings.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.SessionManager
import com.bilals.elearningapp.data.model.RoleType
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = SessionManager.getUserIdFromPreferences(context)
    val activeRole = remember { mutableStateOf(SessionManager.getActiveRole(context)) }

    // Determine button text
    val switchRoleText = if (userId.isNullOrBlank()) {
        "Log in for Instructor Role"
    } else {
        if (activeRole.value == RoleType.STUDENT) "Switch to Instructor Role"
        else "Switch to Student Role"
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(title = "Settings") { navController.popBackStack() }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally // <-- This centers the inner Column itself

            ) {
                // Buttons list
                val buttons = listOf(
                    "Profile" to Icons.Default.Person,
                    "Page UI" to Icons.Default.Layers,
                    switchRoleText to Icons.Default.Sync
                )

                buttons.forEach { (label, icon) ->
                    SettingsScreenButton(
                        label = label,
                        icon = icon
                    ) {
                        when (label) {
                            "Log in for Instructor Role" -> {
                                navController.navigate(ScreenRoutes.Login.route)
                            }

                            "Switch to Instructor Role" -> {
                                SessionManager.saveActiveRole(
                                    RoleType.INSTRUCTOR,
                                    context
                                )
                                activeRole.value = RoleType.INSTRUCTOR
                                navController.navigate(ScreenRoutes.InstructorHome.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }

                            "Switch to Student Role" -> {
                                SessionManager.saveActiveRole(
                                    RoleType.STUDENT,
                                    context
                                )
                                activeRole.value = RoleType.STUDENT
                                navController.navigate(ScreenRoutes.Home.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }

                            else -> {
                                navigateToSettingsScreen(label, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreenButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val cardHeight = 200.dp / 1.618f

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clearAndSetSemantics {
                role = Role.Button
                contentDescription = label
                onClick { onClick(); true }
            }
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun navigateToSettingsScreen(label: String, navController: NavController) {
    when (label) {
        "Profile" -> navController.navigate(ScreenRoutes.ProfileSettings.route)
        "Voice Commands" -> navController.navigate(ScreenRoutes.VoiceSettings.route)
        "Page UI" -> navController.navigate(ScreenRoutes.UISettings.route)
        "Switch Role" -> { /* Handle Switch Role logic here */
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    val navController = NavController(LocalContext.current)
    SettingsScreen(navController)
}

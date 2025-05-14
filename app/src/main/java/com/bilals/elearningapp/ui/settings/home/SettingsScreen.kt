package com.bilals.elearningapp.ui.settings.home

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.data.model.RoleType
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.SessionManager
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
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {
            AppBar(title = "Settings") { navController.popBackStack() }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 200.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
//                Spacer(modifier = Modifier.height(40.dp))

                // Buttons list
                val buttons = listOf(
                    "Profile" to Icons.Default.Person,
//                    "Voice Commands" to Icons.Default.Mic,
                    "Page UI" to Icons.Default.Layers,
                    switchRoleText to Icons.Default.Sync
                )

                val rows = buttons.chunked(2) // Create rows of two buttons

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { (label, icon) ->
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                SettingsScreenButton(
                                    label = label,
                                    icon = icon,
                                    onClick = {
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
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    } // Clears the entire back stack
                                                    launchSingleTop =
                                                        true // Prevents duplicate instances
                                                }
                                            }

                                            "Switch to Student Role" -> {
                                                SessionManager.saveActiveRole(
                                                    RoleType.STUDENT,
                                                    context
                                                )
                                                activeRole.value = RoleType.STUDENT
                                                navController.navigate(ScreenRoutes.Home.route) {
                                                    popUpTo(0) {
                                                        inclusive = true
                                                    } // Clears the entire back stack
                                                    launchSingleTop =
                                                        true // Prevents duplicate instances
                                                }
                                            }


                                            else -> {
                                                navigateToSettingsScreen(label, navController)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f)) // Balance layout
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
    val cardWidth = 200.dp
//    val cardWidth = LocalConfiguration.current.screenWidthDp.dp / 2 + 10.dp // Two items per row
    val cardHeight = cardWidth / 1.618f // Golden ratio height

    AppCard(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { onClick() }
            .semantics { contentDescription = "" } // ⛔ Prevents TalkBack from reading automatically

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
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
                        imageVector = icon,
                        contentDescription = "",
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

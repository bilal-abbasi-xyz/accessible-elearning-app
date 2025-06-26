package com.bilals.elearningapp.ui.instructor

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.navigation.ScreenRoutes
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.HorizontalDividerWithDots

@Composable
fun InstructorHomeScreen(navController: NavController) {

    val context = LocalContext.current

    // Calculate the card width and height based on the screen width and golden ratio
    val cardWidth = (LocalConfiguration.current.screenWidthDp.dp / 2) + 10.dp // Two items per row
    val cardHeight = cardWidth / 1.618f // Golden ratio height

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA))
    ) {
        AppBar(title = "Home") { navController.popBackStack() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalDividerWithDots()

            Spacer(modifier = Modifier.height(40.dp))

            // Create the AppCard for the "View courses available for creation" button
            AppCard(
                modifier = Modifier
                    .width(cardWidth) // Use the calculated card width
                    .height(cardHeight) // Use the calculated card height
                    .clickable { navController.navigate(ScreenRoutes.UnpublishedCourseList.route) }
            ) {
                // Inside the AppCard, display the text for the button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight) // Constrain the height of the content inside the card
                        .padding(16.dp), // Padding inside the card
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "View courses available for creation",
                        style = AppTypography.bodyMedium.copy(color = Color.White),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Add a Settings button with the appropriate icon
            AppCard(
                modifier = Modifier
                    .width(cardWidth) // Use the calculated card width
                    .height(cardHeight) // Use the calculated card height
                    .clickable { navController.navigate(ScreenRoutes.Settings.route) } // Navigate to Settings screen
            ) {
                // Inside the AppCard, display the text for the button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight) // Constrain the height of the content inside the card
                        .padding(16.dp), // Padding inside the card
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Settings",
                        style = AppTypography.bodyMedium.copy(color = Color.White),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDividerWithDots()
        }
    }
}


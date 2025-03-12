package com.bilals.elearningapp.ui.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.navigation.ScreenRoutes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars

@Composable
fun BottomNavBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp) // Increased height to prevent cutoff
            .background(Color(0xAA000000))
            .windowInsetsPadding(WindowInsets.navigationBars) // Prevent overlap
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize() // Ensures full height usage
                .padding(vertical = 4.dp), // Add padding inside Row
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Pair(Icons.Filled.Home, "Home") to ScreenRoutes.Home.route,
                Pair(Icons.Filled.Settings, "Settings") to ScreenRoutes.Settings.route
            ).forEach { (pair, route) ->
                val (icon, label) = pair

                Column(
                    modifier = Modifier
                        .clickable { navController.navigate(route) }
                        .padding(vertical = 4.dp), // Extra spacing to prevent cut-off
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp)) // Small gap between icon and text
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.IconButton
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.bilals.elearningapp.stt.SpeechInputHandler

@Composable
fun BottomNavBar(
    navController: NavController,
    speechInputHandler: SpeechInputHandler
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xCC000000), shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),  // generous horizontal padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ---- Home ----
            IconButton(
                onClick = { navController.navigate(ScreenRoutes.Home.route) },
                modifier = Modifier
                    .semantics { contentDescription = "Go to Home" }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // ---- Voice Command ----
            IconButton(
                onClick = { speechInputHandler.startListening() },
                modifier = Modifier
                    .semantics {
                        contentDescription = "Use a voice command"
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0x33FFFFFF), shape = CircleShape)
                        .padding(6.dp)
                )
            }

            // ---- Settings ----
            IconButton(
                onClick = { navController.navigate(ScreenRoutes.Settings.route) },
                modifier = Modifier
                    .semantics { contentDescription = "Go to Settings" }
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

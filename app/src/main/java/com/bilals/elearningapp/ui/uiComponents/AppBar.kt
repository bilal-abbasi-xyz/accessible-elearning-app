package com.bilals.elearningapp.ui.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.theme.LocalAppCardContainerColor
import com.bilals.elearningapp.ui.theme.LocalAppCardTextColor
import com.bilals.elearningapp.ui.theme.PrimaryBlack
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription

@Composable
fun AppBar(title: String, onBackClick: () -> Unit) {
    val containerColor = LocalAppCardContainerColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != "Home") {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        } else {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Full-width title semantics
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clearAndSetSemantics {
                    contentDescription = title.uppercase()
                }
                .padding(end = 16.dp), // optional padding
            contentAlignment = Alignment.CenterStart
        ) {
            val adjustedTextSize = if (title.length > 22) 16.sp else 25.sp
            Text(
                text = title.uppercase(),
                style = AppTypography.titleMedium.copy(fontSize = adjustedTextSize),
                color = LocalAppCardTextColor.current
            )
        }
    }
}

package com.bilals.elearningapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = PrimaryBlack,
            secondary = LightGray,
            background = AccentColor
        ),
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

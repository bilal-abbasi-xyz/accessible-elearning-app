package com.bilals.elearningapp.ui.theme

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColorPattern(
    val background: Color,
    val cardColor: Color,
    val textOnCard: Color
)

object AppColors {
    private val patterns = listOf(
        AppColorPattern(Color.White, Color.Black, Color.White),          // 0
        AppColorPattern(Color(0xFFE0F7FA), Color(0xFF006064), Color.White), // 1
        AppColorPattern(Color(0xFFFFF3E0), Color(0xFFBF360C), Color.White), // 2
        AppColorPattern(Color(0xFFF3E5F5), Color(0xFF4A148C), Color.White), // 3
        AppColorPattern(Color(0xFFE8F5E9), Color(0xFF1B5E20), Color.White), // 4
        AppColorPattern(Color(0xFFFFEBEE), Color(0xFFB71C1C), Color.White)  // 5
    )

    private val _patternIndex = mutableStateOf(0)
    val currentPattern: State<AppColorPattern> = derivedStateOf { patterns[_patternIndex.value] }

    fun setPattern(index: Int) {
        _patternIndex.value = index.coerceIn(patterns.indices)
    }
}



val LocalAppCardTextColor = staticCompositionLocalOf { Color.White }
val LocalAppCardContainerColor = staticCompositionLocalOf { Color.Black }

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val pattern = AppColors.currentPattern.value
    CompositionLocalProvider(
        LocalAppCardContainerColor provides pattern.cardColor,
        LocalAppCardTextColor provides pattern.textOnCard,
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = PrimaryBlack,
                secondary = LightGray,
                surface = PrimaryBlack,

                background = pattern.background
            ),
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

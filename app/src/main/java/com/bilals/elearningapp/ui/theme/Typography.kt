package com.bilals.elearningapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.bilals.elearningapp.R

val lexendFont = FontFamily(Font(R.font.lexend))
val josefinSansFont = FontFamily(Font(R.font.josefin_sans))
//
//val lexendFont = FontFamily.Serif // Use this for previewing
//val josefinSansFont = FontFamily.Serif
 //
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = lexendFont,
        fontSize = 20.sp

    ),
    bodySmall = TextStyle(
        fontFamily = lexendFont,
        fontSize = 12.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = lexendFont,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = josefinSansFont,
        fontSize = 40.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontFamily = josefinSansFont,
        fontSize = 30.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    headlineMedium = TextStyle(
        fontFamily = lexendFont,
        fontSize = 24.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
)

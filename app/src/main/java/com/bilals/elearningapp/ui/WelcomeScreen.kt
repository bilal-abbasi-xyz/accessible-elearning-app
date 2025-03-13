package com.bilals.elearningapp.ui.welcomeScreen

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.navigation.ScreenRoutes

@Composable
fun WelcomeScreen(navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }
    var isFadingOut by remember { mutableStateOf(false) }

    // Fade-in animation
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = LinearEasing),
        label = "fadeAnimation"
    )

    // Fade-out animation
    val fadeOutAlphaAnimation by animateFloatAsState(
        targetValue = if (isFadingOut) 0f else 1f,
        animationSpec = tween(durationMillis = 1200, easing = LinearEasing),
        label = "fadeOutAnimation"
    )

    // Start animation
    LaunchedEffect(Unit) {
        isVisible = true
        Handler(Looper.getMainLooper()).postDelayed({
            isFadingOut = true
            Handler(Looper.getMainLooper()).postDelayed({
                navController.navigate(ScreenRoutes.Home.route) {
                    popUpTo(ScreenRoutes.WelcomeScreen.route) { inclusive = true } // Destroy screen
                }
            }, 1200) // Delay to allow the fade-out to complete before navigating
        }, 1500) // Delay before fade-out starts
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .alpha(fadeOutAlphaAnimation), // Apply fade-out animation
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom Design Elements
            GeometricDesign()

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Message
            Text(
                text = "Welcome to our FYP Project",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 28.sp,
                    color = Color.White
                )
            )

            Text(
                text = "eLearning App for Visually Impaired!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun GeometricDesign() {
    Canvas(modifier = Modifier.size(150.dp)) {
        val center = Offset(size.width / 2, size.height / 2)

        // Outer Circle
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = size.width / 2,
            style = Stroke(width = 3.dp.toPx())
        )

        // Inner Dots
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = center
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = 4.dp.toPx(),
            center = Offset(center.x - 30f, center.y - 30f)
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = 4.dp.toPx(),
            center = Offset(center.x + 30f, center.y + 30f)
        )

        // Connecting Lines
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(center.x - 30f, center.y - 30f),
            end = center,
            strokeWidth = 2.dp.toPx()
        )

        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(center.x + 30f, center.y + 30f),
            end = center,
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    val navController = rememberNavController()
    WelcomeScreen(navController = navController)
}


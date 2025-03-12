package com.bilals.elearningapp.ui.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.bilals.elearningapp.ui.theme.AppTypography
@Composable
fun SectionHeading(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { this.isTraversalGroup = true }
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Gray, Color.Transparent)))
            )

            // Heading Text
            Text(
                text = text.uppercase(), // Optional: Make it feel more structured
                style = AppTypography.titleLarge,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Right Line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, Color.Gray)))
            )
        }
    }
}


package com.bilals.elearningapp.ui.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bilals.elearningapp.ui.theme.PrimaryBlack


@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null, // Optional click action
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp) // Ensuring spacing around each card
            .let { baseModifier ->
                if (onClick != null) baseModifier.clickable { onClick() } else baseModifier
            }, // Only apply clickable modifier if onClick is provided
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlack)
    ) {
        Box(modifier = Modifier.padding(0.dp)) { // Padding inside the card
            content()
        }
    }
}


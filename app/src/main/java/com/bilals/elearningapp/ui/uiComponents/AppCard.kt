package com.bilals.elearningapp.ui.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bilals.elearningapp.ui.theme.AppColors
import com.bilals.elearningapp.ui.theme.LocalAppCardContainerColor

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    // read the current global card background color
    val containerColor = LocalAppCardContainerColor.current

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box { content() }
    }
}


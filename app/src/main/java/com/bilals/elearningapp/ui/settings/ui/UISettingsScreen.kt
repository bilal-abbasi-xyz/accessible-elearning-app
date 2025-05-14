package com.bilals.elearningapp.ui.settings.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.theme.AppColors
import com.bilals.elearningapp.ui.uiComponents.AppBar
import kotlin.math.roundToInt
@Composable
fun UISettingsScreen(navController: NavController) {
    val context = LocalContext.current

    var textZoom by remember { mutableFloatStateOf(1f) }
    var colorPatternIndex by remember { mutableIntStateOf(0) }
    var magnificationEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppBar(title = "Page UI Settings") {
            navController.popBackStack()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp), // Balanced vertical padding
            verticalArrangement = Arrangement.spacedBy(48.dp), // more vertical space
            horizontalAlignment = Alignment.Start // make everything left-aligned
        ) {
            // Magnification row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Magnification: ${if (magnificationEnabled) "On" else "Off"}",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = magnificationEnabled,
                    onCheckedChange = {
                        magnificationEnabled = it
                        if (it) {
                            SpeechService.announce(context, "Triple tap to magnify")
                        }
                    }
                )
            }

            // Color pattern
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Color Pattern: $colorPatternIndex",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = colorPatternIndex.toFloat(),
                    onValueChange = {
                        val index = it.roundToInt().coerceIn(0, 5)
                        colorPatternIndex = index
                        AppColors.setPattern(index)
                    },
                    valueRange = 0f..5f,
                    steps = 4
                )
            }
        }
    }
}

package com.bilals.elearningapp.ui.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.theme.AppColors
import com.bilals.elearningapp.ui.theme.fontNames
import com.bilals.elearningapp.ui.theme.fontOptions
import com.bilals.elearningapp.ui.uiComponents.AppBar
import kotlin.math.roundToInt
@Composable
fun UISettingsScreen(
    navController: NavController,
    uiSettings: UISettingsViewModel
) {
    val context = LocalContext.current
    val fontIndex = uiSettings.chosenFontIndex
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
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Magnification section: text on one line, switch on the next
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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

            // Font chooser
            Column {
                Text("Choose Font", style = MaterialTheme.typography.titleMedium)
                Slider(
                    value = fontIndex.toFloat(),
                    onValueChange = {
                        val idx = it.roundToInt().coerceIn(0, fontOptions.lastIndex)
                        uiSettings.setFontIndex(idx)
                    },
                    valueRange = 0f..fontOptions.lastIndex.toFloat(),
                    steps = fontOptions.size - 2
                )
                Text(fontNames[fontIndex], style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

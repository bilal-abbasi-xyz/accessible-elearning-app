package com.bilals.elearningapp.ui.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
            // 1) Magnification row as before…
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        role = Role.Switch
                        contentDescription = "Magnification ${if (magnificationEnabled) "On" else "Off"}"
                        onClick {
                            magnificationEnabled = !magnificationEnabled
                            if (magnificationEnabled) {
                                SpeechService.announce(context, "Triple tap to magnify")
                            }
                            true
                        }
                    }
                    .clickable {
                        magnificationEnabled = !magnificationEnabled
                        if (magnificationEnabled) SpeechService.announce(context, "Triple tap to magnify")
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Magnification", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = magnificationEnabled,
                    onCheckedChange = null,
                    modifier = Modifier.semantics { invisibleToUser() }
                )
            }

            // 2) Color Pattern: hide the label, expose only slider
            Text(
                text = "Color Pattern: $colorPatternIndex",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clearAndSetSemantics { } // hide this Text
            )
            Slider(
                value = colorPatternIndex.toFloat(),
                onValueChange = {
                    val idx = it.roundToInt().coerceIn(0, 5)
                    colorPatternIndex = idx
                    AppColors.setPattern(idx)
                },
                valueRange = 0f..5f,
                steps = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        role = Role.ValuePicker
                        contentDescription = "Color Pattern"
                    }
            )

            // 3) Font chooser: hide the label & name, expose only slider
            Text(
                text = "Choose Font: ${fontNames[fontIndex]}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clearAndSetSemantics { }
            )
            Slider(
                value = fontIndex.toFloat(),
                onValueChange = {
                    val idx = it.roundToInt().coerceIn(0, fontOptions.lastIndex)
                    uiSettings.setFontIndex(idx)
                },
                valueRange = 0f..fontOptions.lastIndex.toFloat(),
                steps = fontOptions.size - 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        role = Role.ValuePicker
                        contentDescription = "Choose Font"
                    }
            )
        }
    }
}

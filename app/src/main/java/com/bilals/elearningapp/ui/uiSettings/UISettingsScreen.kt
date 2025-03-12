package com.bilals.elearningapp.ui.uiSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import kotlin.math.roundToInt
@Composable
fun UISettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: UISettingsViewModel = viewModel()

    LaunchedEffect(Unit) {
        val announcement = "UI Settings. 3 items available."
        SpeechService.announce(context, announcement)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // AppBar
        AppBar("UI Settings") { navController.popBackStack() }

        Spacer(modifier = Modifier.height(16.dp))

        // Zoom Level (commented code can be used later if needed)
        // Text(text = "Zoom Level: ${pageUISettings.zoomLevel}")
        // Slider(
        //     value = pageUISettings.zoomLevel,
        //     onValueChange = { newValue -> viewModel.updateZoomLevel((newValue * 10).roundToInt() / 10f) },
        //     valueRange = 0.5f..2.0f,
        //     steps = 4,
        //     modifier = Modifier.fillMaxWidth()
        // )

        // High Contrast Mode Setting
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Handle high contrast toggle action
                    viewModel.toggleHighContrast()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "High Contrast Mode",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                // Replace the Switch with a Text indicating the state
//                Text(
//                    text = if (viewModel.isHighContrastEnabled) "Enabled" else "Disabled",
//                    style = MaterialTheme.typography.bodyMedium
//                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color Scheme Dropdown
        Text(text = "Color Scheme:", style = MaterialTheme.typography.bodyLarge)

//        IntegratedDropdown(
//            selectedItem = viewModel.selectedColorScheme,
//            onItemSelected = { selected -> viewModel.changeColorScheme(selected) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp)
//        )
    }
}

@Composable
fun IntegratedDropdown(
    modifier: Modifier = Modifier,
    selectedItem: ColorSchemeOption,
    onItemSelected: (ColorSchemeOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedItem.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "▼",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
        ) {
            ColorSchemeOption.values().forEach { colorScheme ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(colorScheme)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = colorScheme.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedItem == colorScheme) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UISettingsScreenPreview() {
    val navController = rememberNavController()
    UISettingsScreen(navController)
}

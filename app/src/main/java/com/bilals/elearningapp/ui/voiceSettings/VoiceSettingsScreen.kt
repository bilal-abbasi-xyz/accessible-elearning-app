package com.bilals.elearningapp.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.voiceSettings.VoiceSettingsViewModel
@Composable
fun VoiceSettingsScreen(navController: NavHostController) {
//    val context = LocalContext.current
//    val viewModel: VoiceSettingsViewModel = viewModel()
//
//    val voiceCommands = viewModel.voiceCommands.collectAsState().value
//    val listeningState = viewModel.listeningState.value
//
//    if (listeningState) {
//        ListeningAlertDialog(onSave = {
//            viewModel.onSaveListeningCommand()
//        }, onCancel = {
//            viewModel.onCancelListeningCommand()
//        })
//    }
//
//    LaunchedEffect(Unit) {
//        val announcement = "Edit voice commands. ${voiceCommands.size} items available."
//        SpeechService.announce(context, announcement)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//    ) {
//        // AppBar
//        AppBar("Voice Settings") { navController.popBackStack() }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Display each voice command row
//        voiceCommands.forEach { command ->
//            VoiceSettingRow(
//                description = command.description,
//                voiceCommand = command.command,
//                onChangeClicked = { viewModel.onChangeVoiceCommand(command.key) }
//            )
//            Spacer(modifier = Modifier.height(16.dp)) // Adjusted vertical spacing
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//    }
//}
//
//@Composable
//fun VoiceSettingRow(
//    description: String,
//    voiceCommand: String,
//    onChangeClicked: () -> Unit
//) {
//    AppCard(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onChangeClicked() }
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = description,
//                modifier = Modifier.weight(1f),
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Text(
//                text = voiceCommand.uppercase(),
//                modifier = Modifier.weight(1f),
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//    }
//}
//
//@Composable
//fun ListeningAlertDialog(onSave: () -> Unit, onCancel: () -> Unit) {
//    AlertDialog(
//        onDismissRequest = onCancel,
//        title = { Text(text = "Listening...") },
//        text = {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "Please wait while we listen for a voice command.")
//            }
//        },
//        confirmButton = {
//            Button(onClick = onSave) {
//                Text(text = "Save")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onCancel) {
//                Text(text = "Cancel")
//            }
//        }
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun VoiceSettingsScreenPreview() {
//    val navController = rememberNavController()
//    VoiceSettingsScreen(navController)
}

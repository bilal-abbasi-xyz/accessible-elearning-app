package com.bilals.elearningapp.ui.training

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.tts.SpeechService

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.LectureType
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import io.noties.markwon.Markwon
import android.widget.TextView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.viewinterop.AndroidView
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.ui.contentCreation.createLecture.FormatButton
import com.bilals.elearningapp.ui.contentCreation.createLecture.TextStyleTag
import com.bilals.elearningapp.ui.contentCreation.createLecture.toggleLineMarkdown
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TrainingScreen(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var trainingStep by remember { mutableStateOf(1) } // 1: buttons, 2: quiz, 3: text input, 4: lecture-sim, 5: done
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    var textState by remember { mutableStateOf(TextFieldValue()) }
    var showPreview by remember { mutableStateOf(false) }
    var announceQueue by remember { mutableStateOf<String?>(null) }

    // queued announcements
    LaunchedEffect(announceQueue) {
        announceQueue?.let {
            SpeechService.announce(context, it)
            announceQueue = null
        }
    }

    // step-based announcements/transitions
    LaunchedEffect(trainingStep) {
        when (trainingStep) {
            1 -> SpeechService.announce(
                context,
                "Training part one. Hold and drag your finger to navigate. Double press on button 3 to select it."
            )
            2 -> SpeechService.announce(
                context,
                "Well done! Next, this screen displays a quiz. Read the question, choose the right answer and press next."
            )
            3 -> SpeechService.announce(
                context,
                "Perfect choice! Next, this screen displays a message field. Type hello and select the send button."
            )
            4 -> SpeechService.announce(
                context,
                "Great! Now we’ll simulate creating a lecture. Type some text, select a format on the right, then press Preview."
            )
            5 -> {
                // final pop already handled below
            }
        }
    }

    AppBar(title = "Training") { navController.popBackStack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        when (trainingStep) {
            // 1: button nav practice
            1 -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    (1..5).forEach { i ->
                        AppCard(
                            onClick = {
                                if (i == 3) trainingStep = 2
                                else SpeechService.announce(context, "Wrong button, try again")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Button $i", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                                )
                            }
                        }
                    }
                }
            }

            // 2: quiz
            2 -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "What is 2 + 2?",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                    )
                    listOf(2, 3, 4, 5).forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedAnswer == option,
                                onClick = { selectedAnswer = option }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option.toString(), fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (selectedAnswer == 4) trainingStep = 3
                        else SpeechService.announce(context, "Wrong answer, try again")
                    }) {
                        Text("Next")
                    }
                }
            }

            // 3: text input hello
            3 -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Type something:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (textInput.text.trim().equals("hello", ignoreCase = true)) {
                                trainingStep = 4
                            } else {
                                SpeechService.announce(context, "Incorrect input, try again")
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Send")
                        }
                    }
                }
            }

            // 4: simulate CreateLectureScreen
            4 -> {
                // mimic the editor + toolbar layout
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, Color.Gray)
                ) {
                    Box(
                        Modifier
                            .weight(4f)
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = textState,
                            onValueChange = { textState = it },
                            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFEEEEEE))
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FormatButton("Heading") {
                            textState = toggleLineMarkdown(TextStyleTag.Heading, textState)
                            SpeechService.announce(context, "Heading toggled")
                        }
                        FormatButton("Bold") {
                            textState = toggleLineMarkdown(TextStyleTag.Bold, textState)
                            SpeechService.announce(context, "Bold toggled")
                        }
                        // … add other style buttons as needed
                    }
                }
                // action row: New Line + Preview
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AppCard(onClick = {
                        val text = textState.text
                        val cursor = textState.selection.end
                        // find end of current line
                        val lineEnd = text.indexOf('\n', cursor).takeIf { it >= 0 } ?: text.length
                        // build new text
                        val updated = text.substring(0, lineEnd) + "\n" + text.substring(lineEnd)
                        // place cursor at start of the new (blank) line:
                        val newCursorPos = lineEnd + 1
                        textState = TextFieldValue(
                            text = updated,
                            selection = TextRange(newCursorPos, newCursorPos)
                        )
                        SpeechService.announce(context, "New line added")
                    }, modifier = Modifier.width(120.dp).height(56.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("New Line", fontSize = 18.sp, color = Color.White)
                        }
                    }

                    AppCard(onClick = {
                        showPreview = true
                        // if in training step 4, this means user followed instructions
                        announceQueue = "Perfect. Training complete."
                        // after announcement, wait 2s then pop
                        scope.launch {
                            delay(4000)
                            navController.popBackStack()
                        }
                    }, modifier = Modifier.width(120.dp).height(56.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Preview", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }

                if (showPreview) {
                    Dialog(onDismissRequest = { showPreview = false }) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 8.dp,
                            color = Color.Black.copy(alpha = 0.85f),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.8f)
                                .padding(16.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                val markwon = remember { Markwon.create(context) }
                                AndroidView(
                                    factory = { ctx ->
                                        TextView(ctx).apply {
                                            setTextColor(Color.White.toArgb())
                                        }
                                    },
                                    update = {  tv -> markwon.setMarkdown(tv, textState.text) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                AppCard(
                                    onClick = { showPreview = false },
                                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp)
                                ) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Close Preview", fontSize = 18.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

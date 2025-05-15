package com.bilals.elearningapp.ui.training

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.contentCreation.createLecture.ToolBtn
import com.bilals.elearningapp.ui.contentCreation.createLecture.WordByWordEditor
import com.bilals.elearningapp.ui.contentCreation.createLecture.toggleBoldSelection
import com.bilals.elearningapp.ui.contentCreation.createLecture.toggleHeading
import com.bilals.elearningapp.ui.contentCreation.createLecture.toggleList
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import io.noties.markwon.Markwon
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
    var textState by remember {
        mutableStateOf(
            TextFieldValue(
                // step 4 prefill:
                "Heading\n" +
                        "Bold text\n" +
                        "List item 1\n" +
                        "List item 2"
            )
        )
    }
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
                "Great! Now, format the text appropriately using the buttons on top, then select Preview.\"\n."
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
                                    "Button $i",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
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

            4 -> {
                // Prefilled textState is already:
                // "Heading\nBold text\nList item 1\nList item 2"

                // Show the formatter UI as a Dialog
                Dialog(onDismissRequest = { /* lock them in until they Preview successfully */ }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 8.dp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                            .padding(16.dp)
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            // Top toolbar: H, B, L
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEEEEEE))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ToolBtn("H", "Heading",
                                    onHover = { SpeechService.announce(context, "Heading") },
                                    onToggle = {
                                        val (s, msg) = toggleHeading(textState)
                                        textState = s
                                        SpeechService.announce(context, msg)
                                    }
                                )
                                ToolBtn("B", "Bold",
                                    onHover = { SpeechService.announce(context, "Bold") },
                                    onToggle = {
                                        val (s, msg) = toggleBoldSelection(textState)
                                        textState = s
                                        SpeechService.announce(context, msg)
                                    }
                                )
                                ToolBtn("L", "List",
                                    onHover = { SpeechService.announce(context, "List") },
                                    onToggle = {
                                        val (s, msg) = toggleList(textState)
                                        textState = s
                                        SpeechService.announce(context, msg)
                                    }
                                )
                            }

                            // Word-by-word editor
                            Box(
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            ) {
                                WordByWordEditor(textState) { textState = it }
                            }

                            // Preview button at bottom
                            AppCard(
                                onClick = {
                                    // Validate all four lines:
                                    val lines = textState.text.split("\n")
                                    when {
                                        !lines.getOrNull(0).orEmpty().startsWith("# ") ->
                                            SpeechService.announce(
                                                context,
                                                "First line must be a heading"
                                            )

                                        !lines.getOrNull(1).orEmpty()
                                            .matches(Regex("""\*\*.+\*\*""")) ->
                                            SpeechService.announce(
                                                context,
                                                "Second line must be bold"
                                            )

                                        !lines.getOrNull(2).orEmpty().startsWith("- ") ->
                                            SpeechService.announce(
                                                context,
                                                "Third line must be a list item"
                                            )

                                        !lines.getOrNull(3).orEmpty().startsWith("- ") ->
                                            SpeechService.announce(
                                                context,
                                                "Fourth line must be a list item"
                                            )

                                        else -> {
                                            // Success! Show the final markdown preview for 5s, then pop
                                            showPreview = true
                                            scope.launch {
                                                SpeechService.announce(
                                                    context,
                                                    "Great job! Training complete."
                                                )
                                                delay(5000)
                                                navController.popBackStack()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(16.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    Text("Preview", fontSize = 18.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Reuse your existing dark preview dialog here:
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
                                        TextView(ctx).apply { setTextColor(Color.White.toArgb()) }
                                    },
                                    update = { tv ->
                                        // normalize newlines
                                        val raw = textState.text
                                        val step1 = raw.replace("\\\\n", "\n\n")
                                        val display = step1.replace("\n", "\n\n")
                                        markwon.setMarkdown(tv, display)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                AppCard(
                                    onClick = { showPreview = false },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(top = 16.dp)
                                ) {
                                    Box(Modifier.fillMaxSize(), Alignment.Center) {
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

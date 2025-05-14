package com.bilals.elearningapp.ui.contentCreation.createLecture

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
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
import com.bilals.elearningapp.tts.SpeechService
import kotlinx.coroutines.launch

@Composable
fun CreateLectureScreen(
    navController: NavController,
    lectureId: String,
    lectureName: String,
    sectionId: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = appContainer.lectureRepository

    var textState by remember { mutableStateOf(TextFieldValue()) }
    var showPreview by remember { mutableStateOf(false) }

Log.d("sectionId", "sectionId: $sectionId")
    Log.d("lectureId", "lectureId: $lectureId")
    LaunchedEffect(Unit) {
        repository.getLectureById(lectureId)?.let {
            // decode each “\n\n” back into a real newline
            val decoded = it.content.replace("\\n", "\n")
            textState = TextFieldValue(decoded)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBar(title = lectureName) { navController.popBackStack() }

        // Main editor and toolbar
        Row(
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Gray) // border around section
        ) {
            Box(
                modifier = Modifier
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
                        modifier = Modifier
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
                        FormatButton("Paragraph") {
                            textState = toggleLineMarkdown(TextStyleTag.Paragraph, textState)
                            SpeechService.announce(context, "Paragraph toggled")
                        }
                        FormatButton("Bold") {
                            textState = toggleLineMarkdown(TextStyleTag.Bold, textState)
                            SpeechService.announce(context, "Bold toggled")
                        }
                        FormatButton("Italic") {
                            textState = toggleLineMarkdown(TextStyleTag.Italic, textState)
                            SpeechService.announce(context, "Italic toggled")
                        }
                        FormatButton("Underline") {
                            textState = toggleLineMarkdown(TextStyleTag.Underline, textState)
                            SpeechService.announce(context, "Underline toggled")
                        }

                        // NEW LINE BUTTON
                        AppCard(
                            onClick = {
                                val fullText = textState.text
                                val cursor = textState.selection.end
                                // find end of current line
                                val lineEnd = fullText.indexOf('\n', cursor).takeIf { it >= 0 } ?: fullText.length
                                // insert newline at end of that line
                                val updated = fullText.substring(0, lineEnd) + "\n" + fullText.substring(lineEnd)
                                // move cursor to start of new blank line
                                val newPos = lineEnd + 1
                                textState = TextFieldValue(
                                    text = updated,
                                    selection = TextRange(newPos, newPos)
                                )
                                SpeechService.announce(context, "New line added")
                            },
                            modifier = Modifier
                                .width(125.dp)
                                .height(56.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("New Line", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }

        }

        // Separator between editor and actions
        HorizontalDivider(thickness = 2.dp, color = Color.DarkGray)

        // Preview/Save buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, brush = SolidColor(Color.Gray), shape = RectangleShape)
                .clip(RectangleShape)

                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppCard(
                onClick = { showPreview = true },
                modifier = Modifier
                    .width(150.dp) // increased width
                    .height(56.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Preview", fontSize = 18.sp, color = Color.White)
                }
            }
            AppCard(
                onClick = {
                    scope.launch {
                        // encode each real newline as the literal “\n\n”
                        val encoded = textState.text.replace("\n", "\\n\\n")
                        repository.updateLecture(
                            Lecture(
                                id = lectureId,
                                name = lectureName,
                                content = encoded,
                                sectionId = sectionId,
                                type = LectureType.TEXT
                            )
                        )

                        navController.popBackStack()
                    }
                }
                ,
                modifier = Modifier
                    .width(150.dp)
                    .height(56.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Save", fontSize = 18.sp, color = Color.White)
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
                        modifier = Modifier
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
                            update = { tv ->
// replace any literal “\n\n” sequences with actual double-newlines
                                var display = textState.text.replace("\\n\n", "\n\n")
                                 display = textState.text.replace("\n", "\n\n")

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
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Close Preview", fontSize = 18.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}



fun toggleLineMarkdown(tag: TextStyleTag, text: TextFieldValue): TextFieldValue {
    val full = text.text
    val cursor = text.selection.start
    val lineStart = full.lastIndexOf('\n', cursor - 1).let { if (it == -1) 0 else it + 1 }
    val lineEnd = full.indexOf('\n', cursor).let { if (it == -1) full.length else it }
    val line = full.substring(lineStart, lineEnd)
    val (prefix, suffix) = when (tag) {
        TextStyleTag.Bold -> "**" to "**"
        TextStyleTag.Italic -> "*" to "*"
        TextStyleTag.Underline -> "__" to "__"
        TextStyleTag.Heading -> "# " to ""
        TextStyleTag.Paragraph -> "" to "\n"
    }
    val toggled = if (line.startsWith(prefix) && (suffix.isEmpty() || line.endsWith(suffix))) {
        // remove
        line.removePrefix(prefix).removeSuffix(suffix)
    } else buildString {
        append(prefix)
        append(line)
        append(suffix)
    }
    val newText = full.replaceRange(lineStart, lineEnd, toggled)
    return text.copy(text = newText, selection = text.selection)
}

enum class TextStyleTag {
    Heading, Paragraph, Bold, Italic, Underline
}

@Composable
fun FormatButton(label: String, onClick: () -> Unit) {
    AppCard(
        onClick = onClick,
        modifier = Modifier
            .width(125.dp)
            .height(70.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = label, fontSize = 10.sp, color = Color.White)
        }
    }
}

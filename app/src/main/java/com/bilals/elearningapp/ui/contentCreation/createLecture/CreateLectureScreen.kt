package com.bilals.elearningapp.ui.contentCreation.createLecture

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bilals.elearningapp.data.model.Lecture
import com.bilals.elearningapp.data.model.LectureType
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.uiComponents.AppCard
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch
@Composable
fun CreateLectureScreen(
    navController: NavController,
    lectureId: String,
    lectureName: String,
    sectionId: String,
    appContainer: AppContainer
) {
    val context   = LocalContext.current
    val scope     = rememberCoroutineScope()
    val repo      = appContainer.lectureRepository

    var textState     by remember { mutableStateOf(TextFieldValue()) }
    var showFormatter by remember { mutableStateOf(false) }
    var showPreview   by remember { mutableStateOf(false) }

    // 1) Load & decode (“\n\n” → real newline)
    LaunchedEffect(lectureId) {
        repo.getLectureById(lectureId)?.let {
            textState = TextFieldValue(it.content.replace("\\n\\n", "\n"))
        }
    }


    Column(Modifier.fillMaxSize()) {
        // 2) Top toolbar: only “N” centered
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE))
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ToolBtn(
                label       = "N",
                description = "New line",
                onHover     = { SpeechService.announce(context, "New line") },
                onToggle    = {
                    val full    = textState.text
                    val cur     = textState.selection.end
                    val lineEnd = full.indexOf('\n', cur).takeIf { it >= 0 } ?: full.length
                    val upd     = full.substring(0, lineEnd) + "\n" + full.substring(lineEnd)
                    val pos     = lineEnd + 1
                    textState   = TextFieldValue(upd, TextRange(pos, pos))
                    SpeechService.announce(context, "New line inserted")
                }
            )
        }

        // 3) Main editor: a normal TextField
        Box(
            Modifier
                .weight(1f)
                .border(2.dp, Color.Gray, RectangleShape)
                .padding(16.dp)
        ) {
            TextField(
                value         = textState,
                onValueChange = { textState = it },
                modifier      = Modifier.fillMaxSize(),
                textStyle     = LocalTextStyle.current.copy(fontSize = 18.sp),
                singleLine    = false,
                maxLines      = Int.MAX_VALUE
            )
        }

        // 4) Bottom row: Formatter + Save
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppCard(
                onClick  = { showFormatter = true },
                modifier = Modifier.weight(1f).height(56.dp).padding(end = 8.dp)
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Formatter", fontSize = 18.sp, color = Color.White)
                }
            }
            AppCard(
                onClick  = {
                    scope.launch {
                        val encoded = textState.text.replace("\n", "\\n\\n")
                        repo.updateLecture(
                            Lecture(lectureId, lectureName, encoded, LectureType.TEXT, sectionId)
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.weight(1f).height(56.dp).padding(start = 8.dp)
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Save", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }

    // 5) Formatter dialog (H, B, L + WordByWordEditor + Close/Preview)
    if (showFormatter) {
        Dialog(onDismissRequest = { showFormatter = false }) {
            Surface(
                shape          = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                color          = Color.White,
                modifier       = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp)
            ) {
                Column(Modifier.fillMaxSize()) {
                    // 5a) H, B, L toolbar
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEEEEE))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ToolBtn("H","Heading",
                            onHover  = { SpeechService.announce(context,"Heading") },
                            onToggle = {
                                val (s,msg) = toggleHeading(textState)
                                textState = s
                                SpeechService.announce(context,msg)
                            }
                        )
                        ToolBtn("B","Bold",
                            onHover  = { SpeechService.announce(context,"Bold") },
                            onToggle = {
                                val (s,msg) = toggleBoldSelection(textState)
                                textState = s
                                SpeechService.announce(context,msg)
                            }
                        )
                        ToolBtn("L","List",
                            onHover  = { SpeechService.announce(context,"List") },
                            onToggle = {
                                val (s,msg) = toggleList(textState)
                                textState = s
                                SpeechService.announce(context,msg)
                            }
                        )
                    }

                    // 5b) Word-by-word selectable editor
                    Box(Modifier.weight(1f).padding(8.dp)) {
                        WordByWordEditor(textState) { textState = it }
                    }

                    // 5c) Close & Preview buttons
                    Column(
                        Modifier.fillMaxWidth().padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppCard(onClick = { showFormatter = false },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                Text("Close", fontSize = 18.sp, color = Color.White)
                            }
                        }
                        AppCard(onClick = {
                            showFormatter = false
                            showPreview   = true
                        },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                Text("Preview", fontSize = 18.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // 6) Dark markdown preview
    if (showPreview) {
        Dialog(onDismissRequest = { showPreview = false }) {
            Surface(
                shape          = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                color          = Color.Black.copy(alpha = 0.85f),
                modifier       = Modifier
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
                            // replace literal "\\n" → real double-newlines, then single "\n" → double
                            val step1   = textState.text.replace("\\\\n", "\n\n")
                            val display = step1.replace("\n", "\n\n")
                            markwon.setMarkdown(tv, display)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AppCard(onClick = { showPreview = false },
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp)
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

// ——— Helper Composables & functions ———

@Composable
fun ToolBtn(
    label: String,
    description: String,
    onToggle: () -> Unit,
    onHover: () -> Unit
) {
    val context = LocalContext.current
    AppCard(
        onClick = onToggle,
        modifier = Modifier
            .size(70.dp)
            .semantics { contentDescription = description }
            .focusable()
            .onFocusChanged { if (it.isFocused) onHover() }
    ) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(label, fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
fun WordByWordEditor(
    textState: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    val context = LocalContext.current
    val lines = remember(textState.text) { textState.text.split("\n") }
    var offset by remember { mutableStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        offset = 0
        lines.forEachIndexed { li, line ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)) {
                val words = line.split(" ")
                words.forEachIndexed { wi, w ->
                    val start = offset
                    Text(
                        text = w,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .semantics { contentDescription = w }
                            .focusable()
                            .onFocusChanged { state ->
                                if (state.isFocused) {
                                    SpeechService.announce(context, "$w selected")
                                }
                            }
                            .clearAndSetSemantics {
                                // so the entire word is one node
                                contentDescription = w
                            }
                            .clickable {
                                onValueChange(
                                    TextFieldValue(
                                        textState.text,
                                        TextRange(start, start + w.length)
                                    )
                                )
                                SpeechService.announce(context, "$w selected")
                            }
                    )
                    offset += w.length
                    if (wi < words.lastIndex) {
                        // space token
                        Text(
                            text = " ",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .semantics { contentDescription = "space" }
                                .focusable()
                                .clickable {
                                    onValueChange(
                                        TextFieldValue(
                                            textState.text,
                                            TextRange(offset, offset)
                                        )
                                    )
                                    SpeechService.announce(context, "space")
                                }
                        )
                        offset += 1
                    }
                }
                if (li < lines.lastIndex) offset += 1 // account for '\n'
            }
        }
    }
}

// Toggle helpers operate on selection
fun toggleHeading(state: TextFieldValue): Pair<TextFieldValue, String> {
    val t = state.text
    val s = state.selection
    val ls = t.lastIndexOf('\n', s.start - 1).let { if (it < 0) 0 else it + 1 }
    val le = t.indexOf('\n', s.start).let { if (it < 0) t.length else it }
    val line = t.substring(ls, le)
    return if (line.startsWith("# ")) {
        val nl = line.removePrefix("# ")
        val nt = t.replaceRange(ls, le, nl)
        val pos = (s.start - 2).coerceAtLeast(ls)
        TextFieldValue(nt, TextRange(pos, pos)) to "Heading removed"
    } else {
        val nl = "# $line"
        val nt = t.replaceRange(ls, le, nl)
        val pos = le + 2
        TextFieldValue(nt, TextRange(pos, pos)) to "Heading added"
    }
}

fun toggleBoldSelection(state: TextFieldValue): Pair<TextFieldValue, String> {
    val t = state.text;
    val s = state.selection
    if (s.start == s.end) return state to "No word selected"
    val sel = t.substring(s.start, s.end)
    return if (sel.startsWith("**") && sel.endsWith("**")) {
        val un = sel.removePrefix("**").removeSuffix("**")
        val nt = t.replaceRange(s.start, s.end, un)
        val ne = s.start + un.length
        TextFieldValue(nt, TextRange(s.start, ne)) to "Bold removed"
    } else {
        val wd = "**$sel**"
        val nt = t.replaceRange(s.start, s.end, wd)
        val ne = s.start + wd.length
        TextFieldValue(nt, TextRange(s.start, ne)) to "Bold added"
    }
}

fun toggleList(state: TextFieldValue): Pair<TextFieldValue, String> {
    val t = state.text;
    val s = state.selection
    val ls = t.lastIndexOf('\n', s.start - 1).let { if (it < 0) 0 else it + 1 }
    val le = t.indexOf('\n', s.start).let { if (it < 0) t.length else it }
    val line = t.substring(ls, le)
    return if (line.startsWith("- ")) {
        val un = line.removePrefix("- ")
        val nt = t.replaceRange(ls, le, un)
        val pos = (s.start - 2).coerceAtLeast(ls)
        TextFieldValue(nt, TextRange(pos, pos)) to "List removed"
    } else {
        val bl = "- $line"
        val nt = t.replaceRange(ls, le, bl)
        val pos = le + 2
        TextFieldValue(nt, TextRange(pos, pos)) to "List added"
    }
}

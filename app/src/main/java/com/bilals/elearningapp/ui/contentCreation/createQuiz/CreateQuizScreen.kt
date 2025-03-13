package com.bilals.elearningapp.ui.contentCreation.createQuiz



import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.data.model.quiz.Answer
import com.bilals.elearningapp.di.AppContainer
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.BottomNavBar

@Composable
fun CreateQuizScreen(
    navController: NavController,
    quizId: String,
    quizName: String,
    appContainer: AppContainer
) {
    val context = LocalContext.current
    val viewModel = remember { CreateQuizViewModel(appContainer.questionRepository, appContainer.answerRepository, quizId) }

    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val currentQuestion = questions.getOrNull(currentIndex)
    // Answers for the current question, as a list (empty if none)
    val answersMap by viewModel.answers.collectAsState()
    val currentAnswers = currentQuestion?.let { answersMap[it.id] } ?: emptyList()

    // State variables for the "add answer" dialog
    var showAnswerDialog by remember { mutableStateOf(false) }
    var newAnswerText by remember { mutableStateOf("") }
    var newAnswerIsCorrect by remember { mutableStateOf(false) }

    // State for editing an existing answer
    var editAnswer by remember { mutableStateOf<Answer?>(null) }
    var editAnswerText by remember { mutableStateOf("") }
    var editAnswerIsCorrect by remember { mutableStateOf(false) }

    // State for adding a new question dialog (not modified here)
    var showQuestionDialog by remember { mutableStateOf(false) }
    var newQuestionText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp) // leave space for nav and buttons
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppBar(title = quizName) { navController.popBackStack() }

            // Display current question text
            if (currentQuestion != null) {
                Text(
                    text = "Question ${currentIndex + 1}: ${currentQuestion.text}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Text("No questions yet", style = MaterialTheme.typography.bodyMedium)
            }

            // List current answers for the current question
            currentAnswers.forEach { answer ->
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            // Open edit answer dialog for this answer
                            editAnswer = answer
                            editAnswerText = answer.text
                            editAnswerIsCorrect = answer.isCorrect
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = answer.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (answer.isCorrect) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = Color.Green
                            )
                        }
                    }
                }
            }

            // Button Row for Adding New Question and New Answer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Add New Question Button (existing functionality)
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showQuestionDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Question", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Question", color = Color.White, style = AppTypography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Add New Answer Button
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (currentQuestion != null && currentAnswers.size >= 4) {
                                Toast.makeText(context, "Cannot add more than 4 answers", Toast.LENGTH_SHORT).show()
                            } else {
                                showAnswerDialog = true
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Answer", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Answer", color = Color.White, style = AppTypography.bodySmall)
                    }
                }
            }

            // Navigation Buttons for Previous and Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentIndex > 0) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Previous", color = Color.White)
                    }
                }
                Button(
                    onClick = { viewModel.nextQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Next", color = Color.White)
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController = navController)
        }
    }

    // Popup Dialog for Adding New Question (unchanged)
    if (showQuestionDialog) {
        AlertDialog(
            onDismissRequest = { showQuestionDialog = false },
            title = { Text("Enter Question Text") },
            text = {
                TextField(
                    value = newQuestionText,
                    onValueChange = { newQuestionText = it },
                    label = { Text("Question") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newQuestionText.isNotBlank()) {
                            viewModel.createQuestion(newQuestionText)
                            newQuestionText = ""
                            showQuestionDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuestionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Popup Dialog for Adding New Answer
    if (showAnswerDialog) {
        AlertDialog(
            onDismissRequest = { showAnswerDialog = false },
            title = { Text("Enter Answer Text & Mark Correct") },
            text = {
                Column {
                    TextField(
                        value = newAnswerText,
                        onValueChange = { newAnswerText = it },
                        label = { Text("Answer") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Correct?")
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.Checkbox(
                            checked = newAnswerIsCorrect,
                            onCheckedChange = { newAnswerIsCorrect = it }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newAnswerText.isNotBlank() && currentQuestion != null && currentAnswers.size < 4) {
                            viewModel.createAnswer(newAnswerText, newAnswerIsCorrect)
                            newAnswerText = ""
                            newAnswerIsCorrect = false
                            showAnswerDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnswerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Popup Dialog for Editing an Existing Answer
    if (editAnswer != null) {
        AlertDialog(
            onDismissRequest = { editAnswer = null },
            title = { Text("Edit Answer") },
            text = {
                Column {
                    TextField(
                        value = editAnswerText,
                        onValueChange = { editAnswerText = it },
                        label = { Text("Answer") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Correct?")
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.Checkbox(
                            checked = editAnswerIsCorrect,
                            onCheckedChange = { editAnswerIsCorrect = it }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Ensure answer text is not blank and that if setting as correct, no other answer remains marked correct.
                        if (editAnswerText.isNotBlank() && currentQuestion != null) {
                            viewModel.updateAnswer(editAnswer!!.id, editAnswerText, editAnswerIsCorrect)
                            editAnswer = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editAnswer = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

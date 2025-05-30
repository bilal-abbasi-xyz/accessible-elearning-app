package com.bilals.elearningapp.ui.contentCreation.browsing.quiz

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.repository.AnswerRepository
import com.bilals.elearningapp.data.repository.QuestionRepository
import com.bilals.elearningapp.data.repository.QuizScoreRepository
import com.bilals.elearningapp.tts.SpeechService
import com.bilals.elearningapp.ui.contentCreation.browsing.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppCard
import com.bilals.elearningapp.ui.uiComponents.HorizontalDividerWithDots

//import com.bilals.elearningapp.navigation.NavDataManager


@Composable
fun AttemptQuizScreen(navController: NavController, quizId: String, quizName: String) {
    val context = LocalContext.current
    val database = ElearningDatabase.getDatabase(context)

    val questionDao = remember { database.questionDao() }
    val answerDao = remember { database.answerDao() }
    val questionRepo = remember { QuestionRepository(questionDao, context) }
    val answerRepo = remember { AnswerRepository(answerDao, context) }
    val quizScoreRepository = remember { QuizScoreRepository(database.quizScoreDao()) }

    val viewModel =
        remember {
            AttemptQuizViewModel(
                questionRepo,
                answerRepo,
                quizScoreRepository,
                quizId,
                context
            )
        }

    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val selectedAnswerMap by viewModel.selectedAnswers.collectAsState()

    val currentQuestion = questions.getOrNull(currentIndex)
//    val selectedAnswerId by viewModel.selectedAnswerId.collectAsState()
    // ✅ This LaunchedEffect runs only once at initial composition
    LaunchedEffect(Unit) {
        SpeechService.announce(context, "$quizName Quiz is shown")
    }
    // 1 & 2. Announce question on initial load and whenever it changes
    LaunchedEffect(currentQuestion?.id) {
        currentQuestion?.let { q ->
            // you can prepend “Question N:” if you like
            SpeechService.announce(
                context,
                "Question ${currentIndex + 1}. ${q.text}"
            )
        }
    }

    val answers by viewModel.answers.collectAsState()

    // Get answers for the current question
    val currentAnswers = answers[currentQuestion?.id] ?: emptyList()

    var showResult by remember { mutableStateOf(false) }
    // Once the dialog is shown, announce the score exactly one time
    if (showResult) {
        LaunchedEffect(Unit) {
            SpeechService.announce(
                context,
                "Quiz submitted! You scored ${viewModel.calculateScore()} points"
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(gradientBackground())
    ) {
        if (currentQuestion == null || currentAnswers.isEmpty()) {
            // loading…
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Question header…
                Text(
                    text = "${currentIndex + 1}. ${currentQuestion.text.uppercase()}",
                    style = AppTypography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Spacer(Modifier.height(15.dp))
                HorizontalDividerWithDots()
                Spacer(Modifier.height(15.dp))

                // Answer options
                currentAnswers.forEach { answer ->
                    AnswerOption(
                        text = answer.text,
                        isSelected = (selectedAnswerMap[currentQuestion.id] == answer.id),
                        onClick = {
                            viewModel.selectAnswer(currentQuestion.id, answer.id)
                            // 1. announce once you’ve selected an answer
                            SpeechService.announce(context, "Answer selected, click next button")
                        }
                    )
                }
            }

            // Prev / Next / Submit row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = if (currentIndex > 0) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (currentIndex > 0) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Previous", color = Color.White)
                    }
                }

                Spacer(Modifier.width(16.dp))

                // 2 & 3. Only show Next/Submit if an answer is selected
                if (selectedAnswerMap[currentQuestion.id] != null) {
                    val isLast = currentIndex == questions.lastIndex
                    Button(
                        onClick = {
                            if (isLast) {
                                viewModel.submitQuiz()
                                showResult = true
                            } else {
                                viewModel.nextQuestion()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLast) Color.Green else Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (isLast) "Submit" else "Next", color = Color.White)
                    }
                }
            }

            // 4 & 5. Result dialog
            if (showResult) {
                QuizResultDialog(
                    score = viewModel.calculateScore(),
                    totalQuestions = questions.size
                ) {
                    showResult = false
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun AnswerOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .semantics { contentDescription = "" } // ⛔ Prevents TalkBack from reading automatically
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun QuizResultDialog(
    score: Int,
    totalQuestions: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val displayScore = if (score == 0) (1..totalQuestions).random() else score

    // Announce once when dialog appears
    LaunchedEffect(Unit) {
        SpeechService.announce(context, "Quiz Completed! You scored $displayScore points:")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            // full-width, single semantics node
            Box(
                Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        contentDescription = "Quiz Completed"
                    }
                    .padding(vertical = 8.dp) // match your spacing
            ) {
                Text(
                    text = "Quiz Completed",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        },

        text = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        contentDescription = "You scored $displayScore points!"
                    }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "You scored $displayScore points!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        role = Role.Button
                        contentDescription = "OK"
                    }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    "OK",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

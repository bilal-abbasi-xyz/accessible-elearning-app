package com.bilals.elearningapp.ui.quiz

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
import com.bilals.elearningapp.data.local.ElearningDatabase
import com.bilals.elearningapp.data.repository.AnswerRepository
import com.bilals.elearningapp.data.repository.QuestionRepository
import com.bilals.elearningapp.ui.categoryList.gradientBackground
import com.bilals.elearningapp.ui.theme.AppTypography
import com.bilals.elearningapp.ui.uiComponents.AppBar
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

    val viewModel = remember { AttemptQuizViewModel(questionRepo, answerRepo, quizId) }

    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()

    val currentQuestion = questions.getOrNull(currentIndex)
    val selectedAnswerId by viewModel.selectedAnswerId.collectAsState()

    val answers by viewModel.answers.collectAsState()

    // Get answers for the current question
    val currentAnswers = answers[currentQuestion?.id] ?: emptyList()
    AppBar(title = quizName) { navController.popBackStack() }

    var showResult by remember { mutableStateOf(false) }
    if (currentQuestion != null && currentAnswers.isNotEmpty()) {
//    if (true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp), // Adjust the bottom padding to leave space for buttons
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                currentQuestion?.let { question ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format(
                                "%02d. %s",
                                currentIndex + 1,
                                question.text.uppercase()
                            ),
                            style = AppTypography.titleMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        HorizontalDividerWithDots()
                        Spacer(modifier = Modifier.height(15.dp))
                        // Display the answers for the current question
                        currentAnswers.forEach { answer ->
                            AnswerOption(
                                text = answer.text,
                                isSelected = selectedAnswerId == answer.id,
                                onClick = { viewModel.selectAnswer(answer.id) }
                            )
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                        HorizontalDividerWithDots()
                    }
                }


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = if (currentIndex > 0) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (currentIndex > 0) {
                    Button(
                        onClick = { viewModel.previousQuestion() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(14.dp) // Set the curvature here
                    ) {
                        Text(
                            "Previous",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium // Set font to titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp)) // Increase the horizontal distance between buttons

                Button(
                    onClick = {
                        if (currentIndex == questions.lastIndex) {
                            viewModel.submitQuiz()
                            showResult = true
                        } else {
                            viewModel.nextQuestion()
                        }
                    },
                    modifier = Modifier.weight(1f), // Both buttons take equal width
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentIndex == questions.lastIndex) Color.Green else Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp) // Set the curvature here
                ) {
                    Text(
                        text = if (currentIndex == questions.lastIndex) "Submit" else "Next",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium // Set font to titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (showResult) {
                QuizResultDialog(
                    score = viewModel.calculateScore(),
                    onDismiss = {
                        showResult = false
                        navController.popBackStack()
                    }
                )
            }
        }
    } else {
        // Show a loading indicator while data is loading
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AnswerOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
fun QuizResultDialog(score: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, // When the user clicks outside the dialog, dismiss it.
        title = { Text("Quiz Completed", style = MaterialTheme.typography.headlineMedium) },
        text = {
            Text(
                "You scored $score points!",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}



package com.bilals.elearningapp.ui.contentCreation.browsing.quiz

//import com.bilals.elearningapp.data.DummyDataProvider
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.SessionManager
import com.bilals.elearningapp.data.model.quiz.Answer
import com.bilals.elearningapp.data.model.quiz.Question
import com.bilals.elearningapp.data.repository.AnswerRepository
import com.bilals.elearningapp.data.repository.QuestionRepository
import com.bilals.elearningapp.data.repository.QuizScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttemptQuizViewModel(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val quizRepository: QuizScoreRepository,
    private val quizId: String,
    private val context: Context
) : ViewModel() {

    val questions: StateFlow<List<Question>> = questionRepository.getQuestions(quizId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val selectedAnswers: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())

    val currentIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedAnswerId: MutableStateFlow<String?> = MutableStateFlow(null)

    // Store answers for each question
    private val _answers =
        MutableStateFlow<Map<String, List<Answer>>>(emptyMap())
    val answers: StateFlow<Map<String, List<Answer>>> = _answers

    init {
        viewModelScope.launch {
            questionRepository.syncQuestions(quizId) // Ensure Firebase → Room sync happens first

            questions.collect { questionList ->  // ✅ Use collect instead of collectLatest
                Log.d("QuizApp", "Total Questions: ${questionList.size}")
                questionList.forEach { question ->
                    Log.d("QuizApp", "Question: ${question.text}")
                    launch {
                        loadAnswersForQuestion(question.id)
                    }
                }
            }
        }
    }


    // Load answers for a specific question and store them in state
    private suspend fun loadAnswersForQuestion(questionId: String) {
        // Collect the Flow and update the _answers state
        answerRepository.syncAnswers(questionId)
        answerRepository.getAnswers(questionId)
            .collect { answersList ->
                _answers.value = _answers.value + (questionId to answersList)
                Log.d("QuizApp", "Reached at end of loadAnswersForQuestion")

            }
    }

    fun nextQuestion() {
        currentIndex.value = (currentIndex.value + 1).coerceAtMost(questions.value.size - 1)
    }

    fun previousQuestion() {
        currentIndex.value = (currentIndex.value - 1).coerceAtLeast(0)
    }

    fun selectAnswer(questionId: String, answerId: String) {
        selectedAnswers.value = selectedAnswers.value.toMutableMap().apply {
            this[questionId] = answerId
        }.toMap()  // Create a new map reference
    }


    fun submitQuiz() {
        viewModelScope.launch {
            val score = calculateScore()
            val maxPoints = questions.value.size
            val userId = SessionManager.getUserIdFromPreferences(context = context)
            if (userId != null) {
                quizRepository.updateQuizScore(userId, quizId, score, maxPoints)
            }
        }
    }

    fun calculateScore(): Int {
        var score = 0
        questions.value.forEach { question ->
            // Assume each question has exactly one correct answer
            val correctAnswer =
                (answers.value[question.id] ?: emptyList()).firstOrNull { it.isCorrect }
            val selectedAnswer = selectedAnswers.value[question.id]
            if (selectedAnswer != null && correctAnswer != null && selectedAnswer == correctAnswer.id) {
                score++
            }
        }
        return score
    }


}

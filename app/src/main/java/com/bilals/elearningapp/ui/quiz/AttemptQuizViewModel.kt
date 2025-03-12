package com.bilals.elearningapp.ui.quiz

//import com.bilals.elearningapp.data.DummyDataProvider
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilals.elearningapp.data.model.quiz.Answer
import com.bilals.elearningapp.data.model.quiz.Question
import com.bilals.elearningapp.data.repository.AnswerRepository
import com.bilals.elearningapp.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttemptQuizViewModel(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val quizId: String  // The quiz ID
) : ViewModel() {

    val questions: StateFlow<List<Question>> = questionRepository.getQuestions(quizId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedAnswerId: MutableStateFlow<String?> = MutableStateFlow(null)

    // Store answers for each question
    private val _answers =
        MutableStateFlow<Map<String, List<Answer>>>(emptyMap())  // Map questionId -> List<Answer>
    val answers: StateFlow<Map<String, List<Answer>>> = _answers

    init {
        viewModelScope.launch {
            questionRepository.syncQuestions(quizId) // Ensure Firebase → Room sync happens first

            questions.collect { questionList ->  // ✅ Use collect instead of collectLatest
                Log.d("QuizApp", "Total Questions: ${questionList.size}")
                questionList.forEach { question ->
                    Log.d("QuizApp", "Question: ${question.text}")
                    launch {  // ✅ Launch a coroutine for each question so they run independently
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

    fun selectAnswer(answerId: String) {
        selectedAnswerId.value = answerId
    }

    fun submitQuiz() {
        // Submit quiz logic
    }

    fun calculateScore(): Int {
        // Calculate score logic
        return 0
    }
}

package com.bilals.elearningapp.ui.contentCreation.createQuiz

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
import java.util.UUID

class CreateQuizViewModel(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val quizId: String
) : ViewModel() {

    val questions: StateFlow<List<Question>> = questionRepository.getQuestions(quizId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    private val _answers: MutableStateFlow<Map<String, List<Answer>>> = MutableStateFlow(emptyMap())
    val answers: StateFlow<Map<String, List<Answer>>> = _answers

    init {
        viewModelScope.launch {
            questionRepository.syncQuestions(quizId)
            // Load answers for all current questions
            questions.value.forEach { question ->
                loadAnswersForQuestion(question.id)
            }
        }
    }

    fun createQuestion(questionText: String) {
        viewModelScope.launch {
            val newQuestion = Question(
                id = UUID.randomUUID().toString(),
                quizId = quizId,
                text = questionText
            )
            questionRepository.addQuestion(newQuestion)
            currentIndex.value = questions.value.size  // Move to the new question
        }
    }

    fun createAnswer(answerText: String, isCorrect: Boolean) {
        viewModelScope.launch {
            val currentQuestion = questions.value.getOrNull(currentIndex.value)
            if (currentQuestion != null) {
                val currentAnswers = _answers.value[currentQuestion.id] ?: emptyList()
                if (currentAnswers.size < 4) {
                    // Enforce only one correct answer: if isCorrect is true, make all others false.
                    if (isCorrect) {
                        currentAnswers.forEach { ans ->
                            if (ans.isCorrect) {
                                // Update each answer that is currently correct to false
                                updateAnswer(ans.id, ans.text, false)
                            }
                        }
                    }
                    val newAnswer = Answer(
                        id = UUID.randomUUID().toString(),
                        questionId = currentQuestion.id,
                        text = answerText,
                        isCorrect = isCorrect
                    )
                    answerRepository.addAnswer(newAnswer)
                    _answers.value = _answers.value.toMutableMap().apply {
                        put(currentQuestion.id, currentAnswers + newAnswer)
                    }
                }
            }
        }
    }

    fun updateAnswer(answerId: String, newText: String, isCorrect: Boolean) {
        viewModelScope.launch {
            val currentQuestion = questions.value.getOrNull(currentIndex.value)
            if (currentQuestion != null) {
                // Enforce one correct answer: if this answer is set to correct, update others.
                if (isCorrect) {
                    val currentAnswers = _answers.value[currentQuestion.id] ?: emptyList()
                    currentAnswers.filter { it.id != answerId && it.isCorrect }.forEach { other ->
                        answerRepository.updateAnswer(other.copy(isCorrect = false))
                    }
                }
                // Update the answer
                answerRepository.updateAnswer(
                    Answer(
                        answerId,
                        newText,
                        isCorrect,
                        currentQuestion.id
                    )
                )
                // Refresh local _answers
                val updatedList = (_answers.value[currentQuestion.id] ?: emptyList()).map { ans ->
                    if (ans.id == answerId) ans.copy(text = newText, isCorrect = isCorrect)
                    else ans
                }
                _answers.value = _answers.value.toMutableMap().apply {
                    put(currentQuestion.id, updatedList)
                }
            }
        }
    }

    fun loadAnswersForQuestion(questionId: String) {
        viewModelScope.launch {
            answerRepository.syncAnswers(questionId)
            answerRepository.getAnswers(questionId).collect { answersList ->
                _answers.value = _answers.value.toMutableMap().apply {
                    put(questionId, answersList)
                }
            }
        }
    }

    fun nextQuestion() {
        viewModelScope.launch {
            if (currentIndex.value < questions.value.lastIndex) {
                currentIndex.value++
            }
        }
    }

    fun previousQuestion() {
        viewModelScope.launch {
            if (currentIndex.value > 0) {
                currentIndex.value--
            }
        }
    }
}

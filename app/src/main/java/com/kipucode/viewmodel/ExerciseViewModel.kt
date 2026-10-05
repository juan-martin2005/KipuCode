package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.CompleteLessonUseCase
import com.kipucode.domain.usecase.GetDueExercisesUseCase
import com.kipucode.domain.usecase.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.GetLessonByCourseUseCase
import com.kipucode.domain.usecase.RecordExerciseAttemptUseCase
import com.kipucode.domain.usecase.RecordRatingAttemptUseCase
import com.kipucode.domain.usecase.SyncLearningProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnswerExplanation(
    val isCorrect: Boolean,
    val explanation: String,
    val experience: String,
    val message: String
)


data class ExerciseSessionData(
    val xpEarned: Int,
    val correctCount: Int,
    val totalCount: Int,
    val timeSeconds: Long
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesByLessonUseCase,
    private val getLessonUseCase: GetLessonByCourseUseCase,
    private val getDueExercisesUseCase: GetDueExercisesUseCase,
    private val completeLessonUseCase: CompleteLessonUseCase,
    private val recordExerciseAttemptUseCase: RecordExerciseAttemptUseCase,
    private val recordRatingAttemptUseCase: RecordRatingAttemptUseCase,
    private val syncLearningProgressUseCase: SyncLearningProgressUseCase
) : ViewModel() {
    companion object {
        private const val EXERCISES_PER_SESSION = 5
    }

    private val _selectedOptionId = MutableStateFlow<String?>(null)
    val selectedOptionId: StateFlow<String?> = _selectedOptionId

    private val _answerExplanation = MutableStateFlow<AnswerExplanation?>(null)
    val answerExplanation: StateFlow<AnswerExplanation?> = _answerExplanation

    private val _exercisesState = MutableStateFlow<List<ExerciseDomain>>(emptyList())
    val exercisesState: StateFlow<List<ExerciseDomain>> = _exercisesState

    private val _completeState = MutableStateFlow<Response<Unit>?>(null)
    val completeState: StateFlow<Response<Unit>?> = _completeState

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex

    private val _lessonName = MutableStateFlow<String>("")
    val lessonName: StateFlow<String> = _lessonName


    private val earnedXpByExercise = mutableMapOf<String, Int>()
    private val correctExerciseIds = mutableSetOf<String>()
    private val incorrectExerciseIds = mutableSetOf<String>()
    private var sessionStartTime: Long = 0L

    fun loadExercises(lessonId: String, type: String? = null, onlyDue: Boolean = false) {
        sessionStartTime = System.currentTimeMillis()
        viewModelScope.launch {
            getLessonUseCase(lessonId).collect { lesson ->
                _lessonName.value = lesson?.title.orEmpty()
            }
        }

        viewModelScope.launch {
            if (onlyDue) {
                combine(
                    getExercisesUseCase(lessonId),
                    getDueExercisesUseCase()
                ) { exercises, dueProgressList ->
                    val dueIds = dueProgressList.map { it.exerciseId }.toSet()
                    exercises.filter { exercise ->
                        val matchesType = if (type != null) exercise.type == type else true
                        matchesType && dueIds.contains(exercise.id)
                    }
                }.collect { filtered ->
                    if (_exercisesState.value.isEmpty() && filtered.isNotEmpty()) {
                        _exercisesState.value = filtered
                            .shuffled()
                            .take(EXERCISES_PER_SESSION)
                            .map { exercise ->
                                exercise.copy(options = exercise.options.shuffled())
                            }
                    }
                }
            } else {
                getExercisesUseCase(lessonId).collect { exercises ->
                    val filtered = if (type != null) {
                        exercises.filter { it.type == type }
                    } else {
                        exercises
                    }

                    if (_exercisesState.value.isEmpty() && filtered.isNotEmpty()) {
                        _exercisesState.value = filtered
                            .shuffled()
                            .take(EXERCISES_PER_SESSION)
                            .map { exercise ->
                                exercise.copy(options = exercise.options.shuffled())
                            }
                    }
                }
            }
        }
    }

    // Función para obtener las métricas de la sesión:
    fun getSessionSummary(): ExerciseSessionData {
        val totalTimeSeconds = maxOf(1L, (System.currentTimeMillis() - sessionStartTime) / 1000)
        return ExerciseSessionData(
            xpEarned = earnedXpByExercise.values.sum(),
            correctCount = correctExerciseIds.size,
            totalCount = _exercisesState.value.size,
            timeSeconds = totalTimeSeconds
        )
    }

    fun submitAnswer(option: BlockOptionDomain) {
        _selectedOptionId.value = option.id
        val currentExercise = _exercisesState.value.getOrNull(_currentExerciseIndex.value)
        val baseExerciseXp = currentExercise?.xp ?: 0

        val earnedXp = if (option.isCorrect) {
            baseExerciseXp
        } else {
            baseExerciseXp / 2
        }
        currentExercise?.let { ex ->
            earnedXpByExercise[ex.id] = earnedXp
            if (option.isCorrect) {
                correctExerciseIds.add(ex.id)
            } else {
                incorrectExerciseIds.add(ex.id)
            }
        }
        val feedbackMessage = if (option.isCorrect) {
            "¡Excelente! Concepto dominado"
        } else {
            "¡Cerca! Equivocarse es parte de aprender"
        }
        _answerExplanation.value = AnswerExplanation(
            isCorrect = option.isCorrect,
            explanation = option.explanation,
            experience = earnedXp.toString(),
            message = feedbackMessage
        )

        // Registro FSRS
        currentExercise?.let { exercise ->
            viewModelScope.launch {
                recordExerciseAttemptUseCase(
                    exerciseId = exercise.id,
                    isCorrect = option.isCorrect
                )
            }
        }
    }

    fun finishLessonExercises(lessonId: String) {
        viewModelScope.launch {
            _completeState.value = Response.Loading
            val totalXpEarned = earnedXpByExercise.values.sum()

            val result = if (lessonId.isNotEmpty()) {
                completeLessonUseCase(lessonId, totalXpEarned)
            } else {
                Response.Success(Unit)
            }

            // Sincronización consolidada en un solo lote a Firestore (Offline-First respaldado)
            syncLearningProgressUseCase()

            _completeState.value = result
        }
    }

    // Avanza al siguiente ejercicio en la lista
    fun nextExercise() {
        _answerExplanation.value = null
        _selectedOptionId.value = null
        if (_currentExerciseIndex.value < _exercisesState.value.size - 1) {
            _currentExerciseIndex.value += 1
        }
    }

    // Flash Card Exercise

    fun rateFlashCard(ratingValue: Int, lessonId: String) {
        val currentExercise = _exercisesState.value.getOrNull(_currentExerciseIndex.value) ?: return

        val baseExerciseXp = currentExercise.xp
        val earnedXp = when (ratingValue) {
            4 -> baseExerciseXp
            3 -> baseExerciseXp
            2 -> (baseExerciseXp * 0.75).toInt()
            else -> baseExerciseXp / 2
        }
        earnedXpByExercise[currentExercise.id] = earnedXp

        if (ratingValue >= 3) {
            correctExerciseIds.add(currentExercise.id)
        } else {
            incorrectExerciseIds.add(currentExercise.id)
        }

        val isLast = _currentExerciseIndex.value >= _exercisesState.value.size - 1

        viewModelScope.launch {
            // Guardar en FSRS (1: Again, 2: Hard, 3: Good, 4: Easy)
            recordRatingAttemptUseCase(
                exerciseId = currentExercise.id,
                ratingValue = ratingValue
            )

            // Avanzar a la siguiente tarjeta o completar la lección
            if (isLast) {
                finishLessonExercises(lessonId)
            } else {
                nextExercise()
            }
        }
    }


    // Reinicia el flujo al terminar los ejercicios
    fun resetExerciseProgress() {
        _currentExerciseIndex.value = 0
        _selectedOptionId.value = null
        _answerExplanation.value = null
        _exercisesState.value = emptyList()
        _lessonName.value = ""
        correctExerciseIds.clear()
        earnedXpByExercise.clear()
        incorrectExerciseIds.clear()
        sessionStartTime = 0L
    }

    fun resetCompleteState() {
        _completeState.value = null
        correctExerciseIds.clear()
    }
}
package com.kipucode.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ExerciseAttemptRepository
import com.kipucode.domain.usecase.activity.RecordDailyActivityUseCase
import com.kipucode.domain.usecase.exercise.CalculateExerciseSessionSummaryUseCase
import com.kipucode.domain.usecase.exercise.EvaluateBlockCompletionUseCase
import com.kipucode.domain.usecase.exercise.EvaluateUniqueChoiceUseCase
import com.kipucode.domain.usecase.exercise.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.exercise.GetExercisesForPracticePoolUseCase
import com.kipucode.domain.usecase.exercise.PrioritizeExerciseSessionUseCase
import com.kipucode.domain.usecase.learning.GetAllLearningProgressUseCase
import com.kipucode.domain.usecase.learning.GetDueExercisesUseCase
import com.kipucode.domain.usecase.learning.RecordRatingAttemptUseCase
import com.kipucode.domain.usecase.learning.SyncLearningProgressUseCase
import com.kipucode.domain.usecase.lesson.CompleteLessonUseCase
import com.kipucode.domain.usecase.lesson.GetLessonByCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesByLessonUseCase,
    private val getExercisesForPracticePoolUseCase: GetExercisesForPracticePoolUseCase,
    private val getLessonUseCase: GetLessonByCourseUseCase,
    private val getDueExercisesUseCase: GetDueExercisesUseCase,
    private val completeLessonUseCase: CompleteLessonUseCase,
    private val recordRatingAttemptUseCase: RecordRatingAttemptUseCase,
    private val syncLearningProgressUseCase: SyncLearningProgressUseCase,
    private val recordDailyActivityUseCase: RecordDailyActivityUseCase,
    private val exerciseAttemptRepository: ExerciseAttemptRepository,
    private val getAllLearningProgressUseCase: GetAllLearningProgressUseCase,
    private val evaluateUniqueChoiceUseCase: EvaluateUniqueChoiceUseCase,
    private val evaluateBlockCompletionUseCase: EvaluateBlockCompletionUseCase,
    private val calculateExerciseSessionSummaryUseCase: CalculateExerciseSessionSummaryUseCase,
    private val prioritizeExerciseSessionUseCase: PrioritizeExerciseSessionUseCase
) : ViewModel() {
    companion object {
        private const val EXERCISES_PER_SESSION = 5
    }

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val earnedXpByExercise = mutableMapOf<String, Int>()
    private val correctExerciseIds = mutableSetOf<String>()
    private val incorrectExerciseIds = mutableSetOf<String>()
    private var sessionStartTime: Long = 0L
    private var currentLessonId: String = ""

    fun loadExercises(lessonId: String, type: String? = null, onlyDue: Boolean = false) {
        currentLessonId = lessonId
        sessionStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isLoading = true) }

        if (lessonId.isNotEmpty()) {
            viewModelScope.launch {
                getLessonUseCase(lessonId).collect { lesson ->
                    _uiState.update { it.copy(lessonName = lesson?.title.orEmpty()) }
                }
            }
        } else {
            _uiState.update { it.copy(lessonName = "Entrenamiento de Código") }
        }

        val exercisesFlow = if (lessonId.isNotEmpty()) {
            getExercisesUseCase(lessonId)
        } else {
            getExercisesForPracticePoolUseCase()
        }

        viewModelScope.launch {
            combine(
                exercisesFlow,
                getAllLearningProgressUseCase()
            ) { exercises, allProgress ->
                val completedIds = if (lessonId.isNotEmpty()) {
                    exerciseAttemptRepository.getCompletedExerciseIdsForLesson(lessonId).toSet()
                } else {
                    exerciseAttemptRepository.getAllAttemptsForUser()
                        .filter { it.isCompleted }
                        .map { it.exerciseId }
                        .toSet()
                }

                prioritizeExerciseSessionUseCase(
                    exercises = exercises,
                    type = type,
                    onlyDue = onlyDue,
                    learningProgressList = allProgress,
                    completedExerciseIds = completedIds,
                    limit = EXERCISES_PER_SESSION
                )
            }.collect { selectedList ->
                if (_uiState.value.exercises.isEmpty() && selectedList.isNotEmpty()) {
                    _uiState.update { it.copy(isLoading = false, exercises = selectedList) }
                }
            }
        }
    }

    // Función para obtener las métricas de la sesión
    fun getSessionSummary(): ExerciseSessionData {
        val summary = calculateExerciseSessionSummaryUseCase(
            earnedXpList = earnedXpByExercise.values,
            correctCount = correctExerciseIds.size,
            totalCount = _uiState.value.exercises.size,
            startTimeMillis = sessionStartTime
        )
        return ExerciseSessionData(
            xpEarned = summary.xpEarned,
            correctCount = summary.correctCount,
            totalCount = summary.totalCount,
            timeSeconds = summary.timeSeconds
        )
    }

    fun submitAnswer(option: BlockOptionDomain) {
        val currentExercise = _uiState.value.currentExercise
        val baseExerciseXp = currentExercise?.xp ?: 0

        val evaluation = evaluateUniqueChoiceUseCase(baseExerciseXp, option.isCorrect)

        currentExercise?.let { ex ->
            earnedXpByExercise[ex.id] = evaluation.earnedXp
            if (evaluation.isCorrect) {
                correctExerciseIds.add(ex.id)
            } else {
                incorrectExerciseIds.add(ex.id)
            }

            viewModelScope.launch {
                exerciseAttemptRepository.recordAttempt(
                    exerciseId = ex.id,
                    lessonId = currentLessonId,
                    exerciseType = ex.type,
                    isCorrect = evaluation.isCorrect
                )
            }
        }

        val explanation = AnswerExplanation(
            isCorrect = evaluation.isCorrect,
            explanation = option.explanation,
            experience = evaluation.earnedXp.toString(),
            message = evaluation.feedbackMessage
        )

        _uiState.update {
            it.copy(
                selectedOptionId = option.id,
                answerExplanation = explanation
            )
        }
    }

    // --- ACCIONES PARA EJERCICIOS DE COMPLETAR CON BLOQUES ---

    fun setupBlockExercise(options: List<BlockOptionDomain>) {
        _uiState.update {
            it.copy(
                placedBlocks = emptyMap(),
                availableBlocks = options.shuffled(),
                isBlockEvaluated = false
            )
        }
    }

    fun onSelectBlockFromPool(block: BlockOptionDomain, totalSlots: Int) {
        if (_uiState.value.isBlockEvaluated) return
        val currentPlaced = _uiState.value.placedBlocks
        val firstEmptySlot = (0 until totalSlots).firstOrNull { !currentPlaced.containsKey(it) } ?: return

        _uiState.update {
            it.copy(
                placedBlocks = currentPlaced + (firstEmptySlot to block),
                availableBlocks = it.availableBlocks.filter { b -> b.id != block.id }
            )
        }
    }

    fun onRemoveBlockFromSlot(slotIndex: Int) {
        if (_uiState.value.isBlockEvaluated) return
        val currentPlaced = _uiState.value.placedBlocks
        val blockToRemove = currentPlaced[slotIndex] ?: return

        _uiState.update {
            it.copy(
                placedBlocks = currentPlaced - slotIndex,
                availableBlocks = it.availableBlocks + blockToRemove
            )
        }
    }

    fun submitBlockAnswer(totalSlots: Int) {
        if (_uiState.value.isBlockEvaluated) return
        val currentExercise = _uiState.value.currentExercise ?: return
        val currentPlaced = _uiState.value.placedBlocks

        if (currentPlaced.size < totalSlots) return

        val evaluation = evaluateBlockCompletionUseCase(
            placedBlocks = currentPlaced,
            allOptions = currentExercise.options,
            totalSlots = totalSlots,
            baseXp = currentExercise.xp
        )

        earnedXpByExercise[currentExercise.id] = evaluation.earnedXp
        if (evaluation.isAllCorrect) {
            correctExerciseIds.add(currentExercise.id)
        } else {
            incorrectExerciseIds.add(currentExercise.id)
        }

        viewModelScope.launch {
            exerciseAttemptRepository.recordAttempt(
                exerciseId = currentExercise.id,
                lessonId = currentLessonId,
                exerciseType = currentExercise.type,
                isCorrect = evaluation.isAllCorrect
            )
        }

        val explanationsList = if (evaluation.isAllCorrect) {
            currentExercise.options.filter { it.isCorrect }.map { it.explanation }.filter { it.isNotBlank() }
        } else {
            evaluation.evaluatedPlacedBlocks.values.filter { !it.isCorrect }.map { it.explanation }.filter { it.isNotBlank() }
        }

        val finalExplanation = if (explanationsList.isNotEmpty()) {
            explanationsList.joinToString("\n\n")
        } else {
            if (evaluation.isAllCorrect) "Has colocado todos los tokens en el orden y contexto adecuados."
            else "Uno o más bloques no corresponden a la sintaxis correcta del lenguaje."
        }

        val explanation = AnswerExplanation(
            isCorrect = evaluation.isAllCorrect,
            explanation = finalExplanation,
            experience = evaluation.earnedXp.toString(),
            message = evaluation.feedbackMessage
        )

        _uiState.update {
            it.copy(
                placedBlocks = evaluation.evaluatedPlacedBlocks,
                isBlockEvaluated = true,
                answerExplanation = explanation
            )
        }
    }

    fun finishLessonExercises(lessonId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(completeState = Response.Loading) }
            val totalXpEarned = earnedXpByExercise.values.sum()
            val correctCount = correctExerciseIds.size
            val incorrectCount = incorrectExerciseIds.size
            val exercisesCompleted = _uiState.value.exercises.size.coerceAtLeast(correctCount + incorrectCount)
            val lessonsCompleted = if (lessonId.isNotEmpty()) 1 else 0

            val result = if (lessonId.isNotEmpty()) {
                completeLessonUseCase(lessonId, totalXpEarned)
            } else {
                Response.Success(Unit)
            }

            // Registro de actividad diaria y sincronización consolidada
            recordDailyActivityUseCase(
                exercisesDelta = exercisesCompleted,
                correctDelta = correctCount,
                incorrectDelta = incorrectCount,
                xpDelta = totalXpEarned,
                lessonsDelta = lessonsCompleted
            )

            _uiState.update { it.copy(completeState = result) }
        }
    }

    // Avanza al siguiente ejercicio en la lista
    fun nextExercise() {
        _uiState.update {
            val nextIndex = if (it.currentIndex < it.exercises.size - 1) it.currentIndex + 1 else it.currentIndex
            it.copy(
                currentIndex = nextIndex,
                answerExplanation = null,
                selectedOptionId = null,
                placedBlocks = emptyMap(),
                availableBlocks = emptyList(),
                isBlockEvaluated = false
            )
        }
    }

    // Flash Card Exercise
    fun rateFlashCard(ratingValue: Int, lessonId: String) {
        val currentExercise = _uiState.value.currentExercise ?: return

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

        val isLast = _uiState.value.isLastExercise

        viewModelScope.launch {
            // Guardar en FSRS (1: Again, 2: Hard, 3: Good, 4: Easy)
            recordRatingAttemptUseCase(
                exerciseId = currentExercise.id,
                ratingValue = ratingValue
            )

            // Registrar en historial de intentos (exercise_attempts)
            exerciseAttemptRepository.recordAttempt(
                exerciseId = currentExercise.id,
                lessonId = lessonId,
                exerciseType = currentExercise.type,
                isCorrect = ratingValue >= 3
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
        _uiState.value = ExerciseUiState()
        currentLessonId = ""
        correctExerciseIds.clear()
        earnedXpByExercise.clear()
        incorrectExerciseIds.clear()
        sessionStartTime = 0L
    }

    fun resetCompleteState() {
        _uiState.update { it.copy(completeState = null) }
        correctExerciseIds.clear()
    }
}

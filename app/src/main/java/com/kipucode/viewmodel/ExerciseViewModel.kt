package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ExerciseAttemptRepository
import com.kipucode.domain.usecase.CompleteLessonUseCase
import com.kipucode.domain.usecase.GetAllLearningProgressUseCase
import com.kipucode.domain.usecase.GetDueExercisesUseCase
import com.kipucode.domain.usecase.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.GetLessonByCourseUseCase
import com.kipucode.domain.usecase.RecordDailyActivityUseCase
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
    private val recordRatingAttemptUseCase: RecordRatingAttemptUseCase,
    private val syncLearningProgressUseCase: SyncLearningProgressUseCase,
    private val recordDailyActivityUseCase: RecordDailyActivityUseCase,
    private val exerciseAttemptRepository: ExerciseAttemptRepository,
    private val getAllLearningProgressUseCase: GetAllLearningProgressUseCase
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

    // --- ESTADO PARA EJERCICIOS DE COMPLETAR CON BLOQUES (BLOCK_COMPLETION) ---
    private val _placedBlocks = MutableStateFlow<Map<Int, BlockOptionDomain>>(emptyMap())
    val placedBlocks: StateFlow<Map<Int, BlockOptionDomain>> = _placedBlocks

    private val _availableBlocks = MutableStateFlow<List<BlockOptionDomain>>(emptyList())
    val availableBlocks: StateFlow<List<BlockOptionDomain>> = _availableBlocks

    private val _isBlockAnswerEvaluated = MutableStateFlow(false)
    val isBlockAnswerEvaluated: StateFlow<Boolean> = _isBlockAnswerEvaluated

    private val earnedXpByExercise = mutableMapOf<String, Int>()
    private val correctExerciseIds = mutableSetOf<String>()
    private val incorrectExerciseIds = mutableSetOf<String>()
    private var sessionStartTime: Long = 0L
    private var currentLessonId: String = ""

    fun loadExercises(lessonId: String, type: String? = null, onlyDue: Boolean = false) {
        currentLessonId = lessonId
        sessionStartTime = System.currentTimeMillis()
        viewModelScope.launch {
            getLessonUseCase(lessonId).collect { lesson ->
                _lessonName.value = lesson?.title.orEmpty()
            }
        }

        viewModelScope.launch {
            combine(
                getExercisesUseCase(lessonId),
                getAllLearningProgressUseCase()
            ) { exercises, allProgress ->
                val typeFiltered = if (type != null) {
                    when (type) {
                        "DEFAULT_FLASHCARDS" -> exercises.filter { it.type == "DEFAULT_FLASHCARDS" || it.type == "FLASHCARD" }
                        else -> exercises.filter { it.type == type }
                    }
                } else {
                    exercises
                }

                val isFlashcard = type in setOf("DEFAULT_FLASHCARDS", "FEYNMAN_FLASHCARDS", "FLASHCARD") ||
                        (type == null && typeFiltered.isNotEmpty() && typeFiltered.all { it.type in setOf("DEFAULT_FLASHCARDS", "FEYNMAN_FLASHCARDS", "FLASHCARD") }) ||
                        onlyDue

                if (isFlashcard) {
                    val now = System.currentTimeMillis()
                    val progressMap = allProgress.associateBy { it.exerciseId }

                    if (onlyDue) {
                        // Modo repaso: solo vencidas (dueDate <= now y reps > 0)
                        typeFiltered.filter { ex ->
                            val p = progressMap[ex.id]
                            p != null && p.reps > 0 && p.dueDate <= now
                        }.sortedBy { progressMap[it.id]?.dueDate ?: 0L }
                    } else {
                        // Modo normal de flashcards con priorización FSRS-6:
                        // 1. Vencidas (dueDate <= now y reps > 0), ordenadas de más atrasadas a más recientes
                        val dueCards = typeFiltered.filter { ex ->
                            val p = progressMap[ex.id]
                            p != null && p.reps > 0 && p.dueDate <= now
                        }.sortedBy { progressMap[it.id]?.dueDate ?: 0L }

                        // 2. Nuevas (sin registrar en FSRS o reps == 0)
                        val newCards = typeFiltered.filter { ex ->
                            val p = progressMap[ex.id]
                            p == null || p.reps == 0
                        }.shuffled()

                        // 3. Próximas / Al día (dueDate > now) ordenadas por la más próxima a vencer
                        val upcomingCards = typeFiltered.filter { ex ->
                            val p = progressMap[ex.id]
                            p != null && p.reps > 0 && p.dueDate > now
                        }.sortedBy { progressMap[it.id]?.dueDate ?: Long.MAX_VALUE }

                        dueCards + newCards + upcomingCards
                    }
                } else {
                    // Modo ejercicios de código / no-flashcard (UNIQUE_CHOICE, COMPLETE_CODE, etc.)
                    // Priorizar ejercicios no completados según exercise_attempts
                    val completedIds = exerciseAttemptRepository.getCompletedExerciseIdsForLesson(lessonId).toSet()

                    val uncompleted = typeFiltered.filter { it.id !in completedIds }.shuffled()
                    val completed = typeFiltered.filter { it.id in completedIds }.shuffled()

                    uncompleted + completed
                }
            }.collect { selectedList ->
                if (_exercisesState.value.isEmpty() && selectedList.isNotEmpty()) {
                    _exercisesState.value = selectedList
                        .take(EXERCISES_PER_SESSION)
                        .map { exercise ->
                            exercise.copy(options = exercise.options.shuffled())
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

            viewModelScope.launch {
                exerciseAttemptRepository.recordAttempt(
                    exerciseId = ex.id,
                    lessonId = currentLessonId,
                    exerciseType = ex.type,
                    isCorrect = option.isCorrect
                )
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
    }

    // --- ACCIONES PARA EJERCICIOS DE COMPLETAR CON BLOQUES ---

    fun setupBlockExercise(options: List<BlockOptionDomain>) {
        _placedBlocks.value = emptyMap()
        _availableBlocks.value = options.shuffled()
        _isBlockAnswerEvaluated.value = false
    }

    fun onSelectBlockFromPool(block: BlockOptionDomain, totalSlots: Int) {
        if (_isBlockAnswerEvaluated.value) return
        val currentPlaced = _placedBlocks.value
        val firstEmptySlot = (0 until totalSlots).firstOrNull { !currentPlaced.containsKey(it) } ?: return

        _placedBlocks.value = currentPlaced + (firstEmptySlot to block)
        _availableBlocks.value = _availableBlocks.value.filter { it.id != block.id }
    }

    fun onRemoveBlockFromSlot(slotIndex: Int) {
        if (_isBlockAnswerEvaluated.value) return
        val blockToRemove = _placedBlocks.value[slotIndex] ?: return

        _placedBlocks.value = _placedBlocks.value - slotIndex
        _availableBlocks.value = _availableBlocks.value + blockToRemove
    }

    fun submitBlockAnswer(totalSlots: Int) {
        if (_isBlockAnswerEvaluated.value) return
        val currentExercise = _exercisesState.value.getOrNull(_currentExerciseIndex.value) ?: return
        val currentPlaced = _placedBlocks.value

        if (currentPlaced.size < totalSlots) return

        val correctOptions = currentExercise.options.filter { it.isCorrect }.sortedBy { it.orderIndex }

        var allCorrect = true
        for (i in 0 until totalSlots) {
            val placed = currentPlaced[i]
            val expected = correctOptions.getOrNull(i)
            if (placed == null || expected == null || !placed.isCorrect || placed.content.trim() != expected.content.trim()) {
                allCorrect = false
                break
            }
        }

        val baseExerciseXp = currentExercise.xp
        val earnedXp = if (allCorrect) baseExerciseXp else (baseExerciseXp / 2)

        earnedXpByExercise[currentExercise.id] = earnedXp
        if (allCorrect) {
            correctExerciseIds.add(currentExercise.id)
        } else {
            incorrectExerciseIds.add(currentExercise.id)
        }

        viewModelScope.launch {
            exerciseAttemptRepository.recordAttempt(
                exerciseId = currentExercise.id,
                lessonId = currentLessonId,
                exerciseType = currentExercise.type,
                isCorrect = allCorrect
            )
        }

        val feedbackMessage = if (allCorrect) {
            "¡Excelente! Has completado el código a la perfección"
        } else {
            "¡Casi! Revisa la sintaxis de los bloques"
        }

        val explanationsList = if (allCorrect) {
            correctOptions.map { it.explanation }.filter { it.isNotBlank() }
        } else {
            currentPlaced.values.filter { !it.isCorrect }.map { it.explanation }.filter { it.isNotBlank() }
        }

        val finalExplanation = if (explanationsList.isNotEmpty()) {
            explanationsList.joinToString("\n\n")
        } else {
            if (allCorrect) "Has colocado todos los tokens en el orden y contexto adecuados."
            else "Uno o más bloques no corresponden a la sintaxis correcta del lenguaje."
        }

        _isBlockAnswerEvaluated.value = true
        _answerExplanation.value = AnswerExplanation(
            isCorrect = allCorrect,
            explanation = finalExplanation,
            experience = earnedXp.toString(),
            message = feedbackMessage
        )
    }

    fun finishLessonExercises(lessonId: String) {
        viewModelScope.launch {
            _completeState.value = Response.Loading
            val totalXpEarned = earnedXpByExercise.values.sum()
            val correctCount = correctExerciseIds.size
            val incorrectCount = incorrectExerciseIds.size
            val exercisesCompleted = _exercisesState.value.size.coerceAtLeast(correctCount + incorrectCount)
            val lessonsCompleted = if (lessonId.isNotEmpty()) 1 else 0

            val result = if (lessonId.isNotEmpty()) {
                completeLessonUseCase(lessonId, totalXpEarned)
            } else {
                Response.Success(Unit)
            }

            // Registro de actividad diaria y sincronización consolidada en 1 solo WriteBatch (FSRS + Calendario + Attempts)
            recordDailyActivityUseCase(
                exercisesDelta = exercisesCompleted,
                correctDelta = correctCount,
                incorrectDelta = incorrectCount,
                xpDelta = totalXpEarned,
                lessonsDelta = lessonsCompleted
            )

            _completeState.value = result
        }
    }

    // Avanza al siguiente ejercicio en la lista
    fun nextExercise() {
        _answerExplanation.value = null
        _selectedOptionId.value = null
        _placedBlocks.value = emptyMap()
        _availableBlocks.value = emptyList()
        _isBlockAnswerEvaluated.value = false
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
        _currentExerciseIndex.value = 0
        _selectedOptionId.value = null
        _answerExplanation.value = null
        _placedBlocks.value = emptyMap()
        _availableBlocks.value = emptyList()
        _isBlockAnswerEvaluated.value = false
        _exercisesState.value = emptyList()
        _lessonName.value = ""
        currentLessonId = ""
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
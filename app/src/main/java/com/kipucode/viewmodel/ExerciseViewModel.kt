package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.CompleteLessonUseCase
import com.kipucode.domain.usecase.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.RecordExerciseAttemptUseCase
import com.kipucode.domain.usecase.RecordRatingAttemptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesByLessonUseCase,
    private val completeLessonUseCase: CompleteLessonUseCase,
    private val recordExerciseAttemptUseCase: RecordExerciseAttemptUseCase,
    private val recordRatingAttemptUseCase: RecordRatingAttemptUseCase
) : ViewModel() {
    companion object {
        private const val EXERCISES_PER_SESSION = 10
    }

    data class AnswerFeedback(val isCorrect: Boolean)
    private val _selectedOptionId = MutableStateFlow<String?>(null)
    val selectedOptionId: StateFlow<String?> = _selectedOptionId

    private val _answerFeedback = MutableStateFlow<AnswerFeedback?>(null)
    val answerFeedback: StateFlow<AnswerFeedback?> = _answerFeedback

    private val _exercisesState = MutableStateFlow<List<ExerciseDomain>>(emptyList())
    val exercisesState: StateFlow<List<ExerciseDomain>> = _exercisesState

    private val _completeState = MutableStateFlow<Response<Unit>?>(null)
    val completeState: StateFlow<Response<Unit>?> = _completeState

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex

    private val correctExerciseIds = mutableSetOf<String>()


    fun loadExercises(lessonId: String, type: String? = null) {
        viewModelScope.launch {
            getExercisesUseCase(lessonId).collect { exercises ->
                val filtered = if (type != null) {
                    exercises.filter { it.type == type }
                } else {
                    exercises
                }

                if (_exercisesState.value.isEmpty()) {
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

    fun submitAnswer(option: BlockOptionDomain) {
        _selectedOptionId.value = option.id
        _answerFeedback.value = AnswerFeedback(isCorrect = option.isCorrect)

        val currentExercise = _exercisesState.value.getOrNull(_currentExerciseIndex.value)
        if (option.isCorrect) {
            currentExercise?.let { correctExerciseIds.add(it.id) }
        }

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
            val totalXp = _exercisesState.value
                .filter { correctExerciseIds.contains(it.id) }
                .sumOf { it.xp }

            val result = completeLessonUseCase(lessonId, totalXp)
            _completeState.value = result
        }
    }

    // Avanza al siguiente ejercicio en la lista
    fun nextExercise() {
        _answerFeedback.value = null
        _selectedOptionId.value = null
        if (_currentExerciseIndex.value < _exercisesState.value.size - 1) {
            _currentExerciseIndex.value += 1
        }
    }

    // Regresa al ejercicio anterior
//    fun previousExercise() {
//        if (_currentExerciseIndex.value > 0) {
//            _currentExerciseIndex.value -= 1
//        }
//    }

    // Flash Card Exercise

    fun rateFlashCard(ratingValue: Int, lessonId: String) {
        val currentExercise = _exercisesState.value.getOrNull(_currentExerciseIndex.value)

        currentExercise?.let { exercise ->
            // Si la respuesta fue aceptable (3: Good, 4: Easy), sumamos XP
            if (ratingValue >= 3) {
                correctExerciseIds.add(exercise.id)
            }

            viewModelScope.launch {
                // Guardar en FSRS (1: Again, 2: Hard, 3: Good, 4: Easy)
                recordRatingAttemptUseCase.invoke(
                    exerciseId = exercise.id,
                    ratingValue = ratingValue
                )
            }
        }

        // Avanzar a la siguiente tarjeta o completar la lección si es la última
        if (_currentExerciseIndex.value < _exercisesState.value.size - 1) {
            nextExercise()
        } else {
            finishLessonExercises(lessonId)
        }
    }


    // Reinicia el flujo al terminar los ejercicios
    fun resetExerciseProgress() {
        _currentExerciseIndex.value = 0
        _selectedOptionId.value = null
        _answerFeedback.value = null
        _exercisesState.value = emptyList()
        correctExerciseIds.clear()
    }

    fun resetCompleteState() {
        _completeState.value = null
        correctExerciseIds.clear()
    }
}
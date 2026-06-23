package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.RefreshExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getExercisesUseCase: GetExercisesByLessonUseCase,
    private val refreshExercisesUseCase: RefreshExercisesUseCase
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

    private val _refreshState = MutableStateFlow<Response<Unit>?>(null)
    val refreshState: StateFlow<Response<Unit>?> = _refreshState

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex


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
                }
            }
        }
    }

    // Descarga los ejercicios desde Firebase
    fun refreshExercises(courseId: String, lessonId: String) {
        viewModelScope.launch {
            _refreshState.value = Response.Loading
            val result = refreshExercisesUseCase(courseId, lessonId)
            _refreshState.value = result
        }
    }

    fun submitAnswer(option: BlockOptionDomain) {
        _selectedOptionId.value = option.id
        _answerFeedback.value = AnswerFeedback(isCorrect = option.isCorrect)
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
    fun previousExercise() {
        if (_currentExerciseIndex.value > 0) {
            _currentExerciseIndex.value -= 1
        }
    }

    // Reinicia el flujo al terminar los ejercicios
    fun resetExerciseProgress() {
        _currentExerciseIndex.value = 0
        _selectedOptionId.value = null
        _answerFeedback.value = null
        _exercisesState.value = emptyList()
    }

    fun resetRefreshState() {
        _refreshState.value = null
    }
}
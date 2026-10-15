package com.kipucode.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.domain.usecase.course.UpdateLastViewedLessonUseCase
import com.kipucode.domain.usecase.exercise.DetermineAvailablePracticeMethodsUseCase
import com.kipucode.domain.usecase.exercise.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.lesson.GetLessonByCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor (
    private val getLessonUseCase: GetLessonByCourseUseCase,
    private val updateLastViewedLessonUseCase: UpdateLastViewedLessonUseCase,
    private val getExercisesByLessonUseCase: GetExercisesByLessonUseCase,
    private val determineAvailablePracticeMethodsUseCase: DetermineAvailablePracticeMethodsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    fun selectMethod(method: PracticeMethod) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    fun getLessonById(lessonId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updateLastViewedLessonUseCase(lessonId)

            combine(
                getLessonUseCase(lessonId),
                getExercisesByLessonUseCase(lessonId)
            ) { lesson, exercises ->
                val methods = determineAvailablePracticeMethodsUseCase(exercises.map { it.type })
                _uiState.value.copy(
                    isLoading = false,
                    lesson = lesson,
                    availableMethods = methods,
                    selectedMethod = _uiState.value.selectedMethod ?: methods.firstOrNull(),
                    errorMessage = if (lesson == null) "No se pudo cargar la información de la lección." else null
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}

package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.domain.repository.ExerciseRepository
import com.kipucode.domain.usecase.RefreshCoursesUseCase
import com.kipucode.domain.usecase.RefreshLearningProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CodePracticeAvailability(
    val completeCodeCount: Int = 0,
    val whatsOutputCount: Int = 0
)

data class CodeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val availability: CodePracticeAvailability = CodePracticeAvailability(),
    val selectedMethod: PracticeMethod = PracticeMethod.COMPLETE_CODE
)

@HiltViewModel
class CodeViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val refreshLearningProgressUseCase: RefreshLearningProgressUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase,
    private val refreshUserProgressUseCase: RefreshUserProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CodeUiState())
    val uiState: StateFlow<CodeUiState> = _uiState.asStateFlow()

    init {
        syncDataInBackground()
        observeAvailableExercises()
    }

    private fun observeAvailableExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                val completeCount = exercises.count { it.type == "COMPLETE_CODE" }
                val whatsOutputCount = exercises.count { it.type == "WHATS_OUTPUT" }

                _uiState.update { state ->
                    state.copy(
                        availability = CodePracticeAvailability(
                            completeCodeCount = completeCount,
                            whatsOutputCount = whatsOutputCount
                        )
                    )
                }
            }
        }
    }

    fun selectMethod(method: PracticeMethod) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    private fun syncDataInBackground() {
        viewModelScope.launch {
            refreshLearningProgressUseCase()
            refreshCoursesUseCase()
            refreshUserProgressUseCase()
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                refreshLearningProgressUseCase()
                refreshCoursesUseCase()
                refreshUserProgressUseCase()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }
}

package com.kipucode.ui.screens.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.domain.usecase.activity.CalculateProfileHeatmapUseCase
import com.kipucode.domain.usecase.activity.GetActivityCalendarUseCase
import com.kipucode.domain.usecase.activity.RefreshActivityCalendarUseCase
import com.kipucode.domain.usecase.course.RefreshCoursesUseCase
import com.kipucode.domain.usecase.exercise.GetCodePracticeAvailabilityUseCase
import com.kipucode.domain.usecase.learning.RefreshLearningProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CodeViewModel @Inject constructor(
    private val getCodePracticeAvailabilityUseCase: GetCodePracticeAvailabilityUseCase,
    private val getActivityCalendarUseCase: GetActivityCalendarUseCase,
    private val calculateProfileHeatmapUseCase: CalculateProfileHeatmapUseCase,
    private val refreshActivityCalendarUseCase: RefreshActivityCalendarUseCase,
    private val refreshLearningProgressUseCase: RefreshLearningProgressUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase,
    private val refreshUserProgressUseCase: RefreshUserProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CodeUiState())
    val uiState: StateFlow<CodeUiState> = _uiState.asStateFlow()

    init {
        syncDataInBackground()
        observeAvailableExercises()
        observeActivityHeatmap()
    }

    private fun observeAvailableExercises() {
        viewModelScope.launch {
            getCodePracticeAvailabilityUseCase().collect { availabilityDomain ->
                _uiState.update { state ->
                    state.copy(
                        availability = CodePracticeAvailability(
                            completeCodeCount = availabilityDomain.completeCodeCount,
                            whatsOutputCount = availabilityDomain.whatsOutputCount
                        )
                    )
                }
            }
        }
    }

    private fun observeActivityHeatmap() {
        viewModelScope.launch {
            getActivityCalendarUseCase().collect { activities ->
                val heatmap = calculateProfileHeatmapUseCase(activities)
                _uiState.update { state ->
                    state.copy(
                        heatmapData = HeatmapUiData(
                            year = heatmap.year,
                            totalActiveDays = heatmap.totalActiveDays,
                            totalExercises = heatmap.totalExercises,
                            totalXp = heatmap.totalXp,
                            activityMap = heatmap.activityMap
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
            refreshActivityCalendarUseCase()
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                refreshLearningProgressUseCase()
                refreshCoursesUseCase()
                refreshUserProgressUseCase()
                refreshActivityCalendarUseCase()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }
}

package com.kipucode.ui.screens.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kipucode.domain.usecase.summary.CalculateSessionMetricsUseCase
import com.kipucode.domain.usecase.summary.CalculateWeeklyStreakSummaryUseCase
import com.kipucode.domain.usecase.summary.WeekDayStatus
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.ui.navigation.SummaryRoute
import com.kipucode.ui.screens.summary.components.DayStatus
import com.kipucode.ui.screens.summary.components.DayStreakUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val calculateSessionMetricsUseCase: CalculateSessionMetricsUseCase,
    private val calculateWeeklyStreakSummaryUseCase: CalculateWeeklyStreakSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState(isLoading = true))
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        val route = savedStateHandle.toRoute<SummaryRoute>()
        val metrics = calculateSessionMetricsUseCase(
            correctCount = route.correct,
            totalCount = route.total,
            timeSeconds = route.timeSeconds
        )

        _uiState.update { current ->
            current.copy(
                xpEarned = route.xp,
                accuracy = metrics.accuracy,
                correctCount = metrics.correctCount,
                reinforceCount = metrics.reinforceCount,
                timeFormatted = metrics.timeFormatted,
                summaryMessage = metrics.summaryMessage,
                memoryRetentionPct = metrics.memoryRetentionPct,
                nextReviewText = "Mañana",
                isLoading = false
            )
        }

        loadStreakData()
    }

    private fun loadStreakData() {
        viewModelScope.launch {
            getUserProgressUseCase().collect { progress ->
                val streakDays = progress?.streakDay ?: 1
                val streakSummary = calculateWeeklyStreakSummaryUseCase(streakDays)

                val weekDaysUi = streakSummary.weekDays.map { day ->
                    DayStreakUI(
                        letter = day.label,
                        status = when (day.status) {
                            WeekDayStatus.COMPLETED -> DayStatus.COMPLETED
                            WeekDayStatus.CURRENT -> DayStatus.CURRENT
                            WeekDayStatus.PENDING -> DayStatus.PENDING
                        }
                    )
                }

                _uiState.update { current ->
                    current.copy(
                        currentStreakDays = streakSummary.currentStreakDays,
                        weekDays = weekDaysUi,
                        nextStreakText = streakSummary.nextStreakText
                    )
                }
            }
        }
    }
}

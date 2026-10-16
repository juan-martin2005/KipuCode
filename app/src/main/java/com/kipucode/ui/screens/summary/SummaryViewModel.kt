package com.kipucode.ui.screens.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.kipucode.domain.usecase.summary.CalculateSessionMetricsUseCase
import com.kipucode.ui.navigation.SummaryRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calculateSessionMetricsUseCase: CalculateSessionMetricsUseCase
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
    }
}


package com.kipucode.ui.screens.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kipucode.domain.usecase.learning.GetDueExercisesUseCase
import com.kipucode.domain.usecase.summary.CalculateSessionMetricsUseCase
import com.kipucode.ui.navigation.SummaryRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calculateSessionMetricsUseCase: CalculateSessionMetricsUseCase,
    private val getDueExercisesUseCase: GetDueExercisesUseCase
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

        viewModelScope.launch {
            val remainingDueCount = if (route.onlyDue) {
                try {
                    getDueExercisesUseCase().first().size
                } catch (e: Exception) {
                    0
                }
            } else 0

            val dynamicMessage = if (route.onlyDue) {
                if (remainingDueCount > 0) {
                    "¡Gran trabajo! Aún tienes conceptos por reforzar a lo largo del día. Por ahora ya consolidaste estos conocimientos clave."
                } else {
                    "¡Excelente! Estás al día con todos tus repasos. Tu memoria a largo plazo te lo agradecerá."
                }
            } else {
                metrics.summaryMessage
            }

            _uiState.update { current ->
                current.copy(
                    xpEarned = route.xp,
                    accuracy = metrics.accuracy,
                    correctCount = metrics.correctCount,
                    reinforceCount = metrics.reinforceCount,
                    timeFormatted = metrics.timeFormatted,
                    summaryMessage = dynamicMessage,
                    memoryRetentionPct = metrics.memoryRetentionPct,
                    nextReviewText = if (route.onlyDue && remainingDueCount > 0) "Hoy más tarde" else "Mañana",
                    isLoading = false
                )
            }
        }
    }
}


package com.kipucode.viewmodel

import com.kipucode.ui.screens.summary.components.DayStreakUI
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.ui.screens.summary.components.DayStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class SummaryUiState(
    // FinalSummary
    val xpEarned: Int = 0,
    val accuracy: Float = 0.0f,
    val correctCount: Int = 0,
    val reinforceCount: Int = 0,
    val timeFormatted: String = "0 seg",
    val summaryMessage: String = "",

    // RepetitionSchedule (FSRS-6)
    val memoryRetentionPct: Int = 85,
    val nextReviewText: String = "Mañana",

    //  WeeklyStreak
    val currentStreakDays: Int = 0,
    val weekDays: List<DayStreakUI> = emptyList(),
    val nextStreakText: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserProgressUseCase: GetUserProgressUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SummaryUiState(isLoading = true))
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()
    init {
        // Extraer argumentos enviados desde la sesión de ejercicios
        val xpEarned = savedStateHandle.get<Int>("xp") ?: 0
        val correctCount = savedStateHandle.get<Int>("correct") ?: 0
        val totalCount = savedStateHandle.get<Int>("total") ?: maxOf(1, correctCount)
        val timeSeconds = savedStateHandle.get<Long>("timeSeconds") ?: 0L

        // Cálculos de la sesión
        val accuracy = if (totalCount > 0) correctCount.toFloat() / totalCount.toFloat() else 0f
        val reinforceCount = maxOf(0, totalCount - correctCount)
        val timeFormatted = formatDuration(timeSeconds)
        val summaryMessage = calculateSummaryMessage(accuracy)

        // Retención estimada post-sesión (80% a 95% según desempeño en la tanda)
        val memoryRetentionPct = ((0.75f + accuracy * 0.20f) * 100).toInt()
        _uiState.update { current ->
            current.copy(
                xpEarned = xpEarned,
                accuracy = accuracy,
                correctCount = correctCount,
                reinforceCount = reinforceCount,
                timeFormatted = timeFormatted,
                summaryMessage = summaryMessage,
                memoryRetentionPct = memoryRetentionPct,
                nextReviewText = "Mañana",
                isLoading = false
            )
        }
        // Cargar la racha real y armar la semana desde la base de datos
        loadStreakData()
    }
    private fun loadStreakData() {
        viewModelScope.launch {
            getUserProgressUseCase().collect { progress ->
                val streakDays = progress?.streakDay ?: 1
                val weekDays = generateWeekDays()
                val nextStreakText = generateNextStreakMessage(streakDays)
                _uiState.update { current ->
                    current.copy(
                        currentStreakDays = streakDays,
                        weekDays = weekDays,
                        nextStreakText = nextStreakText
                    )
                }
            }
        }
    }
    private fun calculateSummaryMessage(accuracy: Float): String {
        return when {
            accuracy >= 0.9f -> "¡Impresionante! Dominio casi perfecto del tema"
            accuracy >= 0.7f -> "¡Felicidades! Dominaste la mayoría de conceptos"
            accuracy >= 0.5f -> "¡Buen intento! Repasar te ayudará a consolidar"
            else -> "¡No te rindas! La repetición te ayudará a dominarlo"
        }
    }
    private fun generateNextStreakMessage(streakDays: Int): String {
        val nextMilestone = if (streakDays < 7) 7 else streakDays + 1
        val remaining = nextMilestone - streakDays
        return if (remaining == 1) {
            "A solo 1 día de superar tu meta de $nextMilestone días"
        } else {
            "¡Mantén el ritmo mañana para sumar $nextMilestone días!"
        }
    }
    private fun generateWeekDays(): List<DayStreakUI> {
        val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
        val todayIndex = LocalDate.now().dayOfWeek.value - 1 // 0 = lunes, 6 = domingo
        return dayLabels.mapIndexed { index, label ->
            val status = when {
                index < todayIndex -> DayStatus.COMPLETED
                index == todayIndex -> DayStatus.CURRENT // Completado hoy
                else -> DayStatus.PENDING
            }
            DayStreakUI(label, status)
        }
    }
    private fun formatDuration(totalSeconds: Long): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return when {
            minutes > 0 && seconds > 0 -> "$minutes min $seconds seg"
            minutes > 0 -> "$minutes min"
            else -> "$seconds seg"
        }
    }
}
package com.kipucode.domain.usecase.summary

import java.time.LocalDate
import javax.inject.Inject

// ============================================================================================
//  MÉTRICAS DE LA SESIÓN DE EJERCICIOS
// ============================================================================================

data class SessionMetricsResult(
    val accuracy: Float,
    val correctCount: Int,
    val reinforceCount: Int,
    val timeFormatted: String,
    val summaryMessage: String,
    val memoryRetentionPct: Int
)

class CalculateSessionMetricsUseCase @Inject constructor() {

    operator fun invoke(
        correctCount: Int,
        totalCount: Int,
        timeSeconds: Long
    ): SessionMetricsResult {
        val effectiveTotal = if (totalCount > 0) totalCount else maxOf(1, correctCount)
        val accuracy = if (effectiveTotal > 0) correctCount.toFloat() / effectiveTotal.toFloat() else 0f
        val reinforceCount = maxOf(0, effectiveTotal - correctCount)
        val timeFormatted = formatDuration(timeSeconds)
        val summaryMessage = calculateSummaryMessage(accuracy)
        val memoryRetentionPct = ((0.75f + accuracy * 0.20f) * 100).toInt()

        return SessionMetricsResult(
            accuracy = accuracy,
            correctCount = correctCount,
            reinforceCount = reinforceCount,
            timeFormatted = timeFormatted,
            summaryMessage = summaryMessage,
            memoryRetentionPct = memoryRetentionPct
        )
    }

    private fun calculateSummaryMessage(accuracy: Float): String {
        return when {
            accuracy >= 0.9f -> "¡Impresionante! Dominio casi perfecto del tema"
            accuracy >= 0.7f -> "¡Felicidades! Dominaste la mayoría de conceptos"
            accuracy >= 0.5f -> "¡Buen intento! Repasar te ayudará a consolidar"
            else -> "¡No te rindas! La repetición te ayudará a dominarlo"
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

// ============================================================================================
//  RACHA SEMANAL E HITOS DE GAMIFICACIÓN
// ============================================================================================

enum class WeekDayStatus { COMPLETED, CURRENT, PENDING }

data class WeekDayStreakDomain(
    val label: String,
    val status: WeekDayStatus
)

data class WeeklyStreakSummaryResult(
    val currentStreakDays: Int,
    val nextStreakText: String,
    val weekDays: List<WeekDayStreakDomain>
)

class CalculateWeeklyStreakSummaryUseCase @Inject constructor() {

    operator fun invoke(
        streakDays: Int,
        todayDayOfWeekIndex: Int = LocalDate.now().dayOfWeek.value - 1 // 0 = Lunes, 6 = Domingo
    ): WeeklyStreakSummaryResult {
        val nextStreakText = generateNextStreakMessage(streakDays)
        val weekDays = generateWeekDays(todayDayOfWeekIndex)

        return WeeklyStreakSummaryResult(
            currentStreakDays = streakDays,
            nextStreakText = nextStreakText,
            weekDays = weekDays
        )
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

    private fun generateWeekDays(todayIndex: Int): List<WeekDayStreakDomain> {
        val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
        return dayLabels.mapIndexed { index, label ->
            val status = when {
                index < todayIndex -> WeekDayStatus.COMPLETED
                index == todayIndex -> WeekDayStatus.CURRENT
                else -> WeekDayStatus.PENDING
            }
            WeekDayStreakDomain(label, status)
        }
    }
}

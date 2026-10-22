package com.kipucode.domain.usecase.summary

import com.kipucode.domain.model.DailyActivityDomain
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
        if (totalCount <= 0 && correctCount <= 0) {
            return SessionMetricsResult(
                accuracy = 0f,
                correctCount = 0,
                reinforceCount = 0,
                timeFormatted = formatDuration(timeSeconds),
                summaryMessage = calculateSummaryMessage(0f),
                memoryRetentionPct = 0
            )
        }

        val effectiveTotal = if (totalCount > 0) totalCount else correctCount
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
        todayDayOfWeekIndex: Int = LocalDate.now().dayOfWeek.value - 1, // 0 = Lunes, 6 = Domingo
        activeWeekDayIndices: Set<Int> = emptySet()
    ): WeeklyStreakSummaryResult {
        val nextStreakText = generateNextStreakMessage(streakDays)
        val weekDays = generateWeekDays(
            todayIndex = todayDayOfWeekIndex,
            streakDays = streakDays,
            activeWeekDayIndices = activeWeekDayIndices
        )

        return WeeklyStreakSummaryResult(
            currentStreakDays = streakDays,
            nextStreakText = nextStreakText,
            weekDays = weekDays
        )
    }

    fun extractActiveWeekDayIndices(
        activities: List<DailyActivityDomain>,
        todayDate: LocalDate = LocalDate.now(),
        lastCompletedMillis: Long? = null
    ): Set<Int> {
        val todayIndex = todayDate.dayOfWeek.value - 1
        val weekStart = todayDate.minusDays(todayIndex.toLong())
        val activeDates = activities
            .filter { it.exercisesCount > 0 || it.lessonsCount > 0 || it.xpEarned > 0 }
            .map { it.date }
            .toMutableSet()

        if (lastCompletedMillis != null) {
            val lastDate = Instant.ofEpochMilli(lastCompletedMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            activeDates.add(lastDate.toString())
        }

        return (0..6).filter { index ->
            val dateStr = weekStart.plusDays(index.toLong()).toString()
            dateStr in activeDates
        }.toSet()
    }

    private fun generateNextStreakMessage(streakDays: Int): String {
        val milestones = listOf(3, 7, 14, 30, 60, 100, 365)
        val nextMilestone = milestones.firstOrNull { it > streakDays } ?: ((streakDays / 50 + 1) * 50)
        val remaining = nextMilestone - streakDays
        return if (remaining == 1) {
            "A solo 1 día de superar tu meta de $nextMilestone días"
        } else {
            "¡Mantén el ritmo mañana para alcanzar tu meta de $nextMilestone días!"
        }
    }

    private fun generateWeekDays(
        todayIndex: Int,
        streakDays: Int,
        activeWeekDayIndices: Set<Int>
    ): List<WeekDayStreakDomain> {
        val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
        val isTodayCompleted = todayIndex in activeWeekDayIndices

        // Si hoy ya se completó, la racha llega hasta hoy.
        // Si hoy aún está pendiente, la racha activa acumulada llega hasta ayer.
        val streakEndIndex = if (isTodayCompleted) todayIndex else todayIndex - 1
        val startCompletedIndex = if (streakDays >= 1 && streakEndIndex >= 0) {
            maxOf(0, streakEndIndex - (streakDays - 1))
        } else {
            -1
        }

        return dayLabels.mapIndexed { index, label ->
            val isCompletedByStreak = startCompletedIndex != -1 && index in startCompletedIndex..streakEndIndex
            val isCompletedByActivity = index <= todayIndex && index in activeWeekDayIndices

            val status = when {
                isCompletedByStreak || isCompletedByActivity -> WeekDayStatus.COMPLETED
                index == todayIndex -> WeekDayStatus.CURRENT
                else -> WeekDayStatus.PENDING
            }
            WeekDayStreakDomain(label, status)
        }
    }
}

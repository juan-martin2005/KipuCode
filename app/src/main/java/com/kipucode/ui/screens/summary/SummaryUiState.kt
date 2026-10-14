package com.kipucode.ui.screens.summary

import com.kipucode.ui.screens.summary.components.DayStreakUI

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

    // WeeklyStreak
    val currentStreakDays: Int = 0,
    val weekDays: List<DayStreakUI> = emptyList(),
    val nextStreakText: String = "",
    val isLoading: Boolean = false
)

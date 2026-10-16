package com.kipucode.ui.screens.summary

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

    val isLoading: Boolean = false
)


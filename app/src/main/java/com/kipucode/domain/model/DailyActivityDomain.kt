package com.kipucode.domain.model

data class DailyActivityDomain(
    val date: String, // "YYYY-MM-DD"
    val year: Int,
    val timestamp: Long = 0L,
    val exercisesCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val xpEarned: Int = 0,
    val lessonsCount: Int = 0
)

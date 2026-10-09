package com.kipucode.domain.model

data class ExerciseAttemptDomain(
    val exerciseId: String,
    val userId: String,
    val lessonId: String,
    val exerciseType: String,
    val isCompleted: Boolean,
    val attemptsCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val lastIsCorrect: Boolean,
    val lastAttemptAt: Long
)

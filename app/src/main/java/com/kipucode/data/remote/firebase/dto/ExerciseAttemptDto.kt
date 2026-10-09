package com.kipucode.data.remote.firebase.dto

data class ExerciseAttemptDto(
    val exerciseId: String = "",
    val lessonId: String = "",
    val exerciseType: String = "",
    val isCompleted: Boolean = false,
    val attemptsCount: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val lastIsCorrect: Boolean = false,
    val lastAttemptAt: Long = 0L
)

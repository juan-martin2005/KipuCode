package com.kipucode.data.remote.firebase.dto

data class LearningProgressDto(
    val exerciseId: String = "",
    val difficulty: Double = 0.0,
    val stability: Double = 0.0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val state: Int = 0,
    val dueDate: Long = 0L,
    val lastReviewed: Long? = null
)

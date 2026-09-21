package com.kipucode.domain.model

data class LearningProgressDomain(
    val exerciseId: String,
    val userId: String,
    val difficulty: Double = 0.0,
    val stability: Double = 0.0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val state: Int = 0, // 0=New, 1=Learning, 2=Review, 3=Relearning
    val dueDate: Long = System.currentTimeMillis(),
    val lastReviewed: Long? = null
)

package com.kipucode.domain.model

data class UserProgress(
    val id: String,
    val userId: String,
    val lessonId: String,
    val status: String,
    val score: Int,
    val completedAt: Long?
)

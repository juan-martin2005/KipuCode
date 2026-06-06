package com.kipucode.domain.model

data class UserProgress(
    val id: String,
    val userId: String,
    val currentLessonId: String,
    val status: String,
    val score: Int,
    val completedAt: Long?,
    val totalXp: Int,
    val points: Int,
    val streakDay: Int,
    val completedLessons: List<String>,
    val completedCourses: List<String>
)

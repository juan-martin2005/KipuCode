package com.kipucode.domain.model

data class UserProgressDomain(
    val id: String,
    val userId: String,
    val currentLessonId: String = "",
    val status: String = "",
    val points: Int = 0,
    val totalXp: Int = 0,
    val streakDay: Int = 0,
    val score: Int = 0,
    val completedAt: Long? = null,
    val completedLessons: List<String> = emptyList(),
    val completedCourses: List<String> = emptyList()
)

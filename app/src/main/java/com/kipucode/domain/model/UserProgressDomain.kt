package com.kipucode.domain.model

data class UserProgressDomain(
    val id: String,
    val userId: String,
    val activeTrack: String? = null,
    val lastVisitedLessonId: String? = null,
    val totalXp: Int = 0,
    val streakDay: Int = 0,
    val completedAt: Long? = null,
    val completedLessons: List<String> = emptyList(),
    val completedCourses: List<String> = emptyList(),
    val lessonsXpRecord: Map<String, Int> = emptyMap()
)

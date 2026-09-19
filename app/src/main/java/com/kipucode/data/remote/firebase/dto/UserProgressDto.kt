package com.kipucode.data.remote.firebase.dto

import com.google.firebase.Timestamp

data class UserProgressDto(
    val userId: String = "",
    val currentLessonId: String = "",
    val status: String = "",
    val totalXp: Int = 0,
    val streakDay: Int = 0,
    val completedAt: Timestamp? = null,
    val completedLessons: List<String> = emptyList(),
    val completedCourses: List<String> = emptyList(),
    val lessonsXpRecord: Map<String, Int> = emptyMap()
)

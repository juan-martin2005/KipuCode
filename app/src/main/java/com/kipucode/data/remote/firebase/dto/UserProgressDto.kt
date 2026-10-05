package com.kipucode.data.remote.firebase.dto

import com.google.firebase.Timestamp

data class UserProgressDto(
    val userId: String = "",
    val activeTrack: String? = null,
    val lastVisitedLessonId: String? = null,
    val totalXp: Int = 0,
    val streakDay: Int = 0,
    val completedAt: Timestamp? = null,
    val completedLessons: List<String> = emptyList(),
    val completedCourses: List<String> = emptyList(),
    val lessonsXpRecord: Map<String, Int> = emptyMap()
)

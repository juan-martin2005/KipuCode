package com.kipucode.data.remote.firebase.dto

import com.google.firebase.Timestamp

data class UserProgressDto(
    val userId: String = "",
    val currentLessonId: String = "",
    val points: Int = 0,
    val totalXp: Int = 0,
    val streakDays: Int = 0,
    val score: Int = 0,
    val completedLessons: List<String> = listOf(""),
    val completedCourses: List<String> = listOf(""),
    val completedAt : Timestamp? = null
)

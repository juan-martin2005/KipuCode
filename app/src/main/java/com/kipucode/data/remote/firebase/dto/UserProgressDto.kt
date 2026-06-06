package com.kipucode.data.remote.firebase.dto

import com.google.firebase.Timestamp

data class UserProgressDto(
    val userId: String = "",
    val currentLessonId: String = "",
    val status: String = "",
    val points: Int = 0,
    val totalXp: Int = 0,
    val streakDay: Int = 0,
    val score: Int = 0,
    val completedAt : Timestamp? = null,
    val completedLessons: List<String> = listOf(""),
    val completedCourses: List<String> = listOf(""),
)

package com.kipucode.data.remote.firebase.dto

data class LessonDto(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val content: String = "",
    val orderIndex: Int = 0,
    val xp: Int = 0
)

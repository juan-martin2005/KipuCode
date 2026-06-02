package com.kipucode.data.remote.firebase.dto


data class LessonDto(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val content: String = "",
    val points: Int = 0,
    val exp: Int = 0,
    val orderIndex: Int = 0
)

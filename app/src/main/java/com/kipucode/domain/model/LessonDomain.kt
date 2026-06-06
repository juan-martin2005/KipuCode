package com.kipucode.domain.model

data class LessonDomain(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val content: String = "",
    val points: Int = 0,
    val exp: Int = 0,
    val orderIndex: Int = 0
)
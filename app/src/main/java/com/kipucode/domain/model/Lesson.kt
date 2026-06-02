package com.kipucode.domain.model

data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val content: String,
    val points: Int,
    val exp: Int,
    val orderIndex: Int
)

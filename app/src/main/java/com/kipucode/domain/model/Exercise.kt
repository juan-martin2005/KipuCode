package com.kipucode.domain.model

data class Exercise(
    val id: String,
    val lessonId: String,
    val type: String,
    val instruction: String,
    val orderIndex: Int
)

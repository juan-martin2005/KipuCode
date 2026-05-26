package com.kipucode.domain.model

data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val theoryContent: String,
    val orderIndex: Int
)

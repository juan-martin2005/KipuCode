package com.kipucode.data.remote.firebase.dto


data class LessonDto(
    val id: String,
    val courseId: String,
    val title: String,
    val theoryContent: String,
    val orderIndex: Int
)

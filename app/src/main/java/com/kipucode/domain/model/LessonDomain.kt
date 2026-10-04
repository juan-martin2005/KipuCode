package com.kipucode.domain.model

data class LessonDomain(
    val id: String = "",
    val courseId: String = "",
    val title: String = "",
    val content: String = "",
    val blocks: List<LessonBlock> = emptyList(),
    val xp: Int = 0,
    val orderIndex: Int = 0
)
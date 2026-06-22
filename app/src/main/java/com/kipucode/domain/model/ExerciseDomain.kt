package com.kipucode.domain.model

data class ExerciseDomain(
    val id: String,
    val lessonId: String,
    val type: String,
    val instruction: String,
    val answer: String = "",
    val points: Int,
    val exp: Int,
    val orderIndex: Int,

    val options: List<BlockOptionDomain> = emptyList()
)

package com.kipucode.domain.model

data class DueExerciseDomain(
    val exerciseId: String,
    val lessonId: String,
    val lessonTitle: String,
    val exerciseType: String,
    val dueDate: Long,
    val retentionPercentage: Int,
    val stability: Double
)

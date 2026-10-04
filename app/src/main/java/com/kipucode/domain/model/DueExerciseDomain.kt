package com.kipucode.domain.model

data class DueExerciseDomain(
    val exerciseId: String,
    val lessonId: String,
    val lessonTitle: String,
    val courseId: String = "",
    val courseTitle: String = "",
    val courseOrderIndex: Int = 0,
    val lessonOrderIndex: Int = 0,
    val exerciseType: String,
    val dueDate: Long,
    val retentionPercentage: Int,
    val stability: Double
)

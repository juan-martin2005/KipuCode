package com.kipucode.data.remote.firebase.dto

data class DayActivityDto(
    val exercises: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0,
    val xp: Int = 0,
    val lessons: Int = 0
)

data class YearActivityDto(
    val year: Int = 0,
    val days: Map<String, DayActivityDto> = emptyMap()
)

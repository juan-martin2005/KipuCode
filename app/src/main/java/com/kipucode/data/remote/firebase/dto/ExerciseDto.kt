package com.kipucode.data.remote.firebase.dto

data class ExerciseDto(
    val id : String = "",
    val lessonId : String = "",
    val type: String? = "",
    val instruction : String = "",
    val orderIndex : Int = 0,
    val exp : Int = 0,
    val points : Int = 0
)

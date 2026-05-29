package com.kipucode.data.remote.firebase.dto

import com.google.firebase.Timestamp

data class CourseDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val orderIndex: Int = 0,
    val createdAt: Timestamp? = null
)

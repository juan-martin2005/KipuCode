package com.kipucode.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val completed: Boolean,
    val createdAt: Long?
)

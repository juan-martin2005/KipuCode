package com.kipucode.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val createdAt: Long?
)

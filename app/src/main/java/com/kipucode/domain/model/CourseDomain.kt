package com.kipucode.domain.model

data class CourseDomain(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val orderIndex: Int = 0,
    val points: Int = 0,
    val exp: Int = 0,
    val createdAt: Long? = 0
)

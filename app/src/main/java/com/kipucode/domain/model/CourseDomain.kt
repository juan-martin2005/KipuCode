package com.kipucode.domain.model

data class CourseDomain(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val track: String = "",
    val orderIndex: Int = 0,
    val xp: Int = 0,
    val createdAt: Long? = 0
)

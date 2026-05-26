package com.kipucode.domain.model

data class User(
    val id: String,
    val name: String?,
    val email: String,
    val totalXp: Int = 0,
    val streakDay: Int = 0
)

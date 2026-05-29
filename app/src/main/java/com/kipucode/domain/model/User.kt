package com.kipucode.domain.model

data class User(
    val name: String,
    val email: String,
    val id: String = "",
    val totalXp: Int = 0,
    val streakDay: Int = 0
)

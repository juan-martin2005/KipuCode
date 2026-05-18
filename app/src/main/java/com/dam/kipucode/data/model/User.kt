package com.dam.kipucode.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name: String?,
    val email: String?,
    val total_xp: Int,
    val streak_day: Int,
    val token: String?
)

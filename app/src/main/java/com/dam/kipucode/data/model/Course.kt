package com.dam.kipucode.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    val id: String,
    val title: String?,
    val description: String?,
    val order_index: Int,
    val created_at: Long?
)

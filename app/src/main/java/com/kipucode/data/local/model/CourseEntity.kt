package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val track: String = "",
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    val points: Int,
    val exp: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long?
)

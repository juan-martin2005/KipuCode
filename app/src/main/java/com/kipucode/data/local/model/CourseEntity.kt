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
    @ColumnInfo(name = "order_index")
    val orderIndex: Int,
    val completed: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long?
)

package com.dam.kipucode.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns = ["course_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Lesson(
    @PrimaryKey
    val id: String,
    val course_id: String?,
    val title: String?,
    val theory_content: String?,
    val order_index: Int
)

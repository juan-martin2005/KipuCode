package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(
        entity = CourseEntity::class,
        parentColumns = ["id"],
        childColumns = ["course_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["course_id"])]
)
data class LessonEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "course_id") val courseId: String,
    val title: String,
    @ColumnInfo(name = "theory_content") val content: String,
    val xp: Int = 0,
    @ColumnInfo(name = "order_index") val orderIndex: Int
)

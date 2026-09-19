package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = LessonEntity::class,
        parentColumns = ["id"],
        childColumns = ["lesson_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["lesson_id"])]
)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "lesson_id") val lessonId: String,
    val type: String = "UNIQUE_CHOICE",
    val instruction: String = "",
    val answer: String = "",
    val xp: Int = 0,
    @ColumnInfo(name = "order_index") val orderIndex: Int
)

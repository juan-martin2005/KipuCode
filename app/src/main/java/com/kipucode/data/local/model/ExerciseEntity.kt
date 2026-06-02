package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = LessonEntity::class,
        parentColumns = ["id"],
        childColumns = ["lesson_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "lesson_id")
    val lessonId: String?,
    val type: String?,
    val instruction: String?,
    val points: Int,
    val exp: Int,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int
)

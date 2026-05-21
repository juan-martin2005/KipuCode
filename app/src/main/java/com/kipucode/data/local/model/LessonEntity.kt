package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(
        entity = CourseEntity::class,
        parentColumns = ["id"],
        childColumns = ["course_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LessonEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "course_id")
    val courseId: String?,
    val title: String?,
    @ColumnInfo(name = "theory_content")
    val theoryContent: String?,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int
)

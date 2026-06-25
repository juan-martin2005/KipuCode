package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
        ),
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
        )
    ]
)
data class UserProgressEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id") val userId: String?,
    @ColumnInfo(name = "lesson_id") val lessonId: String?,
    val status: String?,
    val score: Int,
    @ColumnInfo(name = "total_xp") val totalXp: Int,
    val points: Int,
    @ColumnInfo(name = "streak_day") val streakDay: Int,
    @ColumnInfo(name = "completed_at") val completedAt: Long?,
    @ColumnInfo(name = "completed_lessons") val completedLessons: List<String>,
    @ColumnInfo(name = "completed_courses") val completedCourses: List<String>,
    @ColumnInfo(name = "lessons_xp_record") val lessonsXpRecord: Map<String, Int>

)

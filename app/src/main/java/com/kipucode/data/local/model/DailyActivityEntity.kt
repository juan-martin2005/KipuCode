package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_activities",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "date"], unique = true),
        Index(value = ["user_id", "year"])
    ]
)
data class DailyActivityEntity(
    @PrimaryKey
    val id: String, // "${userId}_${date}"
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "date") val date: String, // "YYYY-MM-DD"
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "exercises_count") val exercisesCount: Int = 0,
    @ColumnInfo(name = "correct_count") val correctCount: Int = 0,
    @ColumnInfo(name = "incorrect_count") val incorrectCount: Int = 0,
    @ColumnInfo(name = "xp_earned") val xpEarned: Int = 0,
    @ColumnInfo(name = "lessons_count") val lessonsCount: Int = 0
)

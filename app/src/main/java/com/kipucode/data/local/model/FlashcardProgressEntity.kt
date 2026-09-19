package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "flashcard_progress",
    primaryKeys = ["exercise_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["user_id"])
    ]
)
data class FlashcardProgressEntity(
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val repetitions: Int = 0,
    val interval: Int = 1,
    @ColumnInfo(name = "ease_factor") val easeFactor: Double = 2.5,
    @ColumnInfo(name = "next_review_date") val nextReview: Long,
    @ColumnInfo(name = "last_reviewed") val lastReview: Long
)
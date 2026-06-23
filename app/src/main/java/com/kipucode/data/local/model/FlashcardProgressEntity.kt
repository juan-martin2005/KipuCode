package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_progress")
data class FlashcardProgressEntity(
    @PrimaryKey val
    exerciseId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val repetitions: Int = 0,
    val interval: Int = 1,
    @ColumnInfo(name = "ease_factor") val easeFactor: Double = 2.5,
    @ColumnInfo(name = "next_review_date") val nextReview: Long,
    @ColumnInfo(name = "last_reviewed") val lastReview: Long
)
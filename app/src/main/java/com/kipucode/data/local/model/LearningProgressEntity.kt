package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "learning_progress",
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
data class LearningProgressEntity(
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val difficulty: Double = 0.0,
    val stability: Double = 0.0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val state: Int = 0, // 0=New, 1=Learning, 2=Review, 3=Relearning
    @ColumnInfo(name = "due_date") val dueDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_reviewed") val lastReviewed: Long? = null
)

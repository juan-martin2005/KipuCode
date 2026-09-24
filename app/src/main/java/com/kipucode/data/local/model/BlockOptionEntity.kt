package com.kipucode.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocks_options",
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["id"],
        childColumns = ["exercise_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["exercise_id"])]
)
data class BlockOptionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "exercise_id") val exerciseId: String,
    val content: String = "",
    @ColumnInfo(name = "explanation") val explanation: String = "",
    @ColumnInfo(name = "is_correct") val isCorrect: Boolean,
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0
)

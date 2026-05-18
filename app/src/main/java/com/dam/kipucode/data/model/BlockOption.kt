package com.dam.kipucode.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jg.kipucode.data.local.model.Exercise

@Entity(
    tableName = "blocks_options",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = ["id"],
        childColumns = ["exercise_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BlockOption(
    @PrimaryKey
    val id: String,
    val exercise_id: String?,
    val content: String?,
    val is_correct: Boolean,
    val correct_position: Int
)

package com.dam.kipucode.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dam.kipucode.ui.screens.home.Lesson

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = Lesson::class,
        parentColumns = ["id"],
        childColumns = ["lesson_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Exercise(
    @PrimaryKey
    val id: String,
    val lesson_id: String?,
    val type: String?,
    val instruction: String?,
    val order_index: Int
)

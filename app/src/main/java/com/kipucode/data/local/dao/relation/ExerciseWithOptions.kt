package com.kipucode.data.local.dao.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kipucode.data.local.model.BlockOptionEntity
import com.kipucode.data.local.model.ExerciseEntity

data class ExerciseWithOptions(
    @Embedded
    val exercise: ExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exercise_id"
    )
    val options: List<BlockOptionEntity>
)
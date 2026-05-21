package com.kipucode.data.local.dao.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.local.model.LessonEntity

data class LessonWithExercises(
    @Embedded
    val lesson: LessonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "lesson_id"
    )
    val exercises: List<ExerciseEntity>
)

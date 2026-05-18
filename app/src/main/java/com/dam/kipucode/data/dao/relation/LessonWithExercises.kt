package com.dam.kipucode.data.dao.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.jg.kipucode.data.local.model.Exercise
import com.jg.kipucode.data.local.model.Lesson

data class LessonWithExercises(
    @Embedded
    val lesson: Lesson,
    @Relation(
        parentColumn = "id",
        entityColumn = "lesson_id"
    )
    val exercises: List<Exercise>
)

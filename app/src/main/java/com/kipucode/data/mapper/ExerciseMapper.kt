package com.kipucode.data.mapper

import com.kipucode.data.local.dao.relation.ExerciseWithOptions
import com.kipucode.data.local.model.BlockOptionEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain

// ==========================================
// Mapeo de Room (Relation) a Dominio (Domain)
// ==========================================
fun ExerciseWithOptions.toDomain(): ExerciseDomain {
    return ExerciseDomain(
        id = this.exercise.id,
        lessonId = this.exercise.lessonId,
        type = this.exercise.type,
        instruction = this.exercise.instruction,
        answer = this.exercise.answer,
        xp = this.exercise.xp,
        orderIndex = this.exercise.orderIndex,
        options = this.options.sortedBy { it.orderIndex }.map { it.toDomain() }
    )
}

fun BlockOptionEntity.toDomain(): BlockOptionDomain {
    return BlockOptionDomain(
        id = this.id,
        exerciseId = this.exerciseId,
        content = this.content,
        explanation = this.explanation,
        isCorrect = this.isCorrect,
        orderIndex = this.orderIndex
    )
}
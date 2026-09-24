package com.kipucode.data.mapper

import com.kipucode.data.local.dao.relation.ExerciseWithOptions
import com.kipucode.data.local.model.BlockOptionEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.remote.firebase.dto.BlockOptionDto
import com.kipucode.data.remote.firebase.dto.ExerciseDto
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain

// ==========================================
// Mapeo de Firebase (DTO) a Room (Entity)
// ==========================================
fun ExerciseDto.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = this.id,
        lessonId = this.lessonId,
        type = this.type ?: "UNIQUE_CHOICE",
        instruction = this.instruction,
        answer = this.answer,
        xp = this.xp,
        orderIndex = this.orderIndex
    )
}

fun BlockOptionDto.toEntity(exerciseId: String): BlockOptionEntity {
    return BlockOptionEntity(
        id = "${exerciseId}_${this.id}",
        exerciseId = exerciseId,
        content = this.content,
        explanation = this.explanation,
        isCorrect = this.correct,
        orderIndex = this.orderIndex
    )
}

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
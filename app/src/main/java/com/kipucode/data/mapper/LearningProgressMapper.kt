package com.kipucode.data.mapper

import com.kipucode.data.local.model.LearningProgressEntity
import com.kipucode.data.remote.firebase.dto.LearningProgressDto
import com.kipucode.domain.model.LearningProgressDomain
import io.github.openspacedrepetition.Card
import io.github.openspacedrepetition.State
import java.time.Instant

// =========================================================================================
//  Entity -> Domain
fun LearningProgressEntity.toDomain(): LearningProgressDomain {
    return LearningProgressDomain(
        exerciseId = this.exerciseId,
        userId = this.userId,
        difficulty = this.difficulty,
        stability = this.stability,
        reps = this.reps,
        lapses = this.lapses,
        state = this.state,
        dueDate = this.dueDate,
        lastReviewed = this.lastReviewed
    )
}
// =========================================================================================
//  Domain -> Entity
fun LearningProgressDomain.toEntity(): LearningProgressEntity {
    return LearningProgressEntity(
        exerciseId = this.exerciseId,
        userId = this.userId,
        difficulty = this.difficulty,
        stability = this.stability,
        reps = this.reps,
        lapses = this.lapses,
        state = this.state,
        dueDate = this.dueDate,
        lastReviewed = this.lastReviewed
    )
}
// =========================================================================================
//  Entity -> FSRS Card
fun LearningProgressEntity.toFsrsCard(): Card {
    val builder = Card.builder()
        .difficulty(this.difficulty)
        .stability(this.stability)
        .due(Instant.ofEpochMilli(this.dueDate))

    this.lastReviewed?.let {
        builder.lastReview(Instant.ofEpochMilli(it))
    }

    val stateEnum = when (this.state) {
        State.LEARNING.ordinal -> State.LEARNING
        State.REVIEW.ordinal -> State.REVIEW
        State.RELEARNING.ordinal -> State.RELEARNING
        else -> State.LEARNING
    }
    builder.state(stateEnum)

    // FSRS requiere step != null para estados intradía (LEARNING y RELEARNING).
    // Card.Builder omite inicializar step si state == RELEARNING dejándolo en null,
    // lo que provocaba un NullPointerException interno en reviewCard() que abortaba el guardado en SQLite.
    if (stateEnum == State.RELEARNING || stateEnum == State.LEARNING) {
        builder.step(0)
    }

    return builder.build()
}
// =========================================================================================
//  FSRS Card -> Entity
fun Card.toEntity(
    exerciseId: String,
    userId: String,
    reps: Int,
    lapses: Int
): LearningProgressEntity {
    return LearningProgressEntity(
        exerciseId = exerciseId,
        userId = userId,
        difficulty = this.difficulty ?: 0.0,
        stability = this.stability ?: 0.0,
        reps = reps,
        lapses = lapses,
        state = this.state?.ordinal ?: State.LEARNING.ordinal,
        dueDate = this.due?.toEpochMilli() ?: System.currentTimeMillis(),
        lastReviewed = this.lastReview?.toEpochMilli() ?: System.currentTimeMillis()
    )
}
// =========================================================================================
//  Domain -> DTO
fun LearningProgressDomain.toDto(): LearningProgressDto {
    return LearningProgressDto(
        exerciseId = this.exerciseId,
        difficulty = this.difficulty,
        stability = this.stability,
        reps = this.reps,
        lapses = this.lapses,
        state = this.state,
        dueDate = this.dueDate,
        lastReviewed = this.lastReviewed
    )
}
// =========================================================================================
//  DTO -> Entity
fun LearningProgressDto.toEntity(userId: String): LearningProgressEntity {
    return LearningProgressEntity(
        exerciseId = this.exerciseId,
        userId = userId,
        difficulty = this.difficulty,
        stability = this.stability,
        reps = this.reps,
        lapses = this.lapses,
        state = this.state,
        dueDate = this.dueDate,
        lastReviewed = this.lastReviewed
    )
}

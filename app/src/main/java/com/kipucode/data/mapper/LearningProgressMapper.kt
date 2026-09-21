package com.kipucode.data.mapper

import com.kipucode.data.local.model.LearningProgressEntity
import com.kipucode.data.remote.firebase.dto.LearningProgressDto
import com.kipucode.domain.model.LearningProgressDomain
import io.github.openspacedrepetition.Card
import io.github.openspacedrepetition.State
import java.time.Instant

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

    return builder.build()
}

fun Card.toEntity(
    exerciseId: String,
    userId: String,
    prevReps: Int = 0,
    prevLapses: Int = 0,
    wasLapse: Boolean = false
): LearningProgressEntity {
    return LearningProgressEntity(
        exerciseId = exerciseId,
        userId = userId,
        difficulty = this.difficulty ?: 0.0,
        stability = this.stability ?: 0.0,
        reps = prevReps + 1,
        lapses = if (wasLapse) prevLapses + 1 else prevLapses,
        state = this.state?.ordinal ?: State.LEARNING.ordinal,
        dueDate = this.due?.toEpochMilli() ?: System.currentTimeMillis(),
        lastReviewed = this.lastReview?.toEpochMilli() ?: System.currentTimeMillis()
    )
}

// ============================================================================================
//  Mappers para Firebase (DTO)
// ============================================================================================
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

fun LearningProgressDto.toDomain(userId: String): LearningProgressDomain {
    return LearningProgressDomain(
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

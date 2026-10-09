package com.kipucode.data.mapper

import com.kipucode.data.local.model.ExerciseAttemptEntity
import com.kipucode.data.remote.firebase.dto.ExerciseAttemptDto
import com.kipucode.domain.model.ExerciseAttemptDomain

fun ExerciseAttemptEntity.toDomain() = ExerciseAttemptDomain(
    exerciseId = exerciseId,
    userId = userId,
    lessonId = lessonId,
    exerciseType = exerciseType,
    isCompleted = isCompleted,
    attemptsCount = attemptsCount,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    lastIsCorrect = lastIsCorrect,
    lastAttemptAt = lastAttemptAt
)

fun ExerciseAttemptDomain.toEntity() = ExerciseAttemptEntity(
    exerciseId = exerciseId,
    userId = userId,
    lessonId = lessonId,
    exerciseType = exerciseType,
    isCompleted = isCompleted,
    attemptsCount = attemptsCount,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    lastIsCorrect = lastIsCorrect,
    lastAttemptAt = lastAttemptAt
)

fun ExerciseAttemptDomain.toDto() = ExerciseAttemptDto(
    exerciseId = exerciseId,
    lessonId = lessonId,
    exerciseType = exerciseType,
    isCompleted = isCompleted,
    attemptsCount = attemptsCount,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    lastIsCorrect = lastIsCorrect,
    lastAttemptAt = lastAttemptAt
)

fun ExerciseAttemptDto.toEntity(userId: String) = ExerciseAttemptEntity(
    exerciseId = exerciseId,
    userId = userId,
    lessonId = lessonId,
    exerciseType = exerciseType,
    isCompleted = isCompleted,
    attemptsCount = attemptsCount,
    correctCount = correctCount,
    incorrectCount = incorrectCount,
    lastIsCorrect = lastIsCorrect,
    lastAttemptAt = lastAttemptAt
)

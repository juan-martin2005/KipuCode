package com.kipucode.domain.repository

import com.kipucode.domain.model.ExerciseAttemptDomain
import com.kipucode.domain.model.Response

interface ExerciseAttemptRepository {
    suspend fun recordAttempt(
        exerciseId: String,
        lessonId: String,
        exerciseType: String,
        isCorrect: Boolean
    ): Response<ExerciseAttemptDomain>

    suspend fun getCompletedExerciseIdsForLesson(lessonId: String): List<String>
    suspend fun getAttemptsForLesson(lessonId: String): List<ExerciseAttemptDomain>
    suspend fun getAllAttemptsForUser(): List<ExerciseAttemptDomain>
    suspend fun fetchAllAttemptsFromRemote(): Response<Unit>
}

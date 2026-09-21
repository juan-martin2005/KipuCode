package com.kipucode.domain.repository

import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface LearningProgressRepository {
    fun getDueExercises(): Flow<List<LearningProgressDomain>>
    fun getProgressForExercise(exerciseId: String): Flow<LearningProgressDomain?>
    suspend fun recordChoiceAttempt(exerciseId: String, isCorrect: Boolean): Response<Unit>
    suspend fun recordRatingAttempt(exerciseId: String, ratingValue: Int): Response<Unit>
    suspend fun saveLearningProgress(progress: LearningProgressDomain): Response<Unit>
    suspend fun refreshLearningProgress(): Response<Unit>
}

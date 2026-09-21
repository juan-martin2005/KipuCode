package com.kipucode.data.repository

import android.util.Log
import com.kipucode.data.local.dao.LearningProgressDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.mapper.toFsrsCard
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.repository.LearningProgressRepository
import io.github.openspacedrepetition.Card
import io.github.openspacedrepetition.Rating
import io.github.openspacedrepetition.Scheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

internal class LearningProgressRepositoryImpl @Inject constructor(
    private val learningProgressDao: LearningProgressDao,
    private val userRemoteDataSource: UserRemoteDataSource
) : LearningProgressRepository {

    private val scheduler: Scheduler = Scheduler.builder()
        .desiredRetention(0.9)
        .enableFuzzing(true)
        .build()

    override fun getDueExercises(): Flow<List<LearningProgressDomain>> {
        val currentUid = userRemoteDataSource.currentUserId ?: return flowOf(emptyList())
        return learningProgressDao.getDueExercises(currentUid, System.currentTimeMillis())
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getProgressForExercise(exerciseId: String): Flow<LearningProgressDomain?> {
        val currentUid = userRemoteDataSource.currentUserId ?: return flowOf(null)
        return learningProgressDao.getProgressForExercise(currentUid, exerciseId)
            .map { it?.toDomain() }
    }

    override suspend fun recordChoiceAttempt(exerciseId: String, isCorrect: Boolean): Response<Unit> {
        val rating = if (isCorrect) Rating.GOOD else Rating.AGAIN
        return processReview(exerciseId, rating)
    }

    override suspend fun recordRatingAttempt(exerciseId: String, ratingValue: Int): Response<Unit> {
        val rating = when (ratingValue) {
            1 -> Rating.AGAIN
            2 -> Rating.HARD
            3 -> Rating.GOOD
            4 -> Rating.EASY
            else -> Rating.GOOD
        }
        return processReview(exerciseId, rating)
    }

    override suspend fun saveLearningProgress(progress: LearningProgressDomain): Response<Unit> {
        return try {
            // 1. Guardado local (Offline-First)
            learningProgressDao.insertOrUpdate(progress.toEntity())

            // 2. Respaldo en la nube (Firestore)
            val currentUid = userRemoteDataSource.currentUserId
            if (currentUid != null) {
                userRemoteDataSource.saveLearningProgress(progress.toDto())
                Response.Success(Unit)
            } else {
                Response.Error("Usuario no autenticado", ServerErrorType.CREDENTIAL_INVALID)
            }
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al guardar en Firestore", ServerErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun refreshLearningProgress(): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.CREDENTIAL_INVALID)

            // Descarga el historial de ejercicios del alumno desde Firestore
            val remoteList = userRemoteDataSource.getAllLearningProgress()
            if (remoteList.isNotEmpty()) {
                learningProgressDao.insertAll(remoteList.map { it.toEntity(currentUid) })
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al sincronizar ejercicios desde Firestore", ServerErrorType.FIRESTORE_ERROR)
        }
    }

    private suspend fun processReview(exerciseId: String, rating: Rating): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.CREDENTIAL_INVALID)

            val existingEntity = learningProgressDao.getProgressForExerciseDirect(currentUid, exerciseId)
            val currentCard = existingEntity?.toFsrsCard() ?: Card.builder().build()

            val result = scheduler.reviewCard(currentCard, rating)
            val updatedCard = result.card()

            val wasLapse = rating == Rating.AGAIN
            val updatedEntity = updatedCard.toEntity(
                exerciseId = exerciseId,
                userId = currentUid,
                prevReps = existingEntity?.reps ?: 0,
                prevLapses = existingEntity?.lapses ?: 0,
                wasLapse = wasLapse
            )

            // 1. Guardado local prioritario en SQLite
            learningProgressDao.insertOrUpdate(updatedEntity)

            // 2. Sincronización en segundo plano hacia Firestore con timeout protegido
            try {
                withTimeout(2000L.milliseconds) {
                    userRemoteDataSource.saveLearningProgress(updatedEntity.toDomain().toDto())
                }
            } catch (e: Exception) {
                Log.e("LearningProgressRepo", "Sync remoto diferido (Offline-First activo): ${e.message}")
            }

            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al procesar repaso FSRS", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}

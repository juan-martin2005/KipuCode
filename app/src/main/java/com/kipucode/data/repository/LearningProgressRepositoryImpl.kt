package com.kipucode.data.repository

import android.util.Log
import com.kipucode.data.local.dao.ExerciseDao
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

internal class LearningProgressRepositoryImpl @Inject constructor(
    private val learningProgressDao: LearningProgressDao,
    private val exerciseDao: ExerciseDao,
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



    override fun getAllProgressForCurrentUser(): Flow<List<LearningProgressDomain>> {
        val currentUid = userRemoteDataSource.currentUserId ?: return flowOf(emptyList())
        return learningProgressDao.getAllProgressForUser(currentUid)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun calculateCardRetrievability(progress: LearningProgressDomain): Double {
        if (progress.stability <= 0.001) return 0.0
        val card = progress.toEntity().toFsrsCard()
        return try {
            scheduler.getCardRetrievability(card).coerceIn(0.0, 1.0)
        } catch (e: Exception) {
            0.0
        }
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

    override suspend fun syncLearningProgressToRemote(): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.CREDENTIAL_INVALID)

            val localEntities = learningProgressDao.getAllProgressForUserDirect(currentUid)
            if (localEntities.isNotEmpty()) {
                val progressMap = localEntities.associate { it.exerciseId to it.toDomain().toDto() }
                try {
                    withTimeout(10000L.milliseconds) {
                        userRemoteDataSource.saveLearningProgressMap(progressMap)
                    }
                } catch (e: Exception) {
                    Log.e("LearningProgressRepo", "Sync remoto diferido (Offline-First activo): ${e.message}")
                }
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("LearningProgressRepo", "Error al sincronizar mapa a Firestore: ${e.message}", e)
            Response.Error(e.message ?: "Error al sincronizar progreso", ServerErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun refreshLearningProgress(): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.CREDENTIAL_INVALID)

            // Descarga el mapa consolidado de ejercicios del alumno desde Firestore en 1 sola lectura
            val remoteList = userRemoteDataSource.getAllLearningProgress()
            if (remoteList.isNotEmpty()) {
                val localExerciseIds = exerciseDao.getAllExerciseIds().toSet()
                // Solo insertamos los ejercicios que existen en el catálogo local para evitar errores de Foreign Key
                val validEntities = remoteList
                    .filter { localExerciseIds.contains(it.exerciseId) }
                    .map { it.toEntity(currentUid) }

                if (validEntities.isNotEmpty()) {
                    learningProgressDao.insertAll(validEntities)
                    Log.d("LearningProgressRepo", "Sincronizados ${validEntities.size} ejercicios desde Firestore a SQLite.")
                }
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("LearningProgressRepo", "Error al sincronizar ejercicios desde Firestore: ${e.message}", e)
            Response.Error(e.message ?: "Error al sincronizar ejercicios desde Firestore", ServerErrorType.FIRESTORE_ERROR)
        }
    }

    private suspend fun processReview(exerciseId: String, rating: Rating): Response<Unit> = withContext(NonCancellable) {
        try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return@withContext Response.Error("Usuario no autenticado", ServerErrorType.CREDENTIAL_INVALID)

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

            // 1. Guardado local prioritario instantáneo en Room SQLite (0 ms de red)
            learningProgressDao.insertOrUpdate(updatedEntity)

            Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("LearningProgressRepo", "Error al procesar repaso FSRS: ${e.message}", e)
            Response.Error(e.message ?: "Error al procesar repaso FSRS", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}

package com.kipucode.data.repository

import com.kipucode.data.local.dao.ExerciseAttemptDao
import com.kipucode.data.local.model.ExerciseAttemptEntity
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.ExerciseAttemptDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.repository.ExerciseAttemptRepository
import javax.inject.Inject

class ExerciseAttemptRepositoryImpl @Inject constructor(
    private val exerciseAttemptDao: ExerciseAttemptDao,
    private val userRemoteDataSource: UserRemoteDataSource
) : ExerciseAttemptRepository {

    override suspend fun recordAttempt(
        exerciseId: String,
        lessonId: String,
        exerciseType: String,
        isCorrect: Boolean
    ): Response<ExerciseAttemptDomain> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)

            val existing = exerciseAttemptDao.getAttempt(userId = currentUid, exerciseId = exerciseId)

            val updatedEntity = ExerciseAttemptEntity(
                exerciseId = exerciseId,
                userId = currentUid,
                lessonId = lessonId,
                exerciseType = exerciseType,
                isCompleted = existing?.isCompleted == true || isCorrect,
                attemptsCount = (existing?.attemptsCount ?: 0) + 1,
                correctCount = (existing?.correctCount ?: 0) + (if (isCorrect) 1 else 0),
                incorrectCount = (existing?.incorrectCount ?: 0) + (if (!isCorrect) 1 else 0),
                lastIsCorrect = isCorrect,
                lastAttemptAt = System.currentTimeMillis()
            )

            exerciseAttemptDao.insertOrUpdate(updatedEntity)
            Response.Success(updatedEntity.toDomain())
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al registrar intento", ServerErrorType.LOCAL_DB_ERROR)
        }
    }

    override suspend fun getCompletedExerciseIdsForLesson(lessonId: String): List<String> {
        val currentUid = userRemoteDataSource.currentUserId ?: return emptyList()
        return exerciseAttemptDao.getCompletedExerciseIdsForLesson(userId = currentUid, lessonId = lessonId)
    }

    override suspend fun getAttemptsForLesson(lessonId: String): List<ExerciseAttemptDomain> {
        val currentUid = userRemoteDataSource.currentUserId ?: return emptyList()
        return exerciseAttemptDao.getAttemptsForLesson(userId = currentUid, lessonId = lessonId).map { it.toDomain() }
    }

    override suspend fun getAllAttemptsForUser(): List<ExerciseAttemptDomain> {
        val currentUid = userRemoteDataSource.currentUserId ?: return emptyList()
        return exerciseAttemptDao.getAllAttemptsForUser(userId = currentUid).map { it.toDomain() }
    }

    override suspend fun fetchAllAttemptsFromRemote(): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)

            val remoteList = userRemoteDataSource.getAllExerciseAttempts()
            if (remoteList.isNotEmpty()) {
                val entities = remoteList.map { it.toEntity(userId = currentUid) }
                exerciseAttemptDao.insertAll(entities)
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al descargar intentos remotos", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}

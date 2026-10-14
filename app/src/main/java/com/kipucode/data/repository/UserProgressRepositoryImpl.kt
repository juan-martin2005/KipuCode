package com.kipucode.data.repository

import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.mapper.toCompletedCoursesEntities
import com.kipucode.data.mapper.toCompletedLessonsEntities
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ===============================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO USER_PROGRESS_REPOSITORY (ACCESO Y PERSISTENCIA DE DATOS PURA)
// ===============================================================================================
internal class UserProgressRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userProgressDao: UserProgressDao
): UserProgressRepository {

    override fun getUserProgress(): Flow<UserProgressDomain?> {
        val currentUid = userRemoteDataSource.currentUserId
        return if (currentUid != null) {
            userProgressDao.getUserProgressWithDetails(currentUid).map { it?.toDomain() }
        } else {
            flowOf(null)
        }
    }

    override suspend fun refreshUserProgress(): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)

            val remoteProgressDto = userRemoteDataSource.getUserProgress()
            if (remoteProgressDto != null) {
                userProgressDao.insertFullProgress(
                    remoteProgressDto.toEntity(),
                    remoteProgressDto.toCompletedLessonsEntities(),
                    remoteProgressDto.toCompletedCoursesEntities()
                )
            }
            Response.Success(Unit)
        } catch (ex: Exception) {
            Response.Error(ex.message, ServerErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun saveUserProgress(userProgress: UserProgressDomain): Response<Unit> {
        return try {
            userProgressDao.insertFullProgress(
                userProgress.toEntity(),
                userProgress.toCompletedLessonsEntities(),
                userProgress.toCompletedCoursesEntities()
            )
            Response.Success(Unit)
        } catch (ex: Exception) {
            Response.Error(ex.message, ServerErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun updateLastVisitedLessonLocal(lessonId: String): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)
            userProgressDao.updateLastVisitedLesson(currentUid, lessonId)
            Response.Success(Unit)
        } catch (ex: Exception) {
            Response.Error(ex.message, ServerErrorType.FIRESTORE_ERROR)
        }
    }
}

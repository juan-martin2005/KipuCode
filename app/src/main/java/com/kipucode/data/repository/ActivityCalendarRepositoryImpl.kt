package com.kipucode.data.repository

import com.kipucode.data.local.dao.DailyActivityDao
import com.kipucode.data.local.dao.LearningProgressDao
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.local.model.DailyActivityEntity
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.repository.ActivityCalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ActivityCalendarRepositoryImpl @Inject constructor(
    private val dailyActivityDao: DailyActivityDao,
    private val learningProgressDao: LearningProgressDao,
    private val userProgressDao: UserProgressDao,
    private val userRemoteDataSource: UserRemoteDataSource
) : ActivityCalendarRepository {

    override fun getActivityCalendarForYear(year: Int): Flow<List<DailyActivityDomain>> {
        return dailyActivityDao.getAllActivitiesForYear(year).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun recordDailyActivity(
        exercisesDelta: Int,
        correctDelta: Int,
        incorrectDelta: Int,
        xpDelta: Int,
        lessonsDelta: Int
    ): Response<DailyActivityDomain> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)

            val today = LocalDate.now()
            val todayDate = today.toString() // "YYYY-MM-DD"
            val currentYear = today.year

            val existing = dailyActivityDao.getActivityForDate(currentUid, todayDate)
            val updatedEntity = if (existing != null) {
                existing.copy(
                    exercisesCount = existing.exercisesCount + exercisesDelta,
                    correctCount = existing.correctCount + correctDelta,
                    incorrectCount = existing.incorrectCount + incorrectDelta,
                    xpEarned = existing.xpEarned + xpDelta,
                    lessonsCount = existing.lessonsCount + lessonsDelta,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                DailyActivityEntity(
                    id = "${currentUid}_$todayDate",
                    userId = currentUid,
                    date = todayDate,
                    year = currentYear,
                    timestamp = System.currentTimeMillis(),
                    exercisesCount = exercisesDelta,
                    correctCount = correctDelta,
                    incorrectCount = incorrectDelta,
                    xpEarned = xpDelta,
                    lessonsCount = lessonsDelta
                )
            }

            dailyActivityDao.insertOrUpdate(updatedEntity)
            Response.Success(updatedEntity.toDomain())
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al guardar actividad diaria", ServerErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun syncSessionBatchToRemote(todayActivity: DailyActivityDomain?): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)

            // 1. Obtener progreso de usuario local más reciente (XP, Racha, Lecciones completadas)
            val userProgressEntity = userProgressDao.getUserProgressWithDetails(currentUid).firstOrNull()
            val userProgressDto = userProgressEntity?.toDomain()?.toDto()

            // 2. Obtener lote de ejercicios FSRS locales
            val localExercises = learningProgressDao.getAllProgressForUserDirect(currentUid)
            val exercisesMap = localExercises.associate { entity ->
                entity.exerciseId to entity.toDomain().toDto()
            }

            // 3. Extraer actividad de hoy si aplica
            val todayYear = todayActivity?.year
            val todayDate = todayActivity?.date
            val todayDto = todayActivity?.toDto()

            // 4. Enviar TODO en 1 solo WriteBatch atómico a Firestore
            userRemoteDataSource.syncSessionBatch(
                userProgressDto = userProgressDto,
                exercisesMap = exercisesMap,
                todayYear = todayYear,
                todayDate = todayDate,
                todayActivity = todayDto
            )

            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error en sincronización consolidada", ServerErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun fetchYearActivityFromRemote(year: Int): Response<Unit> {
        return try {
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ServerErrorType.FIRESTORE_ERROR)

            val remoteDays = userRemoteDataSource.getYearActivity(year)
            if (remoteDays.isNotEmpty()) {
                val entities = remoteDays.map { (date, dto) ->
                    dto.toEntity(userId = currentUid, date = date, year = year)
                }
                dailyActivityDao.insertAll(entities)
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error al descargar actividad anual", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}

package com.kipucode.data.repository

import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.collections.all
import kotlin.collections.map
import kotlin.time.Duration.Companion.milliseconds

// ===============================================================================================
//  IMPLEMENTACIÓN DEL CONTRATO USER_PROGRESS_REPOSITORY
// ===============================================================================================
internal class UserProgressRepositoryImpl @Inject constructor(
    //  Instancia de UserRemoteDataSource -> Acceso a datos remotos en Firestore
    //  Instancia de UserProgressDao -> Acceso a operaciones locales del Progreso de Usuario (Room)
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userProgressDao: UserProgressDao,
): UserProgressRepository {
    //  ! IMPORTANTE
    //  Offline-First: Los flujos 'get' consumen directamente de Room (DB Local). La UI nunca
    //     experimenta retrasos de red al consultar datos ya almacenados.

    //  Mappers: Se usa '.map { ... }' de Flow para convertir las entidades de la base de datos
    //     (Entity) a modelos de negocio (Domain).

    //  SINCRONIZACIÓN: El 'refresh' descarga primero la estructura de cursos para traer las
    //     lecciones de cada uno desde Firestore.

    // ===========================================================================================
    //  Observa los datos locales del progreso de usuario
    // ===========================================================================================
    override fun getUserProgress(): Flow<UserProgressDomain?> {
        val currentUid = userRemoteDataSource.currentUserId

        return if (currentUid != null) {
            userProgressDao.getUserProgress(currentUid).map { it?.toDomain() }
        } else {
            flowOf(null)
        }
    }

    // ===========================================================================================
    //  Sincronización Remota -> Descarga de Firestore el progreso y actualiza la DB local
    // ===========================================================================================
    override suspend fun refreshUserProgress(): Response<Unit> {
        return try {
            val userProgressDto = userRemoteDataSource.getUserProgress()

            if (userProgressDto != null) {
                userProgressDao.insert(userProgressDto.toEntity())
                Response.Success(Unit)
            }else{
                Response.Error(message = "Progress not found", error = ErrorType.FIRESTORE_ERROR)
            }
        }catch (ex: Exception){
            Response.Error(message = ex.message, error = ErrorType.FIRESTORE_ERROR)
        }
    }

    // ===========================================================================================
    //  Sincronización Remota -> Actualiza el progreso del usuario actual
    // ===========================================================================================
    override suspend fun saveUserProgress(userProgress: UserProgressDomain): Response<Unit> {
        return try {
            userProgressDao.insert(userProgress.toEntity())

            val id = userRemoteDataSource.currentUserId
            if (id != null) {
                userRemoteDataSource.createUserProgress(userProgress.toDto())
                Response.Success(Unit)
            } else {
                Response.Error("User_Progress Sync Error", ErrorType.FIRESTORE_ERROR)
            }
        } catch (ex: Exception) {
            Response.Error(ex.message, ErrorType.FIRESTORE_ERROR)
        }
    }

    override suspend fun completeLesson(
        completedLessonId: String,
        xpEarned: Int,
        coursesWithLessons: List<CourseWithLessonsDomain>
    ): Response<Unit> {
        return try {
            // Obtener el Uid actual de Firebase
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ErrorType.FIRESTORE_ERROR)

            // Obtener el progreso local
            val currentProgress = userProgressDao.getUserProgress(currentUid).first()?.toDomain()
                ?: return Response.Error("No se encontró el progreso del usuario", ErrorType.FIRESTORE_ERROR)

            val previousXp = currentProgress.lessonsXpRecord[completedLessonId] ?: 0
            val xpDifference = if (xpEarned > previousXp) xpEarned - previousXp else 0

            val updatedXp = currentProgress.totalXp + xpDifference
            val updatedPoints = currentProgress.points + xpDifference
            val updatedLessonsXpRecord = currentProgress.lessonsXpRecord + (completedLessonId to maxOf(previousXp,
                xpEarned))

            val isAlreadyCompleted = currentProgress.completedLessons.contains(completedLessonId)

            val updatedLessons = if (!isAlreadyCompleted) {
                currentProgress.completedLessons + completedLessonId
            } else {
                currentProgress.completedLessons
            }


            if (coursesWithLessons.isEmpty()) {
                return Response.Error("El mapa de cursos está vacío", ErrorType.FIRESTORE_ERROR)
            }

            val allLessonsOrdered = coursesWithLessons
                .sortedBy { it.course.orderIndex }
                .flatMap { item -> item.lessons.sortedBy { it.orderIndex } }

            val currentCourseWithLessons = coursesWithLessons.find { item ->
                item.lessons.any { it.id == completedLessonId }
            } ?: return Response.Error("Lección no mapeada en ningún curso", ErrorType.FIRESTORE_ERROR)

            val currentCourse = currentCourseWithLessons.course

            val allLessonsInCurrentCourse = currentCourseWithLessons.lessons.map { it.id }
            val allCompletedInCourse = allLessonsInCurrentCourse.all { updatedLessons.contains(it) }

            val updatedCourses = if (allCompletedInCourse && !currentProgress.completedCourses.contains(currentCourse.id)) {
                currentProgress.completedCourses + currentCourse.id
            } else {
                currentProgress.completedCourses
            }

            val currentLessonIdx = allLessonsOrdered.indexOfFirst { it.id == completedLessonId }
            val nextLesson = if (currentLessonIdx != -1 && currentLessonIdx < allLessonsOrdered.size - 1) {
                allLessonsOrdered[currentLessonIdx + 1]
            } else {
                null
            }

            var newCurrentLessonId = currentProgress.currentLessonId
            var newStatus = currentProgress.status

            if (nextLesson != null) {
                val nextLessonCourse = coursesWithLessons.find { item ->
                    item.lessons.any { it.id == nextLesson.id }
                }?.course

                val calculatedNextLessonId = if (nextLessonCourse != null && nextLessonCourse.id != currentCourse.id) {
                    if (updatedXp >= nextLessonCourse.exp) {
                        nextLesson.id
                    } else {
                        completedLessonId
                    }
                } else {
                    nextLesson.id
                }

                newCurrentLessonId = if (!isAlreadyCompleted || currentProgress.currentLessonId == completedLessonId) {
                    calculatedNextLessonId
                } else {
                    currentProgress.currentLessonId
                }

            } else {
                if (!isAlreadyCompleted || currentProgress.currentLessonId == completedLessonId) {
                    newStatus = "COMPLETED"
                }
            }

            val now = System.currentTimeMillis()

            val newStreak = calculateStreak(
                lastCompletedMillis = currentProgress.completedAt,
                currentMillis = now,
                currentStreak = currentProgress.streakDay
            )

            val updatedProgress = currentProgress.copy(
                completedLessons = updatedLessons,
                completedCourses = updatedCourses,
                totalXp = updatedXp,
                points = updatedPoints,
                score = updatedPoints, // Temporalmente igual que los puntos.
                lessonsXpRecord = updatedLessonsXpRecord,
                streakDay = newStreak,
                currentLessonId = newCurrentLessonId,
                status = newStatus,
                completedAt = now
            )

            userProgressDao.insert(updatedProgress.toEntity())
            try {
                withTimeout(2000L.milliseconds) {
                    userRemoteDataSource.createUserProgress(updatedProgress.toDto())
                }
            } catch (e: Exception) {
            }

            Response.Success(Unit)

        } catch (e: Exception) {
            Response.Error("${e.localizedMessage}", ErrorType.FIRESTORE_ERROR)
        }
    }

    private fun calculateStreak(lastCompletedMillis: Long?, currentMillis: Long, currentStreak: Int): Int {
        if (lastCompletedMillis == null) return 1 // Primera lección completada, empieza en 1

        // Convertir los timestamps en milisegundos a fechas locales (LocalDate) del dispositivo (ZoneId)
        val lastDate = Instant.ofEpochMilli(lastCompletedMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val todayDate = Instant.ofEpochMilli(currentMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        // Calcular la diferencia exacta en días calendarios
        val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)

        return when (daysBetween) {
            0L -> currentStreak       // Completo Hoy -> La racha se mantiene
            1L -> currentStreak + 1   // Completó Ayer -> la racha aumenta
            else -> 1                                // Se rompió la racha (más de 1 día) == 1
        }
    }
}

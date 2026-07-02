package com.kipucode.data.repository

import android.util.Log
import com.kipucode.data.local.dao.UserProgressDao
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.mapper.toEntity
import com.kipucode.data.remote.firebase.service.UserRemoteDataSource
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
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
            val currentUid = userRemoteDataSource.currentUserId
                ?: return Response.Error("Usuario no autenticado", ErrorType.FIRESTORE_ERROR)

            val currentProgress = userProgressDao.getUserProgress(currentUid).first()?.toDomain()
                ?: return Response.Error("No se encontró el progreso del usuario", ErrorType.FIRESTORE_ERROR)

            val currentCourseWithLessons = coursesWithLessons.find { c -> c.lessons.any { it.id == completedLessonId } }
                ?: return Response.Error("Lección no mapeada en ningún curso", ErrorType.FIRESTORE_ERROR)

            // Calcular XP y Puntos
            val previousXp = currentProgress.lessonsXpRecord[completedLessonId] ?: 0
            val xpDifference = maxOf(0, xpEarned - previousXp)
            val updatedXp = currentProgress.totalXp + xpDifference
            val updatedPoints = currentProgress.points + xpDifference
            val updatedLessonsXpRecord = currentProgress.lessonsXpRecord +
                    (completedLessonId to maxOf(previousXp, xpEarned))

            // Actualizar Lecciones y Cursos completados
            val isAlreadyCompleted = currentProgress.completedLessons.contains(completedLessonId)
            val updatedLessons = if (isAlreadyCompleted) currentProgress.completedLessons else currentProgress.completedLessons + completedLessonId

            val isCourseCompleted = currentCourseWithLessons.lessons.all { updatedLessons.contains(it.id) }
            val updatedCourses = if (isCourseCompleted && !currentProgress.completedCourses.contains(currentCourseWithLessons.course.id)) {
                currentProgress.completedCourses + currentCourseWithLessons.course.id
            } else currentProgress.completedCourses

            // Determinar siguiente lección y estado
            val (nextLessonId, newStatus) = determineNextStep(
                currentProgress = currentProgress,
                coursesWithLessons = coursesWithLessons,
                currentCourseWithLessons = currentCourseWithLessons,
                completedLessonId = completedLessonId,
                updatedXp = updatedXp
            )

            // Calcular Racha
            val now = System.currentTimeMillis()
            val newStreak = calculateStreak(currentProgress.completedAt, now, currentProgress.streakDay)

            // Construir y guardar el nuevo estado
            val updatedProgress = currentProgress.copy(
                completedLessons = updatedLessons,
                completedCourses = updatedCourses,
                totalXp = updatedXp,
                points = updatedPoints,
                score = updatedPoints,
                lessonsXpRecord = updatedLessonsXpRecord,
                streakDay = newStreak,
                currentLessonId = nextLessonId,
                status = newStatus,
                completedAt = now
            )

            persistProgress(updatedProgress)
            Response.Success(Unit)

        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Error desconocido", ErrorType.FIRESTORE_ERROR)
        }
    }

    // ===========================================================================================
    //  Funciones Privadas
    // ===========================================================================================
    private fun determineNextStep(
        currentProgress: UserProgressDomain,
        coursesWithLessons: List<CourseWithLessonsDomain>,
        currentCourseWithLessons: CourseWithLessonsDomain,
        completedLessonId: String,
        updatedXp: Int
    ): Pair<String, String> {
        // Si ya había completado esta lección y NO es la lección actual -> no avanzamos
        val shouldAdvance = !currentProgress.completedLessons
            .contains(completedLessonId) || currentProgress.currentLessonId == completedLessonId

        if (!shouldAdvance) return currentProgress.currentLessonId to currentProgress.status

        val courseIdx = coursesWithLessons.indexOfFirst { it.course.id == currentCourseWithLessons.course.id }
        val lessonIdx = currentCourseWithLessons.lessons.indexOfFirst { it.id == completedLessonId }

        // Hay una siguiente lección en el MISMO curso
        if (lessonIdx != -1 && lessonIdx < currentCourseWithLessons.lessons.size - 1) {
            return currentCourseWithLessons.lessons[lessonIdx + 1].id to currentProgress.status
        }

        // Es la última lección del curso. Buscamos el SIGUIENTE curso
        if (courseIdx != -1 && courseIdx < coursesWithLessons.size - 1) {
            val nextCourse = coursesWithLessons[courseIdx + 1]

            // Validar si tiene la experiencia necesaria para el siguiente curso
            return if (updatedXp >= nextCourse.course.exp) {
                val nextLessonId = nextCourse.lessons.firstOrNull()?.id ?: completedLessonId
                nextLessonId to currentProgress.status
            } else {
                completedLessonId to currentProgress.status // Se queda estancado por falta de XP
            }
        }

        // No hay más cursos ni lecciones
        return currentProgress.currentLessonId to "COMPLETED"
    }

    private suspend fun persistProgress(updatedProgress: UserProgressDomain) {
        userProgressDao.insert(updatedProgress.toEntity())
        try {
            withTimeout(2000L.milliseconds) {
                userRemoteDataSource.createUserProgress(updatedProgress.toDto())
            }
        } catch (e: Exception) {
            Log.e("UserProgressRepository", "Error en sync remoto (Offline-first activo): ${e.message}")
        }
    }

    private fun calculateStreak(lastCompletedMillis: Long?, currentMillis: Long, currentStreak: Int): Int {
        if (lastCompletedMillis == null) return 1

        val lastDate = Instant.ofEpochMilli(lastCompletedMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val todayDate = Instant.ofEpochMilli(currentMillis).atZone(ZoneId.systemDefault()).toLocalDate()

        return when (ChronoUnit.DAYS.between(lastDate, todayDate)) {
            0L -> currentStreak       // Mismo día
            1L -> currentStreak + 1   // Día siguiente
            else -> 1                 // Racha rota
        }
    }
}

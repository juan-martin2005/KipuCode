package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

// ============================================================================================
//  CASOS DE USO: PROGRESO GLOBAL DEL USUARIO
// ============================================================================================

// ============================================================================================
//  CASO DE USO: OBTENER PROGRESO DEL USUARIO
// ============================================================================================
class GetUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    operator fun invoke(): Flow<UserProgressDomain?> {
        return userProgressRepository.getUserProgress()
    }
}

// ============================================================================================
//  CASO DE USO: REFRESCAR PROGRESO DEL USUARIO
// ============================================================================================
class RefreshUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(): Response<Unit> {
        return userProgressRepository.refreshUserProgress()
    }
}

// ============================================================================================
//  CASO DE USO: CÁLCULO DE EXPERIENCIA (XP) INCREMENTAL
// ============================================================================================
data class LessonXpUpdateResult(
    val updatedTotalXp: Int,
    val updatedLessonsXpRecord: Map<String, Int>,
    val xpDifference: Int
)

class CalculateLessonXpUseCase @Inject constructor() {
    operator fun invoke(
        currentTotalXp: Int,
        lessonsXpRecord: Map<String, Int>,
        lessonId: String,
        earnedXp: Int
    ): LessonXpUpdateResult {
        val previousXp = lessonsXpRecord[lessonId] ?: 0
        val xpDifference = maxOf(0, earnedXp - previousXp)
        val updatedTotalXp = currentTotalXp + xpDifference
        val updatedRecord = lessonsXpRecord + (lessonId to maxOf(previousXp, earnedXp))

        return LessonXpUpdateResult(
            updatedTotalXp = updatedTotalXp,
            updatedLessonsXpRecord = updatedRecord,
            xpDifference = xpDifference
        )
    }
}

// ============================================================================================
//  CASO DE USO: CÁLCULO Y EVALUACIÓN DE RACHA DIARIA (STREAK)
// ============================================================================================
class CalculateStreakUseCase @Inject constructor() {

    /**
     * Calcula la nueva racha tras completar una actividad educativa.
     */
    fun calculateOnCompletion(
        lastCompletedMillis: Long?,
        currentMillis: Long = System.currentTimeMillis(),
        currentStreak: Int
    ): Int {
        if (lastCompletedMillis == null) return 1

        val lastDate = Instant.ofEpochMilli(lastCompletedMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val todayDate = Instant.ofEpochMilli(currentMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)

        return when {
            daysBetween <= 0L -> maxOf(1, currentStreak)
            daysBetween == 1L -> currentStreak + 1
            else -> 1
        }
    }

    /**
     * Calcula la racha activa visible para la interfaz de usuario en tiempo real.
     * Si han transcurrido más de 1 día calendario sin actividad, la racha activa expira (0).
     */
    fun calculateActiveStreak(
        lastCompletedMillis: Long?,
        databaseStreak: Int,
        currentMillis: Long = System.currentTimeMillis()
    ): Int {
        if (lastCompletedMillis == null) return 0

        val lastDate = Instant.ofEpochMilli(lastCompletedMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val todayDate = Instant.ofEpochMilli(currentMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)

        return if (daysBetween > 1L) 0 else databaseStreak
    }
}

// ============================================================================================
//  CASO DE USO: DETERMINAR SIGUIENTE LECCIÓN
// ============================================================================================
class DetermineNextLessonUseCase @Inject constructor() {
    operator fun invoke(
        currentProgress: UserProgressDomain,
        coursesWithLessons: List<CourseWithLessonsDomain>,
        justCompletedLessonId: String
    ): String {
        val targetLessonId = if (justCompletedLessonId.isNotEmpty()) justCompletedLessonId else currentProgress.lastVisitedLessonId.orEmpty()

        val activeCourse = coursesWithLessons.find { c -> c.lessons.any { it.id == targetLessonId } }
            ?: return targetLessonId

        val lessonIdx = activeCourse.lessons.indexOfFirst { it.id == targetLessonId }

        // 1. Hay una siguiente lección en el mismo curso
        if (lessonIdx != -1 && lessonIdx < activeCourse.lessons.size - 1) {
            return activeCourse.lessons[lessonIdx + 1].id
        }

        // 2. Última lección del curso -> buscar el siguiente curso del mismo track
        val activeTrack = activeCourse.course.track.ifEmpty { currentProgress.activeTrack }
        val trackCourses = coursesWithLessons.filter { it.course.track == activeTrack }.sortedBy { it.course.orderIndex }
        val courseIdx = trackCourses.indexOfFirst { it.course.id == activeCourse.course.id }

        if (courseIdx != -1 && courseIdx < trackCourses.size - 1) {
            val nextCourse = trackCourses[courseIdx + 1]
            return nextCourse.lessons.firstOrNull()?.id ?: targetLessonId
        }

        return targetLessonId
    }
}

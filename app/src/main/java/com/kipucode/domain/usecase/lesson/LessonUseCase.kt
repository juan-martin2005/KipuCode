package com.kipucode.domain.usecase.lesson

import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.LessonRepository
import com.kipucode.domain.repository.UserProgressRepository
import com.kipucode.domain.usecase.user.CalculateLessonXpUseCase
import com.kipucode.domain.usecase.user.CalculateStreakUseCase
import com.kipucode.domain.usecase.user.DetermineNextLessonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetLessonByCourseUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
){
    operator fun invoke(lessonId: String): Flow<LessonDomain?> {
        return lessonRepository.getLessonById(lessonId)
    }
}

// ============================================================================================
//  CASO DE USO: ORQUESTADOR AL COMPLETAR UNA LECCIÓN
// ============================================================================================
class CompleteLessonUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository,
    private val calculateLessonXpUseCase: CalculateLessonXpUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val determineNextLessonUseCase: DetermineNextLessonUseCase
) {
    suspend operator fun invoke(completedLessonId: String, xpEarned: Int): Response<Int> {
        return try {
            val currentProgress = userProgressRepository.getUserProgress().first()
                ?: return Response.Error("No se encontró el progreso del usuario", ServerErrorType.FIRESTORE_ERROR)

            val now = System.currentTimeMillis()
            val newStreak = calculateStreakUseCase.calculateOnCompletion(
                lastCompletedMillis = currentProgress.completedAt,
                currentMillis = now,
                currentStreak = currentProgress.streakDay
            )

            val (updatedProgress, effectiveXp) = if (completedLessonId.isNotEmpty()) {
                val coursesWithLessons = courseRepository.getCourseWithLessons().first()
                val currentCourseWithLessons = coursesWithLessons.find { c -> c.lessons.any { it.id == completedLessonId } }
                    ?: return Response.Error("Lección no mapeada en ningún curso", ServerErrorType.FIRESTORE_ERROR)

                // 1. Calcular XP incremental delegando al caso de uso del dominio
                val xpResult = calculateLessonXpUseCase(
                    currentTotalXp = currentProgress.totalXp,
                    lessonsXpRecord = currentProgress.lessonsXpRecord,
                    lessonId = completedLessonId,
                    earnedXp = xpEarned
                )

                // 2. Actualizar Lecciones y Cursos completados
                val isAlreadyCompleted = currentProgress.completedLessons.contains(completedLessonId)
                val updatedLessons = if (isAlreadyCompleted) {
                    currentProgress.completedLessons
                } else {
                    currentProgress.completedLessons + completedLessonId
                }

                val isCourseCompleted = currentCourseWithLessons.lessons.all { updatedLessons.contains(it.id) }
                val updatedCourses = if (isCourseCompleted && !currentProgress.completedCourses.contains(currentCourseWithLessons.course.id)) {
                    currentProgress.completedCourses + currentCourseWithLessons.course.id
                } else {
                    currentProgress.completedCourses
                }

                // 3. Determinar siguiente lección delegando al caso de uso del dominio
                val nextLessonId = determineNextLessonUseCase(
                    currentProgress = currentProgress,
                    coursesWithLessons = coursesWithLessons,
                    justCompletedLessonId = completedLessonId
                )

                val courseTrack = currentCourseWithLessons.course.track

                Pair(
                    currentProgress.copy(
                        completedLessons = updatedLessons,
                        completedCourses = updatedCourses,
                        totalXp = xpResult.updatedTotalXp,
                        lessonsXpRecord = xpResult.updatedLessonsXpRecord,
                        streakDay = newStreak,
                        activeTrack = courseTrack.ifEmpty { currentProgress.activeTrack },
                        lastVisitedLessonId = nextLessonId,
                        completedAt = now
                    ),
                    xpResult.xpDifference
                )
            } else {
                // Práctica libre (ejercicios sueltos de código, etc.)
                Pair(
                    currentProgress.copy(
                        totalXp = currentProgress.totalXp + xpEarned,
                        streakDay = newStreak,
                        completedAt = now
                    ),
                    xpEarned
                )
            }

            // Persistir en el repositorio de progreso
            val saveResult = userProgressRepository.saveUserProgress(updatedProgress)
            if (saveResult is Response.Success) {
                Response.Success(effectiveXp)
            } else {
                Response.Error((saveResult as Response.Error).message, saveResult.error)
            }

        } catch (e: Exception) {
            Response.Error("${e.localizedMessage}", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}

// ============================================================================================
//  CASO DE USO: ACTUALIZAR ÚLTIMA LECCIÓN VISITADA (MARCAPÁGINAS)
// ============================================================================================
class UpdateLastViewedLessonUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(lessonId: String) {
        try {
            userProgressRepository.updateLastVisitedLessonLocal(lessonId)
        } catch (_: Exception) {}
    }
}

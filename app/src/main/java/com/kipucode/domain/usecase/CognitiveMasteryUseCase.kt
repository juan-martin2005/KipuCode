package com.kipucode.domain.usecase

import com.kipucode.domain.model.CognitiveMasteryDomain
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.ExerciseRepository
import com.kipucode.domain.repository.LearningProgressRepository
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class CognitiveMasteryOverview(
    val courseMastery: Map<String, CognitiveMasteryDomain> = emptyMap(),
    val lessonMastery: Map<String, CognitiveMasteryDomain> = emptyMap()
)

// ============================================================================================
//  CASO DE USO: OBTENER MAPA COMPLETO DE DOMINIO COGNITIVO FSRS-6 (CURSOS Y LECCIONES)
// ============================================================================================
class GetCognitiveMasteryOverviewUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val exerciseRepository: ExerciseRepository,
    private val learningProgressRepository: LearningProgressRepository
) {
    companion object {
        const val TARGET_STABILITY_DAYS = 21.0
    }

    operator fun invoke(): Flow<CognitiveMasteryOverview> {
        return combine(
            courseRepository.getCourseWithLessons(),
            exerciseRepository.getAllExercises(),
            learningProgressRepository.getAllProgressForCurrentUser()
        ) { coursesWithLessons, allExercises, allProgress ->
            val progressByExercise = allProgress.associateBy { it.exerciseId }
            val exercisesByLesson = allExercises.groupBy { it.lessonId }

            val lessonMap = mutableMapOf<String, CognitiveMasteryDomain>()
            val courseMap = mutableMapOf<String, CognitiveMasteryDomain>()

            coursesWithLessons.forEach { courseWithLessons ->
                val courseId = courseWithLessons.course.id
                var courseMasterySum = 0.0
                var courseLessonsCount = 0

                courseWithLessons.lessons.forEach { lesson ->
                    val allLessonExercises = exercisesByLesson[lesson.id] ?: emptyList()
                    val flashcards = allLessonExercises.filter {
                        it.type.equals("DEFAULT_FLASHCARDS", ignoreCase = true) || it.type.equals("FLASHCARD", ignoreCase = true)
                    }
                    if (flashcards.isEmpty()) {
                        lessonMap[lesson.id] = CognitiveMasteryDomain.fromPercentage(0, 0, 0)
                    } else {
                        var lessonMasterySum = 0.0
                        var practicedCount = 0

                        flashcards.forEach { ex ->
                            val progress = progressByExercise[ex.id]
                            if (progress != null && progress.reps > 0) {
                                practicedCount++
                                val strength = (progress.stability / TARGET_STABILITY_DAYS).coerceIn(0.0, 1.0)
                                val retrievability = learningProgressRepository.calculateCardRetrievability(progress)
                                lessonMasterySum += (strength * retrievability)
                            }
                        }

                        val lessonPct = if (practicedCount > 0) {
                            Math.round((lessonMasterySum / practicedCount) * 100).toInt().coerceIn(1, 100)
                        } else {
                            0
                        }
                        lessonMap[lesson.id] = CognitiveMasteryDomain.fromPercentage(
                            percentage = lessonPct,
                            totalExercises = flashcards.size,
                            practicedExercises = practicedCount
                        )
                        courseMasterySum += lessonPct
                        courseLessonsCount++
                    }
                }

                val coursePct = if (courseLessonsCount > 0) Math.round(courseMasterySum / courseLessonsCount).toInt() else 0
                courseMap[courseId] = CognitiveMasteryDomain.fromPercentage(coursePct)
            }

            CognitiveMasteryOverview(courseMastery = courseMap, lessonMastery = lessonMap)
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

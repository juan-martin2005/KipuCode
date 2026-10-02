package com.kipucode.domain.usecase

import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.CourseRepository
import com.kipucode.domain.repository.ExerciseRepository
import com.kipucode.domain.repository.LearningProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// ============================================================================================
//  CASOS DE USO ENCAPSULADOS EN LEARNING_PROGRESS_REPOSITORY (FSRS-6)
// ============================================================================================

// ============================================================================================
//  CASO DE USO: REGISTRAR INTENTO EN EJERCICIO (Opción múltiple -> GOOD si acierta, AGAIN si falla)
// ============================================================================================
class RecordExerciseAttemptUseCase @Inject constructor(
    private val learningProgressRepository: LearningProgressRepository
) {
    suspend operator fun invoke(exerciseId: String, isCorrect: Boolean): Response<Unit> {
        return learningProgressRepository.recordChoiceAttempt(exerciseId, isCorrect)
    }
}

// ============================================================================================
//  CASO DE USO: REGISTRAR CALIFICACIÓN MANUAL (Flashcards / Autoevaluación 1:Again, 2:Hard, 3:Good, 4:Easy)
// ============================================================================================
class RecordRatingAttemptUseCase @Inject constructor(
    private val learningProgressRepository: LearningProgressRepository
) {
    suspend operator fun invoke(exerciseId: String, ratingValue: Int): Response<Unit> {
        return learningProgressRepository.recordRatingAttempt(exerciseId, ratingValue)
    }
}

// ============================================================================================
//  CASO DE USO: OBTENER EJERCICIOS PENDIENTES DE REPASO HOY (FSRS dueDate <= System.currentTimeMillis())
// ============================================================================================
class GetDueExercisesUseCase @Inject constructor(
    private val learningProgressRepository: LearningProgressRepository
) {
    operator fun invoke(): Flow<List<LearningProgressDomain>> {
        return learningProgressRepository.getDueExercises()
    }
}

// ============================================================================================
//  CASO DE USO: REFRESCAR / SINCRONIZAR HISTORIAL DE EJERCICIOS DESDE FIRESTORE
// ============================================================================================
class RefreshLearningProgressUseCase @Inject constructor(
    private val learningProgressRepository: LearningProgressRepository
) {
    suspend operator fun invoke(): Response<Unit> {
        return learningProgressRepository.refreshLearningProgress()
    }
}

// ============================================================================================
//  CASO DE USO: OBTENER EJERCICIOS VENCIDOS CON METADATOS DE LECCIÓN Y FSRS
// ============================================================================================
class GetDueExercisesWithDetailsUseCase @Inject constructor(
    private val learningProgressRepository: LearningProgressRepository,
    private val exerciseRepository: ExerciseRepository,
    private val courseRepository: CourseRepository
) {
    operator fun invoke(): Flow<List<DueExerciseDomain>> {
        return combine(
            learningProgressRepository.getDueExercises(),
            exerciseRepository.getAllExercises(),
            courseRepository.getCourseWithLessons()
        ) { dueProgressList, allExercises, coursesWithLessons ->
            val exerciseMap = allExercises.associateBy { it.id }
            val lessonMap = coursesWithLessons
                .flatMap { it.lessons }
                .associateBy { it.id }

            dueProgressList.mapNotNull { progress ->
                val exercise = exerciseMap[progress.exerciseId] ?: return@mapNotNull null
                val lesson = lessonMap[exercise.lessonId]

                val retrievability = learningProgressRepository.calculateCardRetrievability(progress)
                val retentionPct = if (retrievability > 0.0) {
                    Math.round(retrievability * 100).toInt().coerceIn(1, 100)
                } else {
                    if (progress.reps > 0) 50 else 0
                }

                DueExerciseDomain(
                    exerciseId = progress.exerciseId,
                    lessonId = exercise.lessonId,
                    lessonTitle = lesson?.title ?: "Lección",
                    exerciseType = exercise.type,
                    dueDate = progress.dueDate,
                    retentionPercentage = retentionPct,
                    stability = progress.stability
                )
            }
        }
    }
}

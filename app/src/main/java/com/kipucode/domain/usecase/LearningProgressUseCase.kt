package com.kipucode.domain.usecase

import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.LearningProgressRepository
import kotlinx.coroutines.flow.Flow
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

package com.kipucode.domain.usecase.exercise

import com.kipucode.domain.model.ExerciseAttemptDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ExerciseAttemptRepository
import com.kipucode.domain.repository.ExerciseRepository
import com.kipucode.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ============================================================================================
//  CASOS DE USO: EJERCICIOS Y MODALIDADES DE PRÁCTICA
// ============================================================================================

// Obtener ejercicios locales por lección
class GetExercisesByLessonUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(lessonId: String): Flow<List<ExerciseDomain>> {
        return exerciseRepository.getExercisesByLessonId(lessonId)
    }
}

// Obtener el pool de ejercicios de todas las lecciones completadas por el usuario
class GetExercisesForPracticePoolUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val userProgressRepository: UserProgressRepository
) {
    operator fun invoke(): Flow<List<ExerciseDomain>> {
        return combine(
            userProgressRepository.getUserProgress(),
            exerciseRepository.getAllExercises()
        ) { progress, allExercises ->
            val completed = progress?.completedLessons?.toSet() ?: emptySet()
            if (completed.isEmpty()) emptyList()
            else allExercises.filter { it.lessonId in completed }
        }
    }
}

// Disponibilidad de ejercicios para la pantalla de entrenamiento de código
data class CodePracticeAvailabilityDomain(
    val completeCodeCount: Int = 0,
    val whatsOutputCount: Int = 0
)

class GetCodePracticeAvailabilityUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val userProgressRepository: UserProgressRepository
) {
    operator fun invoke(): Flow<CodePracticeAvailabilityDomain> {
        return combine(
            userProgressRepository.getUserProgress(),
            exerciseRepository.getAllExercises()
        ) { progress, allExercises ->
            val completed = progress?.completedLessons?.toSet() ?: emptySet()
            if (completed.isEmpty()) {
                CodePracticeAvailabilityDomain(0, 0)
            } else {
                val available = allExercises.filter { it.lessonId in completed }
                CodePracticeAvailabilityDomain(
                    completeCodeCount = available.count { it.type == "COMPLETE_CODE" },
                    whatsOutputCount = available.count { it.type == "WHATS_OUTPUT" }
                )
            }
        }
    }
}

// Determina los métodos de práctica pedagógicos disponibles para una lección
class DetermineAvailablePracticeMethodsUseCase @Inject constructor() {
    operator fun invoke(exerciseTypes: Collection<String>): List<PracticeMethod> {
        return exerciseTypes.mapNotNull { type ->
            when (type) {
                "UNIQUE_CHOICE" -> PracticeMethod.UNIQUE_CHOICE
                "DEFAULT_FLASHCARDS", "FLASHCARD" -> PracticeMethod.DEFAULT_FLASHCARDS
                "FEYNMAN_FLASHCARDS" -> PracticeMethod.FEYNMAN_FLASHCARDS
                else -> null
            }
        }.distinct()
    }
}

// ============================================================================================
//  ALGORITMO DE SELECCIÓN Y PRIORIZACIÓN DE SESIÓN (FSRS + ATTEMPTS + SHUFFLE + LÍMITE DE 5)
// ============================================================================================
class PrioritizeExerciseSessionUseCase @Inject constructor() {
    companion object {
        const val DEFAULT_SESSION_LIMIT = 5
    }

    operator fun invoke(
        exercises: List<ExerciseDomain>,
        type: String? = null,
        onlyDue: Boolean = false,
        learningProgressList: List<LearningProgressDomain> = emptyList(),
        completedExerciseIds: Set<String> = emptySet(),
        limit: Int = DEFAULT_SESSION_LIMIT,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): List<ExerciseDomain> {
        val typeFiltered = if (type != null) {
            when (type) {
                "DEFAULT_FLASHCARDS" -> exercises.filter { it.type == "DEFAULT_FLASHCARDS" || it.type == "FLASHCARD" }
                else -> exercises.filter { it.type == type }
            }
        } else {
            exercises
        }

        val isFlashcard = type in setOf("DEFAULT_FLASHCARDS", "FEYNMAN_FLASHCARDS", "FLASHCARD") ||
                (type == null && typeFiltered.isNotEmpty() && typeFiltered.all { it.type in setOf("DEFAULT_FLASHCARDS", "FEYNMAN_FLASHCARDS", "FLASHCARD") }) ||
                onlyDue

        val selectedList = if (isFlashcard) {
            val progressMap = learningProgressList.associateBy { it.exerciseId }
            if (onlyDue) {
                // Modo repaso: solo vencidas (dueDate <= currentTimeMillis y reps > 0)
                typeFiltered.filter { ex ->
                    val p = progressMap[ex.id]
                    p != null && p.reps > 0 && p.dueDate <= currentTimeMillis
                }.sortedBy { progressMap[it.id]?.dueDate ?: 0L }
            } else {
                // Modo normal de flashcards con priorización FSRS-6:
                // 1. Vencidas (dueDate <= currentTimeMillis y reps > 0)
                val dueCards = typeFiltered.filter { ex ->
                    val p = progressMap[ex.id]
                    p != null && p.reps > 0 && p.dueDate <= currentTimeMillis
                }.sortedBy { progressMap[it.id]?.dueDate ?: 0L }

                // 2. Nuevas (sin registrar en FSRS o reps == 0)
                val newCards = typeFiltered.filter { ex ->
                    val p = progressMap[ex.id]
                    p == null || p.reps == 0
                }.sortedBy { it.orderIndex }

                // 3. Próximas / Al día (dueDate > currentTimeMillis) ordenadas por la más próxima a vencer
                val upcomingCards = typeFiltered.filter { ex ->
                    val p = progressMap[ex.id]
                    p != null && p.reps > 0 && p.dueDate > currentTimeMillis
                }.sortedBy { progressMap[it.id]?.dueDate ?: Long.MAX_VALUE }

                dueCards + newCards + upcomingCards
            }
        } else {
            // Modo ejercicios de código / no-flashcard (UNIQUE_CHOICE, COMPLETE_CODE, WHATS_OUTPUT, etc.)
            // Priorizar ejercicios no completados o fallados según exercise_attempts
            val uncompleted = typeFiltered.filter { it.id !in completedExerciseIds }.shuffled()
            val completed = typeFiltered.filter { it.id in completedExerciseIds }.shuffled()

            uncompleted + completed
        }

        return selectedList
            .take(limit)
            .map { exercise ->
                exercise.copy(options = exercise.options.shuffled())
            }
    }
}

// ============================================================================================
//  CASOS DE USO: INTENTOS DE EJERCICIOS (DESACOPLAMIENTO DE VIEWMODEL)
// ============================================================================================
class RecordExerciseAttemptUseCase @Inject constructor(
    private val exerciseAttemptRepository: ExerciseAttemptRepository
) {
    suspend operator fun invoke(
        exerciseId: String,
        lessonId: String,
        exerciseType: String,
        isCorrect: Boolean
    ): Response<ExerciseAttemptDomain> {
        return exerciseAttemptRepository.recordAttempt(exerciseId, lessonId, exerciseType, isCorrect)
    }
}

class GetCompletedExerciseIdsUseCase @Inject constructor(
    private val exerciseAttemptRepository: ExerciseAttemptRepository
) {
    suspend operator fun invoke(lessonId: String): List<String> {
        return exerciseAttemptRepository.getCompletedExerciseIdsForLesson(lessonId)
    }
}

class GetAllExerciseAttemptsUseCase @Inject constructor(
    private val exerciseAttemptRepository: ExerciseAttemptRepository
) {
    suspend operator fun invoke(): List<ExerciseAttemptDomain> {
        return exerciseAttemptRepository.getAllAttemptsForUser()
    }
}

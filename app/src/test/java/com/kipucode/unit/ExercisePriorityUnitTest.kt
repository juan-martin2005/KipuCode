package com.kipucode.unit

import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LearningProgressDomain
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.domain.usecase.exercise.DetermineAvailablePracticeMethodsUseCase
import com.kipucode.domain.usecase.exercise.PrioritizeExerciseSessionUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

sealed class ExerciseSessionTestCase(
    val scenario: String
) {
    class PracticeMethodsCase(
        val exerciseTypes: List<String>,
        val expectedMethods: List<PracticeMethod>,
        scenario: String
    ) : ExerciseSessionTestCase(scenario) {
        override fun toString(): String = "methods: '${exerciseTypes.size} tipos' -> expected: ${expectedMethods.size} ($scenario)"
    }

    class CodePrioritizationCase(
        val exercises: List<ExerciseDomain>,
        val type: String?,
        val completedIds: Set<String>,
        val limit: Int,
        val expectedSize: Int,
        val expectedExcludedIds: Set<String>,
        scenario: String
    ) : ExerciseSessionTestCase(scenario) {
        override fun toString(): String = "codePriority: '$type' -> expectedSize: $expectedSize ($scenario)"
    }

    class FlashcardOrderCase(
        val exercises: List<ExerciseDomain>,
        val type: String?,
        val onlyDue: Boolean = false,
        val progressList: List<LearningProgressDomain>,
        val currentTimeMillis: Long,
        val limit: Int = PrioritizeExerciseSessionUseCase.DEFAULT_SESSION_LIMIT,
        val expectedOrderedIds: List<String>,
        scenario: String
    ) : ExerciseSessionTestCase(scenario) {
        override fun toString(): String = "flashcards: 'onlyDue=$onlyDue' -> expectedFirst: '${expectedOrderedIds.firstOrNull() ?: "vacío"}' ($scenario)"
    }
}

@RunWith(Parameterized::class)
class ExercisePriorityUnitTest(
    private val testCase: ExerciseSessionTestCase
) {

    private lateinit var determineAvailablePracticeMethodsUseCase: DetermineAvailablePracticeMethodsUseCase
    private lateinit var prioritizeExerciseSessionUseCase: PrioritizeExerciseSessionUseCase

    @Before
    fun setUp() {
        determineAvailablePracticeMethodsUseCase = DetermineAvailablePracticeMethodsUseCase()
        prioritizeExerciseSessionUseCase = PrioritizeExerciseSessionUseCase()
    }

    companion object {
        private const val NOW_MILLIS = 100_000L

        private val CODE_EXERCISES_POOL = (1..10).map { i ->
            ExerciseDomain(
                id = "ex_$i",
                lessonId = "lesson_1",
                type = "COMPLETE_CODE",
                instruction = "Completa el bloque $i",
                orderIndex = i,
                options = listOf(
                    BlockOptionDomain(
                        id = "opt_${i}_a",
                        exerciseId = "ex_$i",
                        content = "Console.WriteLine($i);",
                        isCorrect = true
                    ),
                    BlockOptionDomain(
                        id = "opt_${i}_b",
                        exerciseId = "ex_$i",
                        content = "print($i)",
                        isCorrect = false
                    )
                )
            )
        }

        private val CARD_DUE_OLDER = ExerciseDomain(id = "card_due_old", lessonId = "l1", type = "DEFAULT_FLASHCARDS", instruction = "Q1", orderIndex = 1)
        private val CARD_DUE_RECENT = ExerciseDomain(id = "card_due_recent", lessonId = "l1", type = "FLASHCARD", instruction = "Q2", orderIndex = 2)
        private val CARD_NEW = ExerciseDomain(id = "card_new", lessonId = "l1", type = "DEFAULT_FLASHCARDS", instruction = "Q3", orderIndex = 3)
        private val CARD_UPCOMING = ExerciseDomain(id = "card_upcoming", lessonId = "l1", type = "DEFAULT_FLASHCARDS", instruction = "Q4", orderIndex = 4)
        private val CARD_NEW_2 = ExerciseDomain(id = "card_new_2", lessonId = "l1", type = "DEFAULT_FLASHCARDS", instruction = "Q5", orderIndex = 5)

        private val FLASHCARDS_POOL = listOf(CARD_UPCOMING, CARD_NEW_2, CARD_NEW, CARD_DUE_RECENT, CARD_DUE_OLDER)

        private val FSRS_PROGRESS_LIST = listOf(
            LearningProgressDomain(exerciseId = "card_due_old", userId = "u1", dueDate = NOW_MILLIS - 10_000L, reps = 4),
            LearningProgressDomain(exerciseId = "card_due_recent", userId = "u1", dueDate = NOW_MILLIS - 2_000L, reps = 2),
            LearningProgressDomain(exerciseId = "card_new", userId = "u1", dueDate = 0L, reps = 0),
            LearningProgressDomain(exerciseId = "card_upcoming", userId = "u1", dueDate = NOW_MILLIS + 50_000L, reps = 3)
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<ExerciseSessionTestCase> {
            return listOf(
                // ====================================================================================
                // --- (PU-09) - DETERMINACIÓN DE MÉTODOS DE PRÁCTICA DISPONIBLES ---
                // ====================================================================================
                ExerciseSessionTestCase.PracticeMethodsCase(
                    exerciseTypes = emptyList(),
                    expectedMethods = emptyList(),
                    scenario = "Lista vacía de tipos"
                ),
                ExerciseSessionTestCase.PracticeMethodsCase(
                    exerciseTypes = listOf("UNIQUE_CHOICE", "DEFAULT_FLASHCARDS", "FEYNMAN_FLASHCARDS"),
                    expectedMethods = listOf(
                        PracticeMethod.UNIQUE_CHOICE,
                        PracticeMethod.DEFAULT_FLASHCARDS,
                        PracticeMethod.FEYNMAN_FLASHCARDS
                    ),
                    scenario = "Mapeo de 3 métodos principales"
                ),
                ExerciseSessionTestCase.PracticeMethodsCase(
                    exerciseTypes = listOf("FLASHCARD", "DEFAULT_FLASHCARDS", "UNIQUE_CHOICE"),
                    expectedMethods = listOf(
                        PracticeMethod.DEFAULT_FLASHCARDS,
                        PracticeMethod.UNIQUE_CHOICE
                    ),
                    scenario = "Unifica alias y duplicados"
                ),
                ExerciseSessionTestCase.PracticeMethodsCase(
                    exerciseTypes = listOf("COMPLETE_CODE", "WHATS_OUTPUT", "UNKNOWN_TYPE"),
                    expectedMethods = emptyList(),
                    scenario = "Ignora tipos no válidos"
                ),

                // ====================================================================================
                // --- (PU-10) - PRIORIZACIÓN DE EJERCICIOS DE CÓDIGO Y LÍMITE DE SESIÓN ---
                // ====================================================================================
                ExerciseSessionTestCase.CodePrioritizationCase(
                    exercises = CODE_EXERCISES_POOL,
                    type = "COMPLETE_CODE",
                    completedIds = setOf("ex_1", "ex_2", "ex_3", "ex_4", "ex_5"),
                    limit = 5,
                    expectedSize = 5,
                    expectedExcludedIds = setOf("ex_1", "ex_2", "ex_3", "ex_4", "ex_5"),
                    scenario = "Prioriza 5 no completados"
                ),
                ExerciseSessionTestCase.CodePrioritizationCase(
                    exercises = CODE_EXERCISES_POOL,
                    type = "COMPLETE_CODE",
                    completedIds = (1..10).map { "ex_$it" }.toSet(),
                    limit = 5,
                    expectedSize = 5,
                    expectedExcludedIds = emptySet(),
                    scenario = "Rellena con completados"
                ),
                ExerciseSessionTestCase.CodePrioritizationCase(
                    exercises = CODE_EXERCISES_POOL,
                    type = "WHATS_OUTPUT",
                    completedIds = emptySet(),
                    limit = 5,
                    expectedSize = 0,
                    expectedExcludedIds = emptySet(),
                    scenario = "Tipo sin ejercicios retorna vacío"
                ),

                // ====================================================================================
                // --- (PU-11) - PRIORIZACIÓN FSRS-6 EN FLASHCARDS (MODO NORMAL) ---
                // ====================================================================================
                ExerciseSessionTestCase.FlashcardOrderCase(
                    exercises = FLASHCARDS_POOL,
                    type = "DEFAULT_FLASHCARDS",
                    onlyDue = false,
                    progressList = FSRS_PROGRESS_LIST,
                    currentTimeMillis = NOW_MILLIS,
                    limit = 5,
                    expectedOrderedIds = listOf("card_due_old", "card_due_recent", "card_new", "card_new_2", "card_upcoming"),
                    scenario = "Jerarquía FSRS vencidas primero"
                ),
                ExerciseSessionTestCase.FlashcardOrderCase(
                    exercises = listOf(CARD_DUE_RECENT, CARD_DUE_OLDER),
                    type = "DEFAULT_FLASHCARDS",
                    onlyDue = false,
                    progressList = FSRS_PROGRESS_LIST,
                    currentTimeMillis = NOW_MILLIS,
                    limit = 1,
                    expectedOrderedIds = listOf("card_due_old"),
                    scenario = "Respeta límite de sesión"
                ),

                // ====================================================================================
                // --- (PU-12) - FILTRADO ESTRICTO DE REPASO FSRS-6 (MODO ONLY DUE) ---
                // ====================================================================================
                ExerciseSessionTestCase.FlashcardOrderCase(
                    exercises = FLASHCARDS_POOL,
                    type = "DEFAULT_FLASHCARDS",
                    onlyDue = true,
                    progressList = FSRS_PROGRESS_LIST,
                    currentTimeMillis = NOW_MILLIS,
                    limit = 5,
                    expectedOrderedIds = listOf("card_due_old", "card_due_recent"),
                    scenario = "Repaso filtra solo vencidas"
                ),
                ExerciseSessionTestCase.FlashcardOrderCase(
                    exercises = listOf(CARD_NEW, CARD_UPCOMING),
                    type = "DEFAULT_FLASHCARDS",
                    onlyDue = true,
                    progressList = FSRS_PROGRESS_LIST,
                    currentTimeMillis = NOW_MILLIS,
                    limit = 5,
                    expectedOrderedIds = emptyList(),
                    scenario = "Repaso sin vencidas retorna vacío"
                )
            )
        }
    }

    @Test
    fun verifyMultipleTestCaseScenariosForExercisePrioritization() {
        when (val case = testCase) {
            is ExerciseSessionTestCase.PracticeMethodsCase -> {
                // Act
                val result = determineAvailablePracticeMethodsUseCase(case.exerciseTypes)

                // Assert
                assertEquals(
                    "Fallo en `determineAvailablePracticeMethodsUseCase` (${case.scenario})",
                    case.expectedMethods,
                    result
                )
            }

            is ExerciseSessionTestCase.CodePrioritizationCase -> {
                // Act
                val result = prioritizeExerciseSessionUseCase(
                    exercises = case.exercises,
                    type = case.type,
                    completedExerciseIds = case.completedIds,
                    limit = case.limit,
                    currentTimeMillis = NOW_MILLIS
                )

                // Assert
                assertEquals(
                    "Fallo en cantidad de ejercicios devueltos (${case.scenario})",
                    case.expectedSize,
                    result.size
                )
                assertTrue(
                    "Fallo en priorización de no completados (${case.scenario})",
                    result.all { it.id !in case.expectedExcludedIds }
                )
            }

            is ExerciseSessionTestCase.FlashcardOrderCase -> {
                // Act
                val result = prioritizeExerciseSessionUseCase(
                    exercises = case.exercises,
                    type = case.type,
                    onlyDue = case.onlyDue,
                    learningProgressList = case.progressList,
                    currentTimeMillis = case.currentTimeMillis,
                    limit = case.limit
                )

                // Assert
                assertEquals(
                    "Fallo en cantidad de flashcards seleccionadas (${case.scenario})",
                    case.expectedOrderedIds.size,
                    result.size
                )
                assertEquals(
                    "Fallo en orden de priorización FSRS-6 (${case.scenario})",
                    case.expectedOrderedIds,
                    result.map { it.id }
                )
            }
        }
    }
}
package com.kipucode.unit

import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.usecase.exercise.CalculateExerciseSessionSummaryUseCase
import com.kipucode.domain.usecase.exercise.EvaluateBlockCompletionUseCase
import com.kipucode.domain.usecase.exercise.EvaluateUniqueChoiceUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

sealed class ExerciseEvaluationTestCase(
    val scenario: String
) {
    class UniqueChoiceCase(
        val baseXp: Int,
        val isCorrect: Boolean,
        val expectedIsCorrect: Boolean,
        val expectedXp: Int,
        val expectedFeedback: String,
        scenario: String
    ) : ExerciseEvaluationTestCase(scenario) {
        override fun toString(): String = "uniqueChoice: '$isCorrect' ($baseXp XP) -> expectedXp: $expectedXp ($scenario)"
    }

    class BlockCompletionCase(
        val placedBlocks: Map<Int, BlockOptionDomain>,
        val allOptions: List<BlockOptionDomain>,
        val totalSlots: Int,
        val baseXp: Int = 20,
        val expectedIsAllCorrect: Boolean,
        val expectedXp: Int,
        val expectedFeedback: String,
        val expectedSlotResults: Map<Int, Boolean>,
        scenario: String
    ) : ExerciseEvaluationTestCase(scenario) {
        override fun toString(): String = "completeCode: '${placedBlocks.size}/$totalSlots slots' -> expected: $expectedIsAllCorrect ($scenario)"
    }

    class SessionSummaryCase(
        val earnedXpList: List<Int>,
        val correctCount: Int,
        val totalCount: Int,
        val startTimeMillis: Long,
        val currentTimeMillis: Long,
        val expectedTotalXp: Int,
        val expectedTimeSeconds: Long,
        scenario: String
    ) : ExerciseEvaluationTestCase(scenario) {
        override fun toString(): String = "sessionSummary: '$correctCount/$totalCount aciertos' -> expectedXp: $expectedTotalXp ($scenario)"
    }
}

@RunWith(Parameterized::class)
class ExerciseEvaluationUnitTest(
    private val testCase: ExerciseEvaluationTestCase
) {

    private lateinit var evaluateUniqueChoiceUseCase: EvaluateUniqueChoiceUseCase
    private lateinit var evaluateBlockCompletionUseCase: EvaluateBlockCompletionUseCase
    private lateinit var calculateExerciseSessionSummaryUseCase: CalculateExerciseSessionSummaryUseCase

    @Before
    fun setUp() {
        evaluateUniqueChoiceUseCase = EvaluateUniqueChoiceUseCase()
        evaluateBlockCompletionUseCase = EvaluateBlockCompletionUseCase()
        calculateExerciseSessionSummaryUseCase = CalculateExerciseSessionSummaryUseCase()
    }

    companion object {
        private val BLOCK_PRINT_1 = BlockOptionDomain(
            id = "b1",
            exerciseId = "ex1",
            content = "Console.WriteLine",
            isCorrect = true,
            orderIndex = 1
        )
        private val BLOCK_PRINT_2 = BlockOptionDomain(
            id = "b2",
            exerciseId = "ex1",
            content = "(\"Hola Kipu\");",
            isCorrect = true,
            orderIndex = 2
        )
        private val BLOCK_DISTRACTOR = BlockOptionDomain(
            id = "b3",
            exerciseId = "ex1",
            content = "System.out.println",
            isCorrect = false,
            orderIndex = 3
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<ExerciseEvaluationTestCase> {
            return listOf(
                // ====================================================================================
                // --- (PU-14) - EVALUACIÓN DE SELECCIÓN ÚNICA ---
                // ====================================================================================
                ExerciseEvaluationTestCase.UniqueChoiceCase(
                    baseXp = 20,
                    isCorrect = true,
                    expectedIsCorrect = true,
                    expectedXp = 20,
                    expectedFeedback = "¡Excelente! Concepto dominado",
                    scenario = "Respuesta correcta 100% XP"
                ),
                ExerciseEvaluationTestCase.UniqueChoiceCase(
                    baseXp = 20,
                    isCorrect = false,
                    expectedIsCorrect = false,
                    expectedXp = 10,
                    expectedFeedback = "¡Cerca! Equivocarse es parte de aprender",
                    scenario = "Respuesta incorrecta 50% XP"
                ),
                ExerciseEvaluationTestCase.UniqueChoiceCase(
                    baseXp = 30,
                    isCorrect = false,
                    expectedIsCorrect = false,
                    expectedXp = 15,
                    expectedFeedback = "¡Cerca! Equivocarse es parte de aprender",
                    scenario = "Penalización escalada a 30 XP"
                ),

                // ====================================================================================
                // --- (PU-15) - EVALUACIÓN DE BLOQUES DE CÓDIGO (COMPLETE_CODE) ---
                // ====================================================================================
                ExerciseEvaluationTestCase.BlockCompletionCase(
                    placedBlocks = mapOf(0 to BLOCK_PRINT_1, 1 to BLOCK_PRINT_2),
                    allOptions = listOf(BLOCK_DISTRACTOR, BLOCK_PRINT_2, BLOCK_PRINT_1),
                    totalSlots = 2,
                    baseXp = 20,
                    expectedIsAllCorrect = true,
                    expectedXp = 20,
                    expectedFeedback = "¡Excelente! Has completado el código a la perfección",
                    expectedSlotResults = mapOf(0 to true, 1 to true),
                    scenario = "Bloques en orden correcto"
                ),
                ExerciseEvaluationTestCase.BlockCompletionCase(
                    placedBlocks = mapOf(
                        0 to BLOCK_PRINT_1.copy(content = "  Console.WriteLine  "),
                        1 to BLOCK_PRINT_2
                    ),
                    allOptions = listOf(BLOCK_PRINT_1, BLOCK_PRINT_2),
                    totalSlots = 2,
                    baseXp = 20,
                    expectedIsAllCorrect = true,
                    expectedXp = 20,
                    expectedFeedback = "¡Excelente! Has completado el código a la perfección",
                    expectedSlotResults = mapOf(0 to true, 1 to true),
                    scenario = "Ignora espacios con trim"
                ),
                ExerciseEvaluationTestCase.BlockCompletionCase(
                    placedBlocks = mapOf(0 to BLOCK_PRINT_2, 1 to BLOCK_PRINT_1),
                    allOptions = listOf(BLOCK_PRINT_1, BLOCK_PRINT_2),
                    totalSlots = 2,
                    baseXp = 20,
                    expectedIsAllCorrect = false,
                    expectedXp = 10,
                    expectedFeedback = "¡Casi! Revisa la sintaxis de los bloques",
                    expectedSlotResults = mapOf(0 to false, 1 to false),
                    scenario = "Bloques en orden invertido"
                ),
                ExerciseEvaluationTestCase.BlockCompletionCase(
                    placedBlocks = mapOf(0 to BLOCK_DISTRACTOR),
                    allOptions = listOf(BLOCK_PRINT_1, BLOCK_DISTRACTOR),
                    totalSlots = 1,
                    baseXp = 20,
                    expectedIsAllCorrect = false,
                    expectedXp = 10,
                    expectedFeedback = "¡Casi! Revisa la sintaxis de los bloques",
                    expectedSlotResults = mapOf(0 to false),
                    scenario = "Bloque distractor inválido"
                ),
                ExerciseEvaluationTestCase.BlockCompletionCase(
                    placedBlocks = mapOf(0 to BLOCK_PRINT_1),
                    allOptions = listOf(BLOCK_PRINT_1, BLOCK_PRINT_2),
                    totalSlots = 2,
                    baseXp = 20,
                    expectedIsAllCorrect = false,
                    expectedXp = 10,
                    expectedFeedback = "¡Casi! Revisa la sintaxis de los bloques",
                    expectedSlotResults = mapOf(0 to true, 1 to false),
                    scenario = "Slot vacío incompleto"
                ),

                // ====================================================================================
                // --- (PU-16) - RESUMEN DE SESIÓN DE EJERCICIOS ---
                // ====================================================================================
                ExerciseEvaluationTestCase.SessionSummaryCase(
                    earnedXpList = listOf(20, 10, 20, 20, 10),
                    correctCount = 3,
                    totalCount = 5,
                    startTimeMillis = 10_000L,
                    currentTimeMillis = 55_000L,
                    expectedTotalXp = 80,
                    expectedTimeSeconds = 45L,
                    scenario = "Suma de XP y 45s exactos"
                ),
                ExerciseEvaluationTestCase.SessionSummaryCase(
                    earnedXpList = emptyList(),
                    correctCount = 0,
                    totalCount = 5,
                    startTimeMillis = 10_000L,
                    currentTimeMillis = 10_200L,
                    expectedTotalXp = 0,
                    expectedTimeSeconds = 1L,
                    scenario = "Tiempo mínimo de 1s"
                )
            )
        }
    }

    @Test
    fun verifyMultipleTestCaseScenariosForExerciseEvaluation() {
        when (val case = testCase) {
            is ExerciseEvaluationTestCase.UniqueChoiceCase -> {
                // Act
                val result = evaluateUniqueChoiceUseCase(
                    baseXp = case.baseXp,
                    isCorrect = case.isCorrect
                )

                // Assert
                assertEquals(
                    "Fallo en `isCorrect` (${case.scenario})",
                    case.expectedIsCorrect,
                    result.isCorrect
                )
                assertEquals(
                    "Fallo en `earnedXp` (${case.scenario})",
                    case.expectedXp,
                    result.earnedXp
                )
                assertEquals(
                    "Fallo en `feedbackMessage` (${case.scenario})",
                    case.expectedFeedback,
                    result.feedbackMessage
                )
            }

            is ExerciseEvaluationTestCase.BlockCompletionCase -> {
                // Act
                val result = evaluateBlockCompletionUseCase(
                    placedBlocks = case.placedBlocks,
                    allOptions = case.allOptions,
                    totalSlots = case.totalSlots,
                    baseXp = case.baseXp
                )

                // Assert
                assertEquals(
                    "Fallo en `isAllCorrect` (${case.scenario})",
                    case.expectedIsAllCorrect,
                    result.isAllCorrect
                )
                assertEquals(
                    "Fallo en `earnedXp` (${case.scenario})",
                    case.expectedXp,
                    result.earnedXp
                )
                assertEquals(
                    "Fallo en `feedbackMessage` (${case.scenario})",
                    case.expectedFeedback,
                    result.feedbackMessage
                )
                assertEquals(
                    "Fallo en `slotResults` (${case.scenario})",
                    case.expectedSlotResults,
                    result.slotResults
                )
            }

            is ExerciseEvaluationTestCase.SessionSummaryCase -> {
                // Act
                val result = calculateExerciseSessionSummaryUseCase(
                    earnedXpList = case.earnedXpList,
                    correctCount = case.correctCount,
                    totalCount = case.totalCount,
                    startTimeMillis = case.startTimeMillis,
                    currentTimeMillis = case.currentTimeMillis
                )

                // Assert
                assertEquals(
                    "Fallo en `xpEarned` (${case.scenario})",
                    case.expectedTotalXp,
                    result.xpEarned
                )
                assertEquals(
                    "Fallo en `correctCount` (${case.scenario})",
                    case.correctCount,
                    result.correctCount
                )
                assertEquals(
                    "Fallo en `totalCount` (${case.scenario})",
                    case.totalCount,
                    result.totalCount
                )
                assertEquals(
                    "Fallo en `timeSeconds` (${case.scenario})",
                    case.expectedTimeSeconds,
                    result.timeSeconds
                )
            }
        }
    }
}
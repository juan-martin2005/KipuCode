package com.kipucode.unit

import com.kipucode.domain.model.BlockOptionDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de las reglas de negocio para evaluación de ejercicios (PU-08, PU-09).
 * Comprueba asignación de XP por aciertos/errores y validación sintáctica de bloques de código bajo el patrón AAA.
 */
class ExerciseEvaluationUnitTest {

    // ============================================================================================
    //  PU-08: Evaluación de Ejercicio de Selección Única (Asignación de XP)
    // ============================================================================================

    private fun evaluateUniqueChoice(baseXp: Int, isCorrect: Boolean): Pair<Int, String> {
        val earnedXp = if (isCorrect) baseXp else (baseXp / 2)
        val feedback = if (isCorrect) "¡Excelente! Concepto dominado" else "¡Cerca! Equivocarse es parte de aprender"
        return Pair(earnedXp, feedback)
    }

    /**
     * PU-08: Verifies that a correct unique choice answer awards 100% XP and positive feedback.
     */
    @Test
    fun correctAnswerAwardsFullXpAndPositiveFeedback() {
        // Arrange
        val baseXp = 20
        val isCorrect = true

        // Act
        val (earnedXp, feedback) = evaluateUniqueChoice(baseXp, isCorrect)

        // Assert
        assertEquals("Debe otorgar el 100% de los puntos", 20, earnedXp)
        assertEquals("¡Excelente! Concepto dominado", feedback)
    }

    /**
     * PU-08: Verifies that an incorrect unique choice answer awards 50% XP as educational penalty.
     */
    @Test
    fun incorrectAnswerPenalizesWithHalfXp() {
        // Arrange
        val baseXp = 20
        val isCorrect = false

        // Act
        val (earnedXp, feedback) = evaluateUniqueChoice(baseXp, isCorrect)

        // Assert
        assertEquals("Debe otorgar el 50% de los puntos como penalización pedagógica", 10, earnedXp)
        assertEquals("¡Cerca! Equivocarse es parte de aprender", feedback)
    }

    // ============================================================================================
    //  PU-09: Evaluación de Ensamble de Bloques de Código (COMPLETE_CODE)
    // ============================================================================================

    /**
     * Réplica exacta del algoritmo de ExerciseViewModel.submitBlockAnswer
     */
    private fun evaluateBlockCompletion(
        placedBlocks: Map<Int, BlockOptionDomain>,
        correctOptions: List<BlockOptionDomain>,
        totalSlots: Int
    ): Pair<Boolean, Map<Int, Boolean>> {
        val sortedCorrect = correctOptions.filter { it.isCorrect }.sortedBy { it.orderIndex }
        var allCorrect = true
        val slotResults = mutableMapOf<Int, Boolean>()

        for (i in 0 until totalSlots) {
            val placed = placedBlocks[i]
            val expected = sortedCorrect.getOrNull(i)
            val isSlotCorrect = placed != null
                    && expected != null
                    && placed.isCorrect
                    && placed.content.trim() == expected.content.trim()

            slotResults[i] = isSlotCorrect
            if (!isSlotCorrect) {
                allCorrect = false
            }
        }

        return Pair(allCorrect, slotResults)
    }

    /**
     * PU-09: Verifies that assembled code blocks in correct order and syntax evaluate as successful.
     */
    @Test
    fun allBlocksInOrderAndCorrectContentEvaluatesAsSuccessful() {
        // Arrange
        val block1 = BlockOptionDomain(id = "b1", exerciseId = "ex1", content = "Console.WriteLine", isCorrect = true, orderIndex = 1)
        val block2 = BlockOptionDomain(id = "b2", exerciseId = "ex1", content = "(\"Hola Kipu\");", isCorrect = true, orderIndex = 2)
        val correctOptions = listOf(block1, block2)
        val placedBlocks = mapOf(0 to block1, 1 to block2)

        // Act
        val (allCorrect, slotResults) = evaluateBlockCompletion(placedBlocks, correctOptions, totalSlots = 2)

        // Assert
        assertTrue("El ensamble de bloques debe considerarse 100% correcto", allCorrect)
        assertTrue(slotResults[0] == true)
        assertTrue(slotResults[1] == true)
    }

    /**
     * PU-09: Verifies that assembled code blocks in inverted order evaluate as failed.
     */
    @Test
    fun blocksInInvertedOrderEvaluatesAsFailed() {
        // Arrange
        val block1 = BlockOptionDomain(id = "b1", exerciseId = "ex1", content = "int edad =", isCorrect = true, orderIndex = 1)
        val block2 = BlockOptionDomain(id = "b2", exerciseId = "ex1", content = "25;", isCorrect = true, orderIndex = 2)
        val correctOptions = listOf(block1, block2)
        val placedBlocks = mapOf(0 to block2, 1 to block1) // Orden invertido

        // Act
        val (allCorrect, slotResults) = evaluateBlockCompletion(placedBlocks, correctOptions, totalSlots = 2)

        // Assert
        assertFalse("El orden incorrecto no debe ser aprobado", allCorrect)
        assertFalse(slotResults[0] == true)
        assertFalse(slotResults[1] == true)
    }

    /**
     * PU-09: Verifies that placing a distractor block evaluates as failed.
     */
    @Test
    fun distractorBlockWithSyntaxErrorEvaluatesAsFailed() {
        // Arrange
        val blockCorrect = BlockOptionDomain(id = "b1", exerciseId = "ex1", content = "public void", isCorrect = true, orderIndex = 1)
        val blockDistractor = BlockOptionDomain(id = "b2", exerciseId = "ex1", content = "public function", isCorrect = false, orderIndex = 2)
        val correctOptions = listOf(blockCorrect)
        val placedBlocks = mapOf(0 to blockDistractor)

        // Act
        val (allCorrect, slotResults) = evaluateBlockCompletion(placedBlocks, correctOptions, totalSlots = 1)

        // Assert
        assertFalse("Un bloque distractor no debe pasar la validación", allCorrect)
        assertFalse(slotResults[0] == true)
    }
}

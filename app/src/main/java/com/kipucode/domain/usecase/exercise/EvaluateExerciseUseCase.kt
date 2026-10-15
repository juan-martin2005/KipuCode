package com.kipucode.domain.usecase.exercise

import com.kipucode.domain.model.BlockOptionDomain
import javax.inject.Inject

// =========================================================================================
//  Modelos de Resultado de Evaluación
// =========================================================================================
data class UniqueChoiceResult(
    val isCorrect: Boolean,
    val earnedXp: Int,
    val feedbackMessage: String
)

data class BlockCompletionResult(
    val isAllCorrect: Boolean,
    val earnedXp: Int,
    val feedbackMessage: String,
    val slotResults: Map<Int, Boolean>,
    val evaluatedPlacedBlocks: Map<Int, BlockOptionDomain>
)

data class SessionSummaryResult(
    val xpEarned: Int,
    val correctCount: Int,
    val totalCount: Int,
    val timeSeconds: Long
)

// =========================================================================================
//  Caso de Uso: Evaluación de Selección Única (PU-08)
// =========================================================================================
class EvaluateUniqueChoiceUseCase @Inject constructor() {
    operator fun invoke(baseXp: Int, isCorrect: Boolean): UniqueChoiceResult {
        val earnedXp = if (isCorrect) baseXp else (baseXp / 2)
        val feedback = if (isCorrect) {
            "¡Excelente! Concepto dominado"
        } else {
            "¡Cerca! Equivocarse es parte de aprender"
        }
        return UniqueChoiceResult(
            isCorrect = isCorrect,
            earnedXp = earnedXp,
            feedbackMessage = feedback
        )
    }
}

// =========================================================================================
//  Caso de Uso: Evaluación de Bloques de Código COMPLETE_CODE (PU-09)
// =========================================================================================
class EvaluateBlockCompletionUseCase @Inject constructor() {
    operator fun invoke(
        placedBlocks: Map<Int, BlockOptionDomain>,
        allOptions: List<BlockOptionDomain>,
        totalSlots: Int,
        baseXp: Int
    ): BlockCompletionResult {
        val correctOptions = allOptions.filter { it.isCorrect }.sortedBy { it.orderIndex }
        var allCorrect = true
        val slotResults = mutableMapOf<Int, Boolean>()
        val evaluatedPlaced = mutableMapOf<Int, BlockOptionDomain>()

        for (i in 0 until totalSlots) {
            val placed = placedBlocks[i]
            val expected = correctOptions.getOrNull(i)
            val isInCorrectSlot = placed != null
                    && expected != null
                    && placed.isCorrect
                    && placed.content.trim() == expected.content.trim()

            slotResults[i] = isInCorrectSlot
            if (placed != null) {
                evaluatedPlaced[i] = placed.copy(isCorrect = isInCorrectSlot)
            }
            if (!isInCorrectSlot) {
                allCorrect = false
            }
        }

        val earnedXp = if (allCorrect) baseXp else (baseXp / 2)
        val feedbackMessage = if (allCorrect) {
            "¡Excelente! Has completado el código a la perfección"
        } else {
            "¡Casi! Revisa la sintaxis de los bloques"
        }

        return BlockCompletionResult(
            isAllCorrect = allCorrect,
            earnedXp = earnedXp,
            feedbackMessage = feedbackMessage,
            slotResults = slotResults,
            evaluatedPlacedBlocks = evaluatedPlaced
        )
    }
}

// =========================================================================================
//  Caso de Uso: Cálculo de Métricas de la Sesión de Ejercicios
// =========================================================================================
class CalculateExerciseSessionSummaryUseCase @Inject constructor() {
    operator fun invoke(
        earnedXpList: Collection<Int>,
        correctCount: Int,
        totalCount: Int,
        startTimeMillis: Long,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SessionSummaryResult {
        val totalTimeSeconds = maxOf(1L, (currentTimeMillis - startTimeMillis) / 1000)
        return SessionSummaryResult(
            xpEarned = earnedXpList.sum(),
            correctCount = correctCount,
            totalCount = totalCount,
            timeSeconds = totalTimeSeconds
        )
    }
}

// =========================================================================================
//  Caso de Uso: Evaluacion de Flashcards / Autoevaluacion
// =========================================================================================
data class FlashCardEvaluationResult(
    val earnedXp: Int,
    val feedbackMessage: String
)

class EvaluateFlashCardUseCase @Inject constructor() {
    operator fun invoke(baseXp: Int, rating: Int): FlashCardEvaluationResult {
        // rating: 1: Again, 2: Hard, 3: Good, 4: Easy
        val earnedXp = when (rating) {
            4, 3 -> baseXp
            2 -> (baseXp * 0.75).toInt()
            else -> baseXp / 2
        }
        val feedback = when (rating) {
            4 -> "¡Excelente retencion! Dominas este concepto"
            3 -> "¡Buen trabajo! Concepto aprendido"
            2 -> "Comprendido, pero requiere mas practica"
            else -> "No te preocupes, la repeticion espaciada te ayudara"
        }
        return FlashCardEvaluationResult(
            earnedXp = earnedXp,
            feedbackMessage = feedback
        )
    }
}

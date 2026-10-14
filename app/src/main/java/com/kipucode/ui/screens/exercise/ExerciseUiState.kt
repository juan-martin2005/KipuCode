package com.kipucode.ui.screens.exercise

import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response

data class AnswerExplanation(
    val isCorrect: Boolean,
    val explanation: String,
    val experience: String,
    val message: String
)

data class ExerciseSessionData(
    val xpEarned: Int,
    val correctCount: Int,
    val totalCount: Int,
    val timeSeconds: Long
)

data class ExerciseUiState(
    val isLoading: Boolean = true,
    val exercises: List<ExerciseDomain> = emptyList(),
    val currentIndex: Int = 0,
    val lessonName: String = "",
    val selectedOptionId: String? = null,
    val answerExplanation: AnswerExplanation? = null,
    val placedBlocks: Map<Int, BlockOptionDomain> = emptyMap(),
    val availableBlocks: List<BlockOptionDomain> = emptyList(),
    val isBlockEvaluated: Boolean = false,
    val completeState: Response<Unit>? = null
) {
    val currentExercise: ExerciseDomain?
        get() = exercises.getOrNull(currentIndex)

    val totalExercises: Int
        get() = exercises.size

    val isLastExercise: Boolean
        get() = currentIndex >= exercises.size - 1

    val isCompleting: Boolean
        get() = completeState is Response.Loading
}

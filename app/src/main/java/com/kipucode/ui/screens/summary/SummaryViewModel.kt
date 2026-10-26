package com.kipucode.ui.screens.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kipucode.domain.usecase.exercise.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.learning.GetAllLearningProgressUseCase
import com.kipucode.domain.usecase.learning.GetDueExercisesUseCase
import com.kipucode.domain.usecase.summary.CalculateSessionMetricsUseCase
import com.kipucode.ui.navigation.SummaryRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calculateSessionMetricsUseCase: CalculateSessionMetricsUseCase,
    private val getDueExercisesUseCase: GetDueExercisesUseCase,
    private val getAllLearningProgressUseCase: GetAllLearningProgressUseCase,
    private val getExercisesByLessonUseCase: GetExercisesByLessonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState(isLoading = true))
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        val route = savedStateHandle.toRoute<SummaryRoute>()
        val metrics = calculateSessionMetricsUseCase(
            correctCount = route.correct,
            totalCount = route.total,
            timeSeconds = route.timeSeconds
        )

        val isFlashcardSession = route.isFlashcard ||
                route.onlyDue ||
                route.type?.contains("FLASHCARD", ignoreCase = true) == true

        viewModelScope.launch {
            val remainingDueCount = if (route.onlyDue) {
                try {
                    getDueExercisesUseCase().first().size
                } catch (e: Exception) {
                    0
                }
            } else 0

            val dynamicMessage = if (route.onlyDue) {
                if (remainingDueCount > 0) {
                    "¡Gran trabajo! Aún tienes conceptos por reforzar a lo largo del día. Por ahora ya consolidaste estos conocimientos clave."
                } else {
                    "¡Excelente! Estás al día con todos tus repasos. Tu memoria a largo plazo te lo agradecerá."
                }
            } else {
                metrics.summaryMessage
            }

            // Cálculo dinámico contextual (por lección si viene de una lección, o global si es repaso urgente)
            val calculatedNextReviewText = calculateNextReviewText(
                lessonId = route.lessonId,
                isOnlyDue = route.onlyDue,
                remainingDueCount = remainingDueCount
            )

            _uiState.update { current ->
                current.copy(
                    xpEarned = route.xp,
                    accuracy = metrics.accuracy,
                    correctCount = metrics.correctCount,
                    reinforceCount = metrics.reinforceCount,
                    timeFormatted = metrics.timeFormatted,
                    summaryMessage = dynamicMessage,
                    showRepetitionSchedule = isFlashcardSession,
                    memoryRetentionPct = metrics.memoryRetentionPct,
                    nextReviewText = calculatedNextReviewText,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun calculateNextReviewText(
        lessonId: String,
        isOnlyDue: Boolean,
        remainingDueCount: Int
    ): String {
        if (isOnlyDue && remainingDueCount > 0) {
            return "Hoy más tarde"
        }

        return try {
            val now = System.currentTimeMillis()
            val allProgress = getAllLearningProgressUseCase().first()

            // Si es una lección puntual, consideramos únicamente las tarjetas de esa lección
            val relevantProgress = if (lessonId.isNotEmpty() && !isOnlyDue) {
                val lessonExerciseIds = getExercisesByLessonUseCase(lessonId).first()
                    .map { it.id }
                    .toSet()
                allProgress.filter { it.exerciseId in lessonExerciseIds }
            } else {
                allProgress
            }

            val nextDueDate = relevantProgress
                .map { it.dueDate }
                .filter { it > now }
                .minOrNull()

            if (nextDueDate == null) {
                "Mañana"
            } else {
                val diffMillis = nextDueDate - now
                val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis)
                val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

                when {
                    diffHours < 1 -> "En minutos"
                    diffHours < 20 -> "Hoy más tarde"
                    diffDays <= 1 -> "Mañana"
                    diffDays in 2..6 -> "En $diffDays días"
                    diffDays in 7..13 -> "En 1 semana"
                    else -> "En ${diffDays / 7} semanas"
                }
            }
        } catch (e: Exception) {
            "Mañana"
        }
    }
}

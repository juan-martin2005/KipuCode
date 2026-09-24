package com.kipucode.ui.screens.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.card.KipuDialog
import com.kipucode.ui.screens.exercise.components.ExplanationDialog
import com.kipucode.ui.screens.exercise.components.Flashcard
import com.kipucode.ui.screens.exercise.components.UniqueChoice
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.viewmodel.ExerciseSessionData
import com.kipucode.viewmodel.ExerciseViewModel

// --- SCREEN ---
@Composable
fun ExerciseScreen(
    lessonId: String,
    type: String? = null,
    onBack: () -> Unit,
    onFinished: (ExerciseSessionData) -> Unit,
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }

    val completeState by exerciseViewModel.completeState.collectAsStateWithLifecycle()
    val isLoading = completeState is Response.Loading

    LaunchedEffect(lessonId, type) {
        exerciseViewModel.resetExerciseProgress()
        exerciseViewModel.loadExercises(lessonId, type = type)
    }

    if (completeState is Response.Success) {
        val sessionData = exerciseViewModel.getSessionSummary()
        exerciseViewModel.resetCompleteState()
        onFinished(sessionData)
    }

    val exercises by exerciseViewModel.exercisesState.collectAsStateWithLifecycle()
    val currentIndex by exerciseViewModel.currentExerciseIndex.collectAsStateWithLifecycle()
    val selectedOptionId by exerciseViewModel.selectedOptionId.collectAsStateWithLifecycle()
    val explanation by exerciseViewModel.answerExplanation.collectAsStateWithLifecycle()
    val lessonName by exerciseViewModel.lessonName.collectAsStateWithLifecycle()

    val currentExercise = exercises.getOrNull(currentIndex)
    val isFlashCard = currentExercise?.type == "FLASHCARD"

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AnimatedVisibility(
                visible = explanation != null && !isFlashCard,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                explanation?.let {
                    ExplanationDialog(
                        isCorrect = it.isCorrect,
                        isLoading = isLoading,
                        onContinue = {
                            if (currentIndex < exercises.size - 1) {
                                exerciseViewModel.nextExercise()
                            } else {
                                exerciseViewModel.finishLessonExercises(lessonId)
                            }
                        },
                        experience = it.experience,
                        explanation = it.explanation,
                        message = it.message
                    )
                }
            }
        }
    ) { paddingValues ->
        ExerciseScreenContent(
            modifier = Modifier.padding(paddingValues),
            currentExercise = currentExercise,
            currentExerciseId = currentExercise?.id,
            lessonName = lessonName,
            instruction = currentExercise?.instruction,
            options = currentExercise?.options,
            currentIndex = currentIndex,
            totalExercises = exercises.size,
            selectedOptionId = selectedOptionId,
            onBackClick = { showBackDialog = true },
            onOptionSelected = { exerciseViewModel.submitAnswer(it) },
            onRatingSelected = { rating -> exerciseViewModel.rateFlashCard(rating, lessonId) }
        )
    }

    if (showBackDialog) {
        KipuDialog(
            title = stringResource(R.string.exit_exercise_title),
            description = stringResource(R.string.exit_exercise_desc),
            dismissButtonText = stringResource(R.string.exit_exercise_cancel),
            confirmButtonText = stringResource(R.string.exit_exercise_confirm),
            iconRes = R.drawable.ic_warning,
            onDismissRequest = {
                showBackDialog = false
            },
            onDismissClick = {
                showBackDialog = false
            },
            onConfirmClick = {
                showBackDialog = false
                onBack()
            },
            iconTint = KipuTeal
        )
    }
}

// ---  CONTENT ---
@Composable
fun ExerciseScreenContent(
    modifier: Modifier = Modifier,
    currentExercise: ExerciseDomain?,
    currentExerciseId: String?,
    lessonName: String,
    instruction: String?,
    options: List<BlockOptionDomain>?,
    currentIndex: Int,
    totalExercises: Int,
    selectedOptionId: String?,
    onBackClick: () -> Unit,
    onOptionSelected: (BlockOptionDomain) -> Unit,
    onRatingSelected: (Int) -> Unit
) {
    if (currentExerciseId == null || instruction == null || options == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        item {
            KipuTopBar(
                title = lessonName,
                onBackClick = onBackClick,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }

        item {
            when (currentExercise?.type) {
                "FLASHCARD" -> {
                    Flashcard(
                        currentFc = currentIndex + 1,
                        totalFc = totalExercises,
                        instruction = instruction,
                        answer = if (currentExercise.answer.isNotBlank()) currentExercise.answer else options.firstOrNull()?.content.orEmpty(),
                        module = lessonName.ifBlank { "Módulo actual" },
                        onRatingSelect = onRatingSelected,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                else -> {
                    UniqueChoice(
                        current = currentIndex + 1,
                        total = totalExercises,
                        instruction = instruction,
                        options = options,
                        selectedOptionId = selectedOptionId,
                        onOptionSelected = onOptionSelected,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Exercise Screen Content")
@Composable
fun ExerciseScreenContentPreview() {
    val exercise = ExerciseDomain(
        id = "ex_01",
        type = "FLASHCARD",
        lessonId = "",
        instruction = "Pregunta de prueba",
        orderIndex = 1,
        answer = "Respuesta de prueba"
    )
    ExerciseScreenContent(
        currentExercise = exercise,
        currentExerciseId = "ex_01",
        lessonName = "Historia de Java",
        instruction = "#### ¿En qué año nació Java?",
        options = listOf(
            BlockOptionDomain(id = "1", exerciseId = "ex_01", content = "##### 1995", isCorrect = true),
            BlockOptionDomain(id = "2", exerciseId = "ex_01", content = "##### 2000", isCorrect = false)
        ),
        currentIndex = 0,
        totalExercises = 1,
        selectedOptionId = null,
        onBackClick = {},
        onOptionSelected = {},
        onRatingSelected = {}
    )
}
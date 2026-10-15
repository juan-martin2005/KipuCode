package com.kipucode.ui.screens.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.Response
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.card.KipuDialog
import com.kipucode.ui.screens.exercise.components.BlockCompletion
import com.kipucode.ui.screens.exercise.components.ExplanationDialog
import com.kipucode.ui.screens.exercise.components.Flashcard
import com.kipucode.ui.screens.exercise.components.UniqueChoice
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

// --- SCREEN ---
@Composable
fun ExerciseScreen(
    lessonId: String,
    type: String? = null,
    onlyDue: Boolean = false,
    onBack: () -> Unit,
    onFinished: (ExerciseSessionData) -> Unit,
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }

    val uiState by exerciseViewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState.isCompleting

    LaunchedEffect(lessonId, type, onlyDue) {
        exerciseViewModel.resetExerciseProgress()
        exerciseViewModel.loadExercises(lessonId, type = type, onlyDue = onlyDue)
    }

    LaunchedEffect(uiState.completeState) {
        if (uiState.completeState is Response.Success) {
            val sessionData = exerciseViewModel.getSessionSummary()
            exerciseViewModel.resetCompleteState()
            onFinished(sessionData)
        }
    }

    val currentExercise = uiState.currentExercise
    val isFlashCard = currentExercise?.type?.contains("FLASHCARD", ignoreCase = true) == true

    LaunchedEffect(currentExercise?.id) {
        if (currentExercise?.type == "COMPLETE_CODE" && currentExercise.options.isNotEmpty()) {
            exerciseViewModel.setupBlockExercise(currentExercise.options)
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.answerExplanation != null && !isFlashCard,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                uiState.answerExplanation?.let {
                    ExplanationDialog(
                        isCorrect = it.isCorrect,
                        isLoading = isLoading,
                        onContinue = {
                            if (!uiState.isLastExercise) {
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
            isLoading = uiState.isLoading,
            currentExercise = currentExercise,
            currentExerciseId = currentExercise?.id,
            lessonName = uiState.lessonName,
            instruction = currentExercise?.instruction,
            options = currentExercise?.options,
            currentIndex = uiState.currentIndex,
            totalExercises = uiState.totalExercises,
            selectedOptionId = uiState.selectedOptionId,
            placedBlocks = uiState.placedBlocks,
            availableBlocks = uiState.availableBlocks,
            isBlockEvaluated = uiState.isBlockEvaluated,
            onBackClick = { showBackDialog = true },
            onOptionSelected = { exerciseViewModel.submitAnswer(it) },
            onRatingSelected = { rating -> exerciseViewModel.rateFlashCard(rating, lessonId) },
            onSelectBlock = { block, totalSlots -> exerciseViewModel.onSelectBlockFromPool(block, totalSlots) },
            onRemoveBlock = { slotIndex -> exerciseViewModel.onRemoveBlockFromSlot(slotIndex) },
            onSubmitBlockAnswer = { totalSlots -> exerciseViewModel.submitBlockAnswer(totalSlots) }
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

// --- CONTENT ---
@Composable
fun ExerciseScreenContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    currentExercise: ExerciseDomain?,
    currentExerciseId: String?,
    lessonName: String,
    instruction: String?,
    options: List<BlockOptionDomain>?,
    currentIndex: Int,
    totalExercises: Int,
    selectedOptionId: String?,
    placedBlocks: Map<Int, BlockOptionDomain> = emptyMap(),
    availableBlocks: List<BlockOptionDomain> = emptyList(),
    isBlockEvaluated: Boolean = false,
    onBackClick: () -> Unit,
    onOptionSelected: (BlockOptionDomain) -> Unit,
    onRatingSelected: (Int) -> Unit,
    onSelectBlock: (BlockOptionDomain, Int) -> Unit = { _, _ -> },
    onRemoveBlock: (Int) -> Unit = {},
    onSubmitBlockAnswer: (Int) -> Unit = {}
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = KipuTeal)
        }
        return
    }

    if (currentExerciseId == null || instruction == null || options == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Todo al día!",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No hay ejercicios pendientes por repasar en este momento.",
                fontFamily = Nunito,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            FilledButton(
                textButton = "Volver",
                onClickFilledButton = onBackClick
            )
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
                "DEFAULT_FLASHCARDS", "FEYNMAN_FLASHCARDS", "FLASHCARD" -> {
                    Flashcard(
                        currentFc = currentIndex + 1,
                        totalFc = totalExercises,
                        instruction = instruction,
                        answer = currentExercise.answer.ifBlank { options.firstOrNull()?.content.orEmpty() },
                        module = lessonName.ifBlank { "Módulo actual" },
                        onRatingSelect = onRatingSelected,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                "UNIQUE_CHOICE" -> {
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
                "COMPLETE_CODE" -> {
                    BlockCompletion(
                        current = currentIndex + 1,
                        total = totalExercises,
                        instruction = instruction,
                        options = options,
                        placedBlocks = placedBlocks,
                        availableBlocks = availableBlocks,
                        isEvaluated = isBlockEvaluated,
                        onSelectBlock = onSelectBlock,
                        onRemoveBlock = onRemoveBlock,
                        onSubmitAnswer = onSubmitBlockAnswer,
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
@Preview(showBackground = true, showSystemUi = true, name = "ExerciseScreen - Content")
@Composable
private fun ExerciseScreenContentPreview() {
    KipuCodeTheme {
        val exercise = ExerciseDomain(
            id = "ex_01",
            type = "UNIQUE_CHOICE",
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
}
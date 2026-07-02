package com.kipucode.ui.screens.lesson

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
import com.kipucode.domain.model.Response
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.card.KipuDialog
import com.kipucode.ui.screens.lesson.components.FeedbackDialog
import com.kipucode.ui.screens.lesson.components.UniqueChoice
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.viewmodel.ExerciseViewModel

// --- SCREEN ---
@Composable
fun ExerciseScreen(
    lessonId: String,
    onBack: () -> Unit,
    onFinished: () -> Unit,
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }

    val completeState by exerciseViewModel.completeState.collectAsStateWithLifecycle()
    val isLoading = completeState is Response.Loading

    LaunchedEffect(lessonId) {
        exerciseViewModel.resetExerciseProgress()
        exerciseViewModel.loadExercises(lessonId, type = "UNIQUE_CHOICE")
    }

    if (completeState is Response.Success) {
        exerciseViewModel.resetCompleteState()
        onFinished()
    }

    val exercises by exerciseViewModel.exercisesState.collectAsStateWithLifecycle()
    val currentIndex by exerciseViewModel.currentExerciseIndex.collectAsStateWithLifecycle()
    val selectedOptionId by exerciseViewModel.selectedOptionId.collectAsStateWithLifecycle()
    val feedback by exerciseViewModel.answerFeedback.collectAsStateWithLifecycle()

    val currentExercise = exercises.getOrNull(currentIndex)

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            AnimatedVisibility(
                visible = feedback != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                feedback?.let {
                    FeedbackDialog(
                        isCorrect = it.isCorrect,
                        isLoading = isLoading,
                        onContinue = {
                            if (currentIndex < exercises.size - 1) {
                                exerciseViewModel.nextExercise()
                            } else {
                                exerciseViewModel.finishLessonExercises(lessonId)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        ExerciseScreenContent(
            modifier = Modifier.padding(paddingValues),
            currentExerciseId = currentExercise?.id,
            instruction = currentExercise?.instruction,
            options = currentExercise?.options,
            currentIndex = currentIndex,
            totalExercises = exercises.size,
            selectedOptionId = selectedOptionId,
            onBackClick = { showBackDialog = true },
            onOptionSelected = { exerciseViewModel.submitAnswer(it) },
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
    currentExerciseId: String?,
    instruction: String?,
    options: List<BlockOptionDomain>?,
    currentIndex: Int,
    totalExercises: Int,
    selectedOptionId: String?,
    onBackClick: () -> Unit,
    onOptionSelected: (BlockOptionDomain) -> Unit
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
                title = "",
                onBackClick = onBackClick,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            UniqueChoice(
                currentEx = currentIndex + 1,
                totalEx = totalExercises,
                instruction = instruction,
                options = options,
                selectedOptionId = selectedOptionId,
                onOptionSelected = onOptionSelected,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
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
    ExerciseScreenContent(
        currentExerciseId = "ex_01",
        instruction = "#### ¿Qué comando utilizas en Python para mostrar texto en la consola?",
        options = listOf(
            BlockOptionDomain(id = "1", exerciseId = "ex_01", content = "console.log()", isCorrect = false),
            BlockOptionDomain(id = "2", exerciseId = "ex_01", content = "print()", isCorrect = true),
            BlockOptionDomain(id = "3", exerciseId = "ex_01", content = "echo", isCorrect = false),
            BlockOptionDomain(id = "4", exerciseId = "ex_01", content = "System.out.println()", isCorrect = false)
        ),
        currentIndex = 2,
        totalExercises = 10,
        selectedOptionId = null,
        onBackClick = {},
        onOptionSelected = {},
    )
}
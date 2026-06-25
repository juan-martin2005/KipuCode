package com.kipucode.ui.screens.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.domain.model.Response
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.screens.lesson.components.UniqueChoice
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Red
import com.kipucode.viewmodel.ExerciseViewModel

// --- SCREEN ---
@Composable
fun ExerciseScreen(
    lessonId: String,
    onBack: () -> Unit,
    onFinished: () -> Unit,
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
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
            currentExerciseId = currentExercise?.id,
            instruction = currentExercise?.instruction,
            options = currentExercise?.options,
            currentIndex = currentIndex,
            totalExercises = exercises.size,
            selectedOptionId = selectedOptionId,
            onBackClick = onBack,
            onOptionSelected = { exerciseViewModel.submitAnswer(it) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// ---  CONTENT ---
@Composable
fun ExerciseScreenContent(
    currentExerciseId: String?,
    instruction: String?,
    options: List<BlockOptionDomain>?,
    currentIndex: Int,
    totalExercises: Int,
    selectedOptionId: String?,
    onBackClick: () -> Unit,
    onOptionSelected: (BlockOptionDomain) -> Unit,
    modifier: Modifier = Modifier
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

// --- DIALOG ---
@Composable
fun FeedbackDialog(
    isCorrect: Boolean,
    isLoading: Boolean = false,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isCorrect) Green.copy(alpha = 0.1f) else Red.copy(alpha = 0.1f)
    val accentColor = if (isCorrect) Green else Red
    val iconRes = if (isCorrect) R.drawable.ic_correct else R.drawable.ic_incorrect
    val label = if (isCorrect) "¡Correcto!" else "Incorrecto"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = accentColor
            )
        }

        FilledButton(
            if (isLoading) "Guardando..." else "Continuar",
            { if (!isLoading) onContinue() }
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Exercise Screen Content")
@Composable
fun ExerciseScreenContentPreview() {
    ExerciseScreenContent(
        currentExerciseId = "ex_01",
        instruction = "¿Qué comando utilizas en Python para mostrar texto en la consola?",
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
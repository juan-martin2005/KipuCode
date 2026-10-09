package com.kipucode.ui.screens.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.domain.model.LessonBlock
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.screens.lesson.components.LessonCodeBlock
import com.kipucode.ui.screens.lesson.components.LessonConceptBlock
import com.kipucode.ui.screens.lesson.components.LessonCueBlock
import com.kipucode.ui.screens.lesson.components.LessonHeaderBlock
import com.kipucode.ui.screens.lesson.components.LessonSummaryBlock
import com.kipucode.ui.screens.lesson.components.PracticeMethodSelector
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.viewmodel.LessonViewModel

@Composable
fun LessonScreen(
    lessonId: String?,
    lessonViewModel: LessonViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToExercises: (lessonId: String, type: String) -> Unit
) {
    val lesson by lessonViewModel.lessonState.collectAsStateWithLifecycle()
    val availableMethods by lessonViewModel.availableMethods.collectAsStateWithLifecycle()
    val currentLesson = lesson

    LaunchedEffect(lessonId) {
        if (lessonId != null) {
            lessonViewModel.getLessonById(lessonId)
        }
    }

    Scaffold(
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            if (currentLesson != null) {
                LessonContent(
                    title = "Volver al Inicio",
                    blocks = currentLesson.blocks,
                    availableMethods = availableMethods,
                    onClickBack = onBack,
                    onStartPractice = { selectedMethod ->
                        onNavigateToExercises(currentLesson.id, selectedMethod.name)
                    }
                )
            } else {
                KipuTopBar(
                    title = "Volver al Inicio",
                    onBackClick = onBack,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KipuTeal)
                }
            }
        }
    }
}

@Composable
fun LessonContent(
    title: String,
    blocks: List<LessonBlock>,
    availableMethods: List<PracticeMethod>,
    onClickBack: () -> Unit,
    onStartPractice: (PracticeMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember(availableMethods) {
        mutableStateOf(
            if (availableMethods.contains(PracticeMethod.DEFAULT_FLASHCARDS)) {
                PracticeMethod.DEFAULT_FLASHCARDS
            } else {
                availableMethods.firstOrNull() ?: PracticeMethod.UNIQUE_CHOICE
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            KipuTopBar(
                title = title,
                onBackClick = onClickBack,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }

        items(blocks) { block ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                when (block) {
                    is LessonBlock.Header -> LessonHeaderBlock(block)
                    is LessonBlock.Concept -> LessonConceptBlock(block)
                    is LessonBlock.Code -> LessonCodeBlock(block)
                    is LessonBlock.Cue -> LessonCueBlock(block)
                    is LessonBlock.Summary -> LessonSummaryBlock(block)
                }
            }
        }

        if (availableMethods.isNotEmpty()) {
            item {
                PracticeMethodSelector(
                    availableMethods = availableMethods,
                    selectedMethod = selectedMethod,
                    onMethodSelected = { selectedMethod = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledButton(
                    textButton = "Comenzar práctica",
                    onClickFilledButton = {
                        onStartPractice(selectedMethod)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Lesson Content Preview")
@Composable
fun LessonContentPreview() {
    val mockBlocks = listOf(
        LessonBlock.Summary(
            items = listOf(
                ".NET divide su arquitectura en **CLR**, **BCL** y herramientas **SDK**.",
                "El **.NET Runtime** ejecuta programas existentes."
            )
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        LessonContent(
            title = "1. Introducción a Variables",
            blocks = mockBlocks,
            availableMethods = listOf(
                PracticeMethod.UNIQUE_CHOICE,
                PracticeMethod.DEFAULT_FLASHCARDS,
                PracticeMethod.FEYNMAN_FLASHCARDS
            ),
            onClickBack = {},
            onStartPractice = {}
        )
    }
}
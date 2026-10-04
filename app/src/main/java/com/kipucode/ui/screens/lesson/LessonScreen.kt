package com.kipucode.ui.screens.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.ui.screens.lesson.components.ContentMarkdown
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.card.KipuDialog
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.viewmodel.LessonViewModel
import kotlinx.coroutines.delay

@Composable
fun LessonScreen(
    lessonId: String?,
    lessonViewModel: LessonViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToExercises: (lessonId: String) -> Unit
) {
    var showExerciseDialog by remember { mutableStateOf(false) }
    val isInPreview = LocalInspectionMode.current
    var isTransitionSettled by remember { mutableStateOf(isInPreview) }

    val lesson by lessonViewModel.lessonState.collectAsStateWithLifecycle()
    val currentLesson = lesson

    LaunchedEffect(lessonId) {
        if (lessonId != null) {
            lessonViewModel.getLessonById(lessonId)
        }
    }

    LaunchedEffect(Unit) {
        if (!isInPreview) {
            // Permite que la animación de navegación (300ms) se asiente de forma fluida a 60/120 FPS
            // antes de procesar el árbol pesado de Markdown en el hilo principal
            delay(320)
            isTransitionSettled = true
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
            if (isTransitionSettled && currentLesson != null) {
                LessonContent(
                    title = "Volver al Inicio",
                    content = currentLesson.content,
                    onClickBack = onBack,
                    onClickNext = {
                        showExerciseDialog = true
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

    if (showExerciseDialog && currentLesson != null) {
        KipuDialog(
            title = stringResource(R.string.enter_exercise_title),
            description = stringResource(R.string.enter_exercise_desc),
            dismissButtonText = stringResource(R.string.enter_exercise_cancel),
            confirmButtonText = stringResource(R.string.enter_exercise_confirm),
            iconRes = R.drawable.ic_quiz,
            onDismissRequest = {
                showExerciseDialog = false
            },
            onDismissClick = {
                showExerciseDialog = false
            },
            onConfirmClick = {
                showExerciseDialog = false
                onNavigateToExercises(currentLesson.id)
            },
            iconTint = KipuTeal
        )
    }
}

@Composable
fun LessonContent(
    title: String,
    content: String,
    onClickBack: () -> Unit,
    onClickNext: () -> Unit
) {
    val isInPreview = LocalInspectionMode.current
    var isButtonVisible by remember { mutableStateOf(isInPreview) }

    LaunchedEffect(content) {
        if (!isInPreview) {
            // Retardo breve (120ms) para que Markdown calcule y mida su layout en LazyColumn,
            // previniendo que el botón "Siguiente" aparezca temporalmente pegado arriba (Layout Shift)
            delay(120)
            isButtonVisible = true
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            KipuTopBar(
                title = title,
                onBackClick = onClickBack,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            ContentMarkdown(
                Modifier.padding(24.dp),
                content
            )
        }

        item {
            AnimatedVisibility(
                visible = isButtonVisible,
                enter = fadeIn(animationSpec = tween(350)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = tween(350)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledButton(
                        textButton = "Siguiente",
                        onClickFilledButton = onClickNext,
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lesson Content Preview")
@Composable
fun LessonContentPreview() {
    val mockMarkdownContent = """
        # 01 - Historia, Visión y Ecosistema de Python
        
        ---
        
        > **Objetivo de Aprendizaje:** 
        > Comprender el origen de Python, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.

        ## El Origen y el Propósito del Lenguaje     
        
        ---
        
        Python fue creado a principios de la década de 1990 por **Guido van Rossum**. A diferencia de otros lenguajes de la época que priorizaban la optimización extrema para la máquina, Python fue diseñado con una visión disruptiva: **optimizar el tiempo del desarrollador**.
        
        El lenguaje se construyó bajo la premisa de que el código se lee muchas más veces de las que se escribe. Por lo tanto, su sintaxis elimina elementos superfluos (como llaves o puntos y comas) y utiliza la **indentación obligatoria** para definir bloques de código, forzando un estilo visualmente limpio.
    """.trimIndent()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        LessonContent(
            title = "1. Introducción a Variables",
            content = mockMarkdownContent,
            onClickBack = {},
            onClickNext = {}
        )
    }
}
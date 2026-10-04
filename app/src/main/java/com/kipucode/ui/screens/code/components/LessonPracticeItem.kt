package com.kipucode.ui.screens.code.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.kipucode.R
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun LessonPracticeItem(
    lesson: LessonDomain,
    isCurrentLesson: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    exercisesFlow: Flow<List<ExerciseDomain>>,
    dueExercises: List<DueExerciseDomain>,
    onOpenPracticeOptions: (lesson: LessonDomain, exerciseType: String, totalCount: Int, dueCount: Int) -> Unit
) {
    val exercises by exercisesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val flashcards = remember(exercises) { exercises.filter { it.type == "FLASHCARD" } }
    val uniqueChoices = remember(exercises) { exercises.filter { it.type == "UNIQUE_CHOICE" } }

    val dueFlashcardsCount = remember(dueExercises, lesson.id) {
        dueExercises.count { it.lessonId == lesson.id && it.exerciseType.equals("FLASHCARD", ignoreCase = true) }
    }
    val dueUniqueChoicesCount = remember(dueExercises, lesson.id) {
        dueExercises.count { it.lessonId == lesson.id && it.exerciseType.equals("UNIQUE_CHOICE", ignoreCase = true) }
    }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "lessonArrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentLesson) Color(0xFFF9FDFE) else Color.White
        ),
        border = if (isCurrentLesson) BorderStroke(1.5.dp, KipuTeal.copy(alpha = 0.6f)) else BorderStroke(1.dp, Color(0xFFE9ECEF)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentLesson) 2.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (isCurrentLesson) KipuTeal else MoonFrost.copy(alpha = 0.6f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${lesson.orderIndex}",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCurrentLesson) Color.White else KipuTealDark,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (isCurrentLesson) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KipuTeal.copy(alpha = 0.12f),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_info),
                                    contentDescription = null,
                                    tint = KipuTealDark,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "Tu lección actual",
                                    fontFamily = Nunito,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KipuTealDark
                                )
                            }
                        }
                    }

                    Text(
                        text = lesson.title,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = KipuDarkBlue
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_filled),
                    contentDescription = null,
                    tint = Gray,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(arrowRotation)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (exercises.isEmpty()) {
                        Text(
                            text = "Cargando opciones de práctica...",
                            fontSize = 12.sp,
                            fontFamily = Nunito,
                            color = Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        if (flashcards.isNotEmpty()) {
                            CombinedExerciseTypeCard(
                                title = "Tarjetas de Memoria",
                                description = "Repaso activo espaciado para consolidar conceptos en memoria.",
                                totalCount = flashcards.size,
                                iconRes = R.drawable.ic_flash_card,
                                accentColor = KipuTealDark,
                                containerColor = Color(0xFFF6F8FB),
                                exerciseType = "FLASHCARD",
                                onClick = {
                                    onOpenPracticeOptions(
                                        lesson,
                                        "FLASHCARD",
                                        flashcards.size,
                                        dueFlashcardsCount
                                    )
                                }
                            )
                        }

                        if (uniqueChoices.isNotEmpty()) {
                            CombinedExerciseTypeCard(
                                title = "Opción Múltiple",
                                description = "Preguntas prácticas y autoevaluación teórica de la lección.",
                                totalCount = uniqueChoices.size,
                                iconRes = R.drawable.ic_quiz,
                                accentColor = KipuTealDark,
                                containerColor = Color(0xFFF6F8FB),
                                exerciseType = "UNIQUE_CHOICE",
                                onClick = {
                                    onOpenPracticeOptions(
                                        lesson,
                                        "UNIQUE_CHOICE",
                                        uniqueChoices.size,
                                        dueUniqueChoicesCount
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lección Actual (En Curso)")
@Composable
fun LessonPracticeItemCurrentPreview() {
    val mockLesson = LessonDomain(id = "l1", title = "Variables y Tipos Primitivos", orderIndex = 2)
    val mockExercises = listOf(
        ExerciseDomain(id = "e1", lessonId = "l1", type = "FLASHCARD", instruction = "", orderIndex = 1),
        ExerciseDomain(id = "e2", lessonId = "l1", type = "UNIQUE_CHOICE", instruction = "", orderIndex = 2)
    )

    Box(modifier = Modifier.padding(16.dp)) {
        LessonPracticeItem(
            lesson = mockLesson,
            isCurrentLesson = true,
            isExpanded = true,
            onToggleExpand = {},
            exercisesFlow = flowOf(mockExercises),
            dueExercises = listOf(
                DueExerciseDomain(
                    exerciseId = "e1", lessonId = "l1", exerciseType = "FLASHCARD",
                    lessonTitle = "XDDD",
                    dueDate = 188128312,
                    retentionPercentage = 80,
                    stability = 7.732901732766994
                )
            ),
            onOpenPracticeOptions = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Lección Completada (Acoplada)")
@Composable
fun LessonPracticeItemCompletedPreview() {
    val mockLesson = LessonDomain(id = "l0", title = "Introducción y Configuración", orderIndex = 1)
    val mockExercises = listOf(
        ExerciseDomain(id = "e0", lessonId = "l0", type = "FLASHCARD", instruction = "", orderIndex = 1)
    )

    Box(modifier = Modifier.padding(16.dp)) {
        LessonPracticeItem(
            lesson = mockLesson,
            isCurrentLesson = false,
            isExpanded = false,
            onToggleExpand = {},
            exercisesFlow = flowOf(mockExercises),
            dueExercises = emptyList(),
            onOpenPracticeOptions = { _, _, _, _ -> }
        )
    }
}



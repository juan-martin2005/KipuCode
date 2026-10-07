package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.theme.KipuCyan
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.PracticeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun DirectLessonItem(
    lesson: LessonDomain,
    isCurrentLesson: Boolean,
    isLessonPracticed: Boolean,
    exercisesFlow: Flow<List<ExerciseDomain>>,
    dueExercises: List<DueExerciseDomain>,
    selectedFilter: PracticeFilter,
    onOpenPracticeOptions: (lesson: LessonDomain, exerciseType: String, totalCount: Int, dueCount: Int) -> Unit,
    modifier: Modifier = Modifier
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

    val isFlashcardPracticed = dueFlashcardsCount > 0 || (isLessonPracticed && flashcards.isNotEmpty())
    val isUniqueChoicePracticed = dueUniqueChoicesCount > 0 || (isLessonPracticed && uniqueChoices.isNotEmpty())

    // Determinamos si se debe mostrar cada tarjeta según el filtro seleccionado
    val showFlashcard = when (selectedFilter) {
        PracticeFilter.ALL -> flashcards.isNotEmpty()
        PracticeFilter.FOR_REVIEW -> dueFlashcardsCount > 0
        PracticeFilter.UP_TO_DATE -> dueFlashcardsCount == 0 && isFlashcardPracticed && flashcards.isNotEmpty()
    }

    val showUniqueChoice = when (selectedFilter) {
        PracticeFilter.ALL -> uniqueChoices.isNotEmpty()
        PracticeFilter.FOR_REVIEW -> dueUniqueChoicesCount > 0
        PracticeFilter.UP_TO_DATE -> dueUniqueChoicesCount == 0 && isUniqueChoicePracticed && uniqueChoices.isNotEmpty()
    }

    // Si con el filtro actual la lección no tiene ejercicios para mostrar, no renderizamos
    if (!showFlashcard && !showUniqueChoice && exercises.isNotEmpty()) {
        return
    }

    val formattedIndex = lesson.orderIndex.toString().padStart(2, '0')

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- CABECERA DE LECCIÓN ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de la lección (ej: 01, 02, 03)
            Text(
                text = formattedIndex,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = KipuTeal
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Título de la lección
            Text(
                text = lesson.title,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = KipuDarkBlue,
                modifier = Modifier.weight(1f)
            )

            // Insignia "Actual" si es la lección en curso del usuario
            if (isCurrentLesson) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F7FA)
                ) {
                    Text(
                        text = "Actual",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = KipuTealDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // --- TARJETAS DE PRÁCTICA DIRECTA ---
        if (exercises.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cargando ejercicios...",
                    fontSize = 12.sp,
                    fontFamily = Nunito,
                    color = Color.Gray,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            if (showFlashcard) {
                DirectPracticeCard(
                    type = DirectPracticeType.FLASHCARD,
                    totalCount = flashcards.size,
                    dueCount = dueFlashcardsCount,
                    isPracticed = isFlashcardPracticed,
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

            if (showFlashcard && showUniqueChoice) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF0F3F6)
                )
            }

            if (showUniqueChoice) {
                DirectPracticeCard(
                    type = DirectPracticeType.UNIQUE_CHOICE,
                    totalCount = uniqueChoices.size,
                    dueCount = dueUniqueChoicesCount,
                    isPracticed = isUniqueChoicePracticed,
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

            if (showFlashcard || showUniqueChoice) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF0F3F6),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview(showBackground = true, name = "Direct Lesson Item Preview")
@Composable
private fun DirectLessonItemPreview() {
    val mockLesson = LessonDomain(id = "l1", title = "Tipos primitivos", orderIndex = 1)
    val mockExercises = listOf(
        ExerciseDomain(id = "e1", lessonId = "l1", type = "FLASHCARD", instruction = "", orderIndex = 1),
        ExerciseDomain(id = "e2", lessonId = "l1", type = "UNIQUE_CHOICE", instruction = "", orderIndex = 2)
    )

    Column(modifier = Modifier.padding(16.dp)) {
        DirectLessonItem(
            lesson = mockLesson,
            isCurrentLesson = true,
            isLessonPracticed = true,
            exercisesFlow = flowOf(mockExercises),
            dueExercises = listOf(
                DueExerciseDomain(
                    exerciseId = "e1",
                    lessonId = "l1",
                    exerciseType = "FLASHCARD",
                    lessonTitle = "Tipos primitivos",
                    dueDate = 188128312,
                    retentionPercentage = 80,
                    stability = 7.732901732766994
                )
            ),
            selectedFilter = PracticeFilter.ALL,
            onOpenPracticeOptions = { _, _, _, _ -> }
        )
    }
}

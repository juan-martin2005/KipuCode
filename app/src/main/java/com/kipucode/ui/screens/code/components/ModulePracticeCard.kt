package com.kipucode.ui.screens.code.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.kipucode.R
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.ModuleItemStatus
import com.kipucode.viewmodel.ModuleItemUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ModulePracticeCard(
    moduleItem: ModuleItemUiModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    currentLessonId: String,
    dueExercises: List<DueExerciseDomain>,
    getExercisesForLesson: (String) -> Flow<List<ExerciseDomain>>,
    onOpenPracticeOptions: (lesson: LessonDomain, exerciseType: String, totalCount: Int, dueCount: Int) -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "arrowRotation"
    )

    val isLocked = moduleItem.status == ModuleItemStatus.NEXT_LOCKED

    val cardBorder = when (moduleItem.status) {
        ModuleItemStatus.CURRENT -> BorderStroke(2.dp, KipuTeal)
        ModuleItemStatus.COMPLETED -> BorderStroke(1.dp, Color(0xFFE0E0E0))
        ModuleItemStatus.NEXT_LOCKED -> BorderStroke(1.dp, Color(0xFFDDE2E7))
    }

    val cardBgColor = when (moduleItem.status) {
        ModuleItemStatus.NEXT_LOCKED -> Color(0xFFF8F9FA)
        else -> Color.White
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLocked, onClick = onToggleExpand),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            border = cardBorder,
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (moduleItem.status == ModuleItemStatus.CURRENT) 3.dp else 1.dp
            )
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            when (moduleItem.status) {
                                ModuleItemStatus.CURRENT -> KipuTeal.copy(alpha = 0.15f)
                                ModuleItemStatus.COMPLETED -> Color(0xFFE8F5E9)
                                ModuleItemStatus.NEXT_LOCKED -> Color(0xFFECEFF1)
                            },
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (moduleItem.status) {
                        ModuleItemStatus.CURRENT -> {
                            Text(
                                text = "${moduleItem.course.orderIndex}",
                                fontFamily = Nunito,
                                fontWeight = FontWeight.ExtraBold,
                                color = KipuTeal,
                                fontSize = 16.sp
                            )
                        }
                        ModuleItemStatus.COMPLETED -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        ModuleItemStatus.NEXT_LOCKED -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = null,
                                tint = Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Módulo ${moduleItem.course.orderIndex}".uppercase(),
                            fontSize = 11.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (moduleItem.status) {
                                ModuleItemStatus.CURRENT -> KipuTeal
                                ModuleItemStatus.COMPLETED -> Color(0xFF2E7D32)
                                ModuleItemStatus.NEXT_LOCKED -> Gray
                            }
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (moduleItem.status) {
                                ModuleItemStatus.CURRENT -> KipuTeal
                                ModuleItemStatus.COMPLETED -> Color(0xFFE8F5E9)
                                ModuleItemStatus.NEXT_LOCKED -> Color(0xFFECEFF1)
                            }
                        ) {
                            Text(
                                text = when (moduleItem.status) {
                                    ModuleItemStatus.CURRENT -> "En curso"
                                    ModuleItemStatus.COMPLETED -> "Completado"
                                    ModuleItemStatus.NEXT_LOCKED -> "Bloqueado"
                                },
                                fontSize = 10.sp,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                color = when (moduleItem.status) {
                                    ModuleItemStatus.CURRENT -> Color.White
                                    ModuleItemStatus.COMPLETED -> Color(0xFF2E7D32)
                                    ModuleItemStatus.NEXT_LOCKED -> Gray
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = moduleItem.course.title,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (isLocked) Gray else KipuDarkBlue
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (moduleItem.status != ModuleItemStatus.NEXT_LOCKED) {
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                moduleItem.masteryPercentage >= 80 -> Color(0xFFE8F5E9)
                                moduleItem.masteryPercentage >= 40 -> Color(0xFFFFF3E0)
                                else -> Color(0xFFF0F4F8)
                            }
                        ) {
                            Text(
                                text = "Dominio ${moduleItem.masteryPercentage}%",
                                fontSize = 11.sp,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    moduleItem.masteryPercentage >= 80 -> Color(0xFF2E7D32)
                                    moduleItem.masteryPercentage >= 40 -> Color(0xFFE65100)
                                    else -> Gray
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_filled),
                            contentDescription = null,
                            tint = Gray,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(arrowRotation)
                        )
                    }
                }
            }

            if (isLocked && moduleItem.lockMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFECEFF1).copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color(0xFFCFD8DC))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = null,
                            tint = Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = moduleItem.lockMessage,
                            fontFamily = Nunito,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = isExpanded && !isLocked,
        enter = slideInVertically(
            initialOffsetY = { -it / 2 }
        ) + expandVertically() + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it / 2 }
        ) + shrinkVertically() + fadeOut()
    ) {
        var expandedLessonId by remember(currentLessonId) {
            mutableStateOf<String?>(currentLessonId.ifEmpty { null })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            moduleItem.lessons.forEach { lesson ->
                val isCurrentLesson = lesson.id == currentLessonId
                val isLessonExpanded = expandedLessonId == lesson.id

                LessonPracticeItem(
                    lesson = lesson,
                    isCurrentLesson = isCurrentLesson,
                    isExpanded = isLessonExpanded,
                    onToggleExpand = {
                        expandedLessonId = if (isLessonExpanded) null else lesson.id
                    },
                    exercisesFlow = getExercisesForLesson(lesson.id),
                    dueExercises = dueExercises,
                    onOpenPracticeOptions = onOpenPracticeOptions
                )
            }
        }
    }
}
}

@Preview(showBackground = true, name = "Módulo Actual (Expandido)")
@Composable
fun ModulePracticeCardCurrentPreview() {
    val mockCourse = CourseDomain(id = "c2", title = "Variables y Operadores", orderIndex = 2)
    val mockLessons = listOf(
        LessonDomain(id = "l1", courseId = "c2", title = "Tipos Primitivos", orderIndex = 1)
    )
    val moduleItem = ModuleItemUiModel(
        course = mockCourse,
        lessons = mockLessons,
        status = ModuleItemStatus.CURRENT,
        masteryPercentage = 60
    )

    Box(modifier = Modifier.padding(16.dp)) {
        ModulePracticeCard(
            moduleItem = moduleItem,
            isExpanded = true,
            onToggleExpand = {},
            currentLessonId = "l1",
            dueExercises = emptyList(),
            getExercisesForLesson = { flowOf(emptyList()) },
            onOpenPracticeOptions = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Módulo Completado")
@Composable
fun ModulePracticeCardCompletedPreview() {
    val mockCourse = CourseDomain(id = "c1", title = "Introducción a Java", orderIndex = 1)
    val moduleItem = ModuleItemUiModel(
        course = mockCourse,
        lessons = emptyList(),
        status = ModuleItemStatus.COMPLETED,
        masteryPercentage = 95
    )

    Box(modifier = Modifier.padding(16.dp)) {
        ModulePracticeCard(
            moduleItem = moduleItem,
            isExpanded = false,
            onToggleExpand = {},
            currentLessonId = "",
            dueExercises = emptyList(),
            getExercisesForLesson = { flowOf(emptyList()) },
            onOpenPracticeOptions = { _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Módulo Bloqueado")
@Composable
fun ModulePracticeCardLockedPreview() {
    val mockCourse = CourseDomain(id = "c3", title = "Estructuras de Control", orderIndex = 3)
    val moduleItem = ModuleItemUiModel(
        course = mockCourse,
        lessons = emptyList(),
        status = ModuleItemStatus.NEXT_LOCKED,
        lockMessage = "Módulo bloqueado · Completa el Módulo 2 para desbloquear el acceso a este contenido."
    )

    Box(modifier = Modifier.padding(16.dp)) {
        ModulePracticeCard(
            moduleItem = moduleItem,
            isExpanded = false,
            onToggleExpand = {},
            currentLessonId = "",
            dueExercises = emptyList(),
            getExercisesForLesson = { flowOf(emptyList()) },
            onOpenPracticeOptions = { _, _, _, _ -> }
        )
    }
}



package com.kipucode.ui.screens.code

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kipucode.R
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.navigation.ExerciseRoute
import com.kipucode.ui.screens.code.components.DirectLessonItem
import com.kipucode.ui.screens.code.components.ModuleSelectorBottomSheet
import com.kipucode.ui.screens.code.components.PracticeOptionsBottomSheet
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CodeUiState
import com.kipucode.viewmodel.CodeViewModel
import com.kipucode.viewmodel.ModuleItemStatus
import com.kipucode.viewmodel.ModuleItemUiModel
import com.kipucode.viewmodel.PracticeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private data class ActivePracticeSelection(
    val lesson: LessonDomain,
    val exerciseType: String,
    val totalCount: Int,
    val dueCount: Int
)

@Composable
fun CodeScreen(
    navController: NavController,
    codeViewModel: CodeViewModel = hiltViewModel()
) {
    val uiState by codeViewModel.uiState.collectAsStateWithLifecycle()
    var activePracticeSelection by remember { mutableStateOf<ActivePracticeSelection?>(null) }
    var isModuleSelectorOpen by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { codeViewModel.swipeToRefresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CodeContent(
                uiState = uiState,
                onOpenModuleSelector = { isModuleSelectorOpen = true },
                onSelectFilter = { codeViewModel.selectFilter(it) },
                getExercisesForLesson = { lessonId ->
                    codeViewModel.getExercisesForLesson(lessonId)
                },
                onOpenPracticeOptions = { lesson, exerciseType, totalCount, dueCount ->
                    activePracticeSelection = ActivePracticeSelection(
                        lesson = lesson,
                        exerciseType = exerciseType,
                        totalCount = totalCount,
                        dueCount = dueCount
                    )
                }
            )

            // Selector modal inferior de módulos
            if (isModuleSelectorOpen) {
                ModuleSelectorBottomSheet(
                    modules = uiState.modules,
                    selectedModuleId = uiState.selectedModuleId,
                    onSelectModule = { moduleId ->
                        codeViewModel.selectModule(moduleId)
                    },
                    onDismiss = { isModuleSelectorOpen = false }
                )
            }

            // Ventana modal para elegir opciones de práctica
            val selection = activePracticeSelection
            if (selection != null) {
                PracticeOptionsBottomSheet(
                    lessonTitle = selection.lesson.title,
                    exerciseType = selection.exerciseType,
                    totalCount = selection.totalCount,
                    dueCount = selection.dueCount,
                    onDismiss = { activePracticeSelection = null },
                    onSelectOption = { onlyDue ->
                        val currentSelection = activePracticeSelection
                        activePracticeSelection = null
                        if (currentSelection != null) {
                            codeViewModel.onSelectLesson(currentSelection.lesson.id)
                            navController.navigate(
                                ExerciseRoute(
                                    lessonId = currentSelection.lesson.id,
                                    type = currentSelection.exerciseType,
                                    onlyDue = onlyDue
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CodeContent(
    uiState: CodeUiState,
    onOpenModuleSelector: () -> Unit,
    onSelectFilter: (PracticeFilter) -> Unit,
    getExercisesForLesson: (String) -> Flow<List<ExerciseDomain>>,
    onOpenPracticeOptions: (lesson: LessonDomain, exerciseType: String, totalCount: Int, dueCount: Int) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = KipuTeal)
        }
        return
    }

    val selectedModule = uiState.modules.find { it.course.id == uiState.selectedModuleId }
        ?: uiState.modules.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- CABECERA PRINCIPAL---
        item {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tu espacio de práctica",
                fontSize = 28.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Elige una lección y empieza.",
                fontSize = 15.sp,
                fontFamily = Nunito,
                color = Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- TARJETA SELECTORA DE MÓDULO (DROPDOWN) ---
            if (selectedModule != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenModuleSelector),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF8FA)),
                    border = BorderStroke(1.dp, Color(0xFFD3EEF3)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MÓDULO ${selectedModule.course.orderIndex}".uppercase(),
                                fontFamily = Nunito,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = KipuTeal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedModule.course.title,
                                fontFamily = Nunito,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = KipuDarkBlue
                            )
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_filled),
                            contentDescription = "Cambiar módulo",
                            tint = KipuDarkBlue,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(90f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- CHIPS SEGMENTADOS DE FILTRO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PracticeFilterChip(
                    text = "Todas",
                    isSelected = uiState.selectedFilter == PracticeFilter.ALL,
                    onClick = { onSelectFilter(PracticeFilter.ALL) }
                )
                PracticeFilterChip(
                    text = "Para repasar",
                    isSelected = uiState.selectedFilter == PracticeFilter.FOR_REVIEW,
                    onClick = { onSelectFilter(PracticeFilter.FOR_REVIEW) }
                )
                PracticeFilterChip(
                    text = "Al día",
                    isSelected = uiState.selectedFilter == PracticeFilter.UP_TO_DATE,
                    onClick = { onSelectFilter(PracticeFilter.UP_TO_DATE) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFF0F3F6)
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        // --- LISTADO DE LECCIONES DEL MÓDULO SELECCIONADO ---
        if (selectedModule == null || selectedModule.lessons.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron lecciones disponibles en este módulo.",
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        color = Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(selectedModule.lessons, key = { it.id }) { lesson ->
                val isCurrent = lesson.id == uiState.currentLessonId
                val isPracticed = (uiState.masteryOverview.lessonMastery[lesson.id]?.practicedExercises ?: 0) > 0

                DirectLessonItem(
                    lesson = lesson,
                    isCurrentLesson = isCurrent,
                    isLessonPracticed = isPracticed,
                    exercisesFlow = getExercisesForLesson(lesson.id),
                    dueExercises = uiState.dueExercises,
                    selectedFilter = uiState.selectedFilter,
                    onOpenPracticeOptions = onOpenPracticeOptions
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PracticeFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) KipuTeal else Color(0xFFEFF2F5)
    val textColor = if (isSelected) Color.White else Color(0xFF4B5563)

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
        )
    }
}

@Preview(showBackground = true, name = "Pantalla Vacía")
@Composable
fun CodeScreenEmptyPreview() {
    val mockNavController = rememberNavController()
    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent(
                uiState = CodeUiState(isLoading = false),
                onOpenModuleSelector = {},
                onSelectFilter = {},
                getExercisesForLesson = { flowOf(emptyList()) },
                onOpenPracticeOptions = { _, _, _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true, name = "Pantalla con Datos - Expectative")
@Composable
fun CodeScreenWithDataPreview() {
    val mockNavController = rememberNavController()
    val mockCourse1 = CourseDomain(id = "c1", title = "Fundamentos de Java", orderIndex = 1)
    val mockCourse2 = CourseDomain(id = "c2", title = "Variables y operadores", orderIndex = 2)

    val mockLessons = listOf(
        LessonDomain(id = "l1", courseId = "c2", title = "Tipos primitivos", orderIndex = 1),
        LessonDomain(id = "l2", courseId = "c2", title = "Operadores aritméticos", orderIndex = 2),
        LessonDomain(id = "l3", courseId = "c2", title = "Operadores lógicos", orderIndex = 3)
    )

    val mockModules = listOf(
        ModuleItemUiModel(course = mockCourse1, lessons = emptyList(), status = ModuleItemStatus.COMPLETED, masteryPercentage = 100),
        ModuleItemUiModel(course = mockCourse2, lessons = mockLessons, status = ModuleItemStatus.CURRENT, masteryPercentage = 60)
    )

    val mockDue = listOf(
        DueExerciseDomain(
            exerciseId = "e1",
            lessonId = "l1",
            exerciseType = "FLASHCARD",
            retentionPercentage = 65,
            lessonTitle = "Tipos primitivos",
            stability = 8.73,
            dueDate = 188618623123
        )
    )

    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent(
                uiState = CodeUiState(
                    isLoading = false,
                    modules = mockModules,
                    selectedModuleId = "c2",
                    currentLessonId = "l1",
                    dueExercises = mockDue,
                    selectedFilter = PracticeFilter.ALL
                ),
                onOpenModuleSelector = {},
                onSelectFilter = {},
                getExercisesForLesson = { flowOf(emptyList()) },
                onOpenPracticeOptions = { _, _, _, _ -> }
            )
        }
    }
}
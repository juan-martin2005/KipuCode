package com.kipucode.ui.screens.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.navigation.ExerciseRoute
import com.kipucode.ui.screens.code.components.GlobalMemoryStatusBanner
import com.kipucode.ui.screens.code.components.ModulePracticeCard
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

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
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

            // Ventana flotante (ModalBottomSheet) para elegir el modo de práctica
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

    var expandedModuleIds by remember(uiState.modules) {
        mutableStateOf(
            uiState.modules
                .filter { it.status == ModuleItemStatus.CURRENT }
                .map { it.course.id }
                .toSet()
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // --- CABECERA PRINCIPAL ---
        item {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Centro de Práctica",
                fontSize = 28.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Explora los módulos de tu ruta y consolida tu aprendizaje lección por lección.",
                fontSize = 14.sp,
                fontFamily = Nunito,
                color = Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

        }

        // --- LISTADO DE MÓDULOS ---
        if (uiState.modules.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron módulos disponibles en tu ruta actual.",
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        color = Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(uiState.modules, key = { it.course.id }) { moduleItem ->
                val isExpanded = expandedModuleIds.contains(moduleItem.course.id)

                ModulePracticeCard(
                    moduleItem = moduleItem,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedModuleIds = if (isExpanded) {
                            expandedModuleIds - moduleItem.course.id
                        } else {
                            expandedModuleIds + moduleItem.course.id
                        }
                    },
                    currentLessonId = uiState.currentLessonId,
                    dueExercises = uiState.dueExercises,
                    getExercisesForLesson = getExercisesForLesson,
                    onOpenPracticeOptions = onOpenPracticeOptions
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Pantalla Vacía")
@Composable
fun CodeScreenEmptyPreview() {
    val mockNavController = rememberNavController()
    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent(
                uiState = CodeUiState(isLoading = false),
                getExercisesForLesson = { flowOf(emptyList()) },
                onOpenPracticeOptions = { _, _, _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true, name = "Pantalla con Datos")
@Composable
fun CodeScreenWithDataPreview() {
    val mockNavController = rememberNavController()
    val mockCourse1 = CourseDomain(id = "c1", title = "Fundamentos de Java", orderIndex = 1)
    val mockCourse2 = CourseDomain(id = "c2", title = "Variables y Operadores", orderIndex = 2)
    val mockCourse3 = CourseDomain(id = "c3", title = "Estructuras de Control", orderIndex = 3)

    val mockLessons = listOf(
        LessonDomain(id = "l1", courseId = "c2", title = "Tipos Primitivos", orderIndex = 1),
        LessonDomain(id = "l2", courseId = "c2", title = "Operadores Aritméticos", orderIndex = 2)
    )

    val mockModules = listOf(
        ModuleItemUiModel(course = mockCourse1, lessons = emptyList(), status = ModuleItemStatus.COMPLETED, masteryPercentage = 100),
        ModuleItemUiModel(course = mockCourse2, lessons = mockLessons, status = ModuleItemStatus.CURRENT, masteryPercentage = 60),
        ModuleItemUiModel(
            course = mockCourse3,
            lessons = emptyList(),
            status = ModuleItemStatus.NEXT_LOCKED,
            lockMessage = "Módulo bloqueado · Completa el Módulo 2 para desbloquear el acceso a este contenido."
        )
    )

    val mockDue = listOf(
        DueExerciseDomain(exerciseId = "e1",
            lessonId = "l1",
            exerciseType = "FLASHCARD",
            retentionPercentage = 65,
            lessonTitle = "",
            stability = 8.732901732766994,
            dueDate = 188618623123
        )
    )

    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent(
                uiState = CodeUiState(
                    isLoading = false,
                    modules = mockModules,
                    currentLessonId = "l1",
                    dueExercises = mockDue
                ),
                getExercisesForLesson = { flowOf(emptyList()) },
                onOpenPracticeOptions = { _, _, _, _ -> }
            )
        }
    }
}
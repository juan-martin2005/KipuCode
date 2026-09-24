package com.kipucode.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kipucode.R
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.card.HomeCard
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun ExploreScreen(
    userViewModel: UserViewModel,
    courseViewModel: CoursesViewModel = hiltViewModel(),
    navController: NavController,
    onNavigateToCode: (String) -> Unit
) {
    val userProgress by userViewModel.userProgressState.collectAsStateWithLifecycle()
    val coursesWithLessons by courseViewModel.coursesWithLessonsState.collectAsStateWithLifecycle()

    val progressData = userProgress

    // Buscar el curso que contiene la lección actual para saber cuál es el track activo
    val activeCourseWithLessons = coursesWithLessons.find { courseItem ->
        courseItem.lessons.any { it.id == progressData?.currentLessonId }
    }

    val activeTrack = activeCourseWithLessons?.course?.track

    // Filtrar para que solo se consideren los cursos del track actual
    val filteredCoursesWithLessons = remember(coursesWithLessons, activeTrack) {
        if (activeTrack != null) {
            coursesWithLessons.filter { it.course.track == activeTrack }
        } else {
            coursesWithLessons
        }
    }

    val activeCourseId = activeCourseWithLessons?.course?.id

    val currentLessonOrderIndex = activeCourseWithLessons?.lessons?.find {
        it.id == progressData?.currentLessonId
    }?.orderIndex ?: 0

    val completedCourses = progressData?.completedCourses ?: emptyList()

    var selectedCourseWithLessons by remember { mutableStateOf<CourseWithLessonsDomain?>(null) }


    Scaffold(
        bottomBar = {
            KipuBottomBar(navController = navController)
            },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            val selectedCourse = selectedCourseWithLessons

            if (selectedCourse != null) {
                val course = selectedCourse.course
                val lessons = selectedCourse.lessons.sortedBy { it.orderIndex }

                ModuleExercisesContent(
                    courseTitle = course.title,
                    courseNumber = course.orderIndex,
                    lessons = lessons,
                    courseViewModel = courseViewModel,
                    onBackClick = { selectedCourseWithLessons = null },
                    onExerciseTypeClick = { lessonId, type ->
                        navController.navigate("exercise?lessonId=$lessonId&type=$type")
                    }
                )
            } else {
                ExploreContent(
                    coursesWithLessons = filteredCoursesWithLessons,
                    activeCourseId = activeCourseId,
                    completedCourses = completedCourses,
                    activeCourseCurrentLessons = currentLessonOrderIndex,
                    onCourseClick = { selectedCourseWithLessons = it }
                )
            }
        }
    }
}

// ============================================================================================
//  NUEVO DISEÑO: LISTADO DE LECCIONES CON CARDS DE TIPOS DE EJERCICIOS
// ============================================================================================
@Composable
fun ModuleExercisesContent(
    courseTitle: String,
    courseNumber: Int,
    lessons: List<LessonDomain>,
    courseViewModel: CoursesViewModel,
    onBackClick: () -> Unit,
    onExerciseTypeClick: (lessonId: String, type: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            KipuTopBar(
                title = stringResource(id = R.string.back_to_modules),
                onBackClick = onBackClick,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = "Módulo $courseNumber".uppercase(),
                fontSize = 13.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                color = KipuTealDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = courseTitle,
                fontSize = 24.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecciona el tipo de práctica interactiva que deseas realizar en cada lección.",
                fontSize = 14.sp,
                fontFamily = Nunito,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(lessons, key = { it.id }) { lesson ->
            LessonExerciseSection(
                lesson = lesson,
                courseViewModel = courseViewModel,
                onExerciseTypeClick = onExerciseTypeClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LessonExerciseSection(
    lesson: LessonDomain,
    courseViewModel: CoursesViewModel,
    onExerciseTypeClick: (lessonId: String, type: String) -> Unit
) {
    val exercises by courseViewModel.getExercisesForLesson(lesson.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val flashcardsCount = remember(exercises) { exercises.count { it.type == "FLASHCARD" } }
    val uniqueChoiceCount = remember(exercises) { exercises.count { it.type == "UNIQUE_CHOICE" } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MoonFrost.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${lesson.orderIndex}",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        color = KipuTealDark,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = lesson.title,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = KipuDarkBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (exercises.isEmpty()) {
                Text(
                    text = "Cargando ejercicios...",
                    fontSize = 13.sp,
                    fontFamily = Nunito,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (flashcardsCount > 0) {
                        ExerciseTypeCard(
                            title = "Tarjetas de Memoria",
                            description = "$flashcardsCount tarjetas interactivas de repaso activo (FSRS)",
                            iconRes = R.drawable.ic_terminal_rounded,
                            containerColor = MoonFrost.copy(alpha = 0.35f),
                            accentColor = KipuTealDark,
                            onClick = { onExerciseTypeClick(lesson.id, "FLASHCARD") }
                        )
                    }

                    if (uniqueChoiceCount > 0) {
                        ExerciseTypeCard(
                            title = "Preguntas de Opción Múltiple",
                            description = "$uniqueChoiceCount preguntas de comprensión teórica",
                            iconRes = R.drawable.ic_quiz,
                            containerColor = Color(0xFFF6F8FB),
                            accentColor = KipuTeal,
                            onClick = { onExerciseTypeClick(lesson.id, "UNIQUE_CHOICE") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseTypeCard(
    title: String,
    description: String,
    iconRes: Int,
    containerColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = KipuDarkBlue
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontFamily = Nunito,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_bent_arrow_right),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ExploreContent(
    coursesWithLessons: List<CourseWithLessonsDomain>,
    activeCourseId: String?,
    completedCourses: List<String>,
    activeCourseCurrentLessons: Int,
    onCourseClick: (CourseWithLessonsDomain) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        if (coursesWithLessons.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KipuTeal)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))

                    // --- SECCIÓN 1: TÍTULO ---
                    Text(
                        text = stringResource(id = R.string.modules),
                        fontSize = 28.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        color = KipuDarkBlue
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- LISTA DE CURSOS ---
                items(coursesWithLessons) { item ->
                    val course = item.course
                    val totalLessons = item.lessons.size

                    val isActiveCourse = course.id == activeCourseId
                    val isCompletedCourse = completedCourses.contains(course.id)

                    val current = when {
                        isCompletedCourse -> totalLessons
                        isActiveCourse -> (activeCourseCurrentLessons - 1).coerceAtLeast(0)
                        else -> 0
                    }

                    HomeCard(
                        courseName = course.title,
                        currentLessons = current,
                        totalLessons = totalLessons,
                        courseNumber = course.orderIndex,
                        modifier = Modifier.clickable { onCourseClick(item) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Explore")
@Composable
fun ExplorePreview() {
    MaterialTheme {
        val mockLessonsModule1 = listOf(
            LessonDomain(id = "l1", title = "Lección 1", orderIndex = 1),
            LessonDomain(id = "l2", title = "Lección 2", orderIndex = 2)
        )
        val mockLessonsModule2 = listOf(
            LessonDomain(id = "l3", title = "Lección 1", orderIndex = 1),
            LessonDomain(id = "l4", title = "Lección 2", orderIndex = 2),
            LessonDomain(id = "l5", title = "Lección 3", orderIndex = 3),
            LessonDomain(id = "l6", title = "Lección 4", orderIndex = 4)
        )
        val mockLessonsModule3 = listOf(
            LessonDomain(id = "l7", title = "Lección 1", orderIndex = 1)
        )

        val mockCoursesWithLessons = listOf(
            CourseWithLessonsDomain(
                course = CourseDomain(
                    id = "python_module_01",
                    title = "Introducción a Python",
                    orderIndex = 1
                ),
                lessons = mockLessonsModule1
            ),
            CourseWithLessonsDomain(
                course = CourseDomain(
                    id = "python_module_02",
                    title = "Variables y Tipos de Datos",
                    orderIndex = 2
                ),
                lessons = mockLessonsModule2
            ),
            CourseWithLessonsDomain(
                course = CourseDomain(
                    id = "python_module_03",
                    title = "Estructuras de Control",
                    orderIndex = 3
                ),
                lessons = mockLessonsModule3
            )
        )

        ExploreContent(
            coursesWithLessons = mockCoursesWithLessons,
            activeCourseId = "python_module_03",
            completedCourses = listOf("python_module_01", "python_module_02"),
            activeCourseCurrentLessons = 1,
            onCourseClick = {}
        )
    }
}
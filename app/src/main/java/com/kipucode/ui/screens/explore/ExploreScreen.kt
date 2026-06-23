package com.kipucode.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.card.HomeCard
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun ExploreScreen(
    userViewModel: UserViewModel,
    courseViewModel: CoursesViewModel = hiltViewModel(),
    navController: NavController
) {
    val userProgress by userViewModel.userProgressState.collectAsStateWithLifecycle()
    val coursesWithLessons by courseViewModel.coursesWithLessonsState.collectAsStateWithLifecycle()

    val progressData = userProgress

    val activeCourseWithLessons = coursesWithLessons.find { courseItem ->
        courseItem.lessons.any { it.id == progressData?.currentLessonId }
    }

    val activeCourseId = activeCourseWithLessons?.course?.id

    val currentLessonOrderIndex = activeCourseWithLessons?.lessons?.find {
        it.id == progressData?.currentLessonId
    }?.orderIndex ?: 0

    val completedCourses = progressData?.completedCourses ?: emptyList()

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            ExploreContent(
                coursesWithLessons = coursesWithLessons,
                activeCourseId = activeCourseId,
                completedCourses = completedCourses,
                activeCourseCurrentLessons = currentLessonOrderIndex
            )
        }
    }

}

@Composable
fun ExploreContent(
    coursesWithLessons: List<CourseWithLessonsDomain>,
    activeCourseId: String?,
    completedCourses: List<String>,
    activeCourseCurrentLessons: Int
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
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))

                    // --- SECCIÓN 1: TÍTULO ---
                    Text(
                        text = "Modules",
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
            activeCourseId = "python_module_02",
            completedCourses = listOf("python_module_01"),
            activeCourseCurrentLessons = 1
        )
    }
}
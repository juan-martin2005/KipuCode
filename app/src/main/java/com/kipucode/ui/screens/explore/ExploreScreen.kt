package com.kipucode.ui.screens.explore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kipucode.R
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.usecase.CognitiveMasteryOverview
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.card.HomeCard
import com.kipucode.ui.components.card.LessonCard
import com.kipucode.ui.navigation.LessonRoute
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    coursesViewModel: CoursesViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    onNavigateToLesson: (String) -> Unit = { lessonId ->
        navController.navigate(LessonRoute(lessonId = lessonId))
    }
) {
    val coursesWithLessons by coursesViewModel.coursesWithLessonsState.collectAsStateWithLifecycle()
    val masteryOverview by coursesViewModel.masteryOverviewState.collectAsStateWithLifecycle()
    val userProgress by userViewModel.userProgressState.collectAsStateWithLifecycle()

    var isRefreshing by remember { mutableStateOf(false) }
    var selectedCourseWithLessons by remember { mutableStateOf<CourseWithLessonsDomain?>(null) }

    // Intercepta el botón 'Atrás' de Android para volver a la lista de módulos si hay uno abierto
    BackHandler(enabled = selectedCourseWithLessons != null) {
        selectedCourseWithLessons = null
    }

    // Filtrar módulos correspondientes al track activo del usuario
    val activeCourse = coursesWithLessons.find { courseItem ->
        courseItem.lessons.any { it.id == userProgress?.currentLessonId }
    }
    val activeTrack = activeCourse?.course?.track

    val filteredCourses = remember(coursesWithLessons, activeTrack) {
        if (activeTrack != null) {
            coursesWithLessons.filter { it.course.track == activeTrack }
        } else {
            coursesWithLessons
        }.sortedBy { it.course.orderIndex }
    }

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coursesViewModel.swipeToRefresh()
                isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val selectedCourse = selectedCourseWithLessons

            if (selectedCourse != null) {
                // VISTA 2: LISTA DE LECCIONES DEL MÓDULO SELECCIONADO
                ModuleLessonsContent(
                    courseWithLessons = selectedCourse,
                    masteryOverview = masteryOverview,
                    completedLessons = userProgress?.completedLessons ?: emptyList(),
                    onBackClick = { selectedCourseWithLessons = null },
                    onLessonClick = { lessonId ->
                        coursesViewModel.onSelectLesson(lessonId)
                        onNavigateToLesson(lessonId)
                    }
                )
            } else {
                // VISTA 1: LISTA DE MÓDULOS
                ModulesListContent(
                    coursesWithLessons = filteredCourses,
                    masteryOverview = masteryOverview,
                    onCourseClick = { selectedCourseWithLessons = it }
                )
            }
        }
    }
}

@Composable
fun ModulesListContent(
    coursesWithLessons: List<CourseWithLessonsDomain>,
    masteryOverview: CognitiveMasteryOverview,
    onCourseClick: (CourseWithLessonsDomain) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.modules),
                fontSize = 28.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Explora la ruta de aprendizaje y accede a cada módulo para ver sus lecciones.",
                fontSize = 14.sp,
                fontFamily = Nunito,
                color = Gray,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (coursesWithLessons.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KipuTeal)
                }
            }
        } else {
            items(coursesWithLessons, key = { it.course.id }) { item ->
                val course = item.course
                val totalLessons = item.lessons.size
                val courseMastery = masteryOverview.courseMastery[course.id]

                HomeCard(
                    courseName = course.title,
                    totalLessons = totalLessons,
                    courseNumber = course.orderIndex,
                    masteryPercentage = courseMastery?.percentage,
                    statusTag = courseMastery?.statusTag,
                    modifier = Modifier.clickable { onCourseClick(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ModuleLessonsContent(
    courseWithLessons: CourseWithLessonsDomain,
    masteryOverview: CognitiveMasteryOverview,
    completedLessons: List<String>,
    onBackClick: () -> Unit,
    onLessonClick: (String) -> Unit
) {
    val course = courseWithLessons.course
    val lessons = remember(courseWithLessons) { courseWithLessons.lessons.sortedBy { it.orderIndex } }
    val courseMastery = masteryOverview.courseMastery[course.id]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            KipuTopBar(
                title = stringResource(id = R.string.back_to_modules),
                onBackClick = onBackClick,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )

            HomeCard(
                courseName = course.title,
                totalLessons = lessons.size,
                courseNumber = course.orderIndex,
                masteryPercentage = courseMastery?.percentage,
                statusTag = courseMastery?.statusTag,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                text = stringResource(id = R.string.learning_journey),
                fontSize = 20.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        itemsIndexed(lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
            val isCompleted = lesson.id in completedLessons
            val lessonMastery = masteryOverview.lessonMastery[lesson.id]?.percentage

            LessonCard(
                lessonId = lesson.id,
                title = lesson.title,
                isCompleted = isCompleted,
                isLocked = false,
                masteryPercentage = lessonMastery,
                onLessonClick = onLessonClick
            )

            if (index < lessons.size - 1) {
                Box(modifier = Modifier.padding(start = 28.dp)) {
                    Spacer(
                        modifier = Modifier
                            .width(2.dp)
                            .height(16.dp)
                            .background(KipuTeal)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenModulesPreview() {
    val mockCourse = CourseDomain(id = "c1", title = "Fundamentos de C#", orderIndex = 1)
    val mockLessons = listOf(
        LessonDomain(id = "l1", courseId = "c1", title = "Variables y Tipos", orderIndex = 1),
        LessonDomain(id = "l2", courseId = "c1", title = "Condicionales", orderIndex = 2)
    )
    val mockList = listOf(CourseWithLessonsDomain(mockCourse, mockLessons))

    ModulesListContent(
        coursesWithLessons = mockList,
        masteryOverview = CognitiveMasteryOverview(),
        onCourseClick = {}
    )
}
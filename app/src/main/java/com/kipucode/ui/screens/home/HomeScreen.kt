package com.kipucode.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kipucode.R
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.Response
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.card.HeadlineHome
import com.kipucode.ui.components.card.HomeCard
import com.kipucode.ui.components.card.LessonCard
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.LightGray
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    courseViewModel: CoursesViewModel = hiltViewModel(),
    navController: NavController,
    onNavigateToCode: (String) -> Unit
) {
    val userProfile by userViewModel.userProfileState.collectAsStateWithLifecycle()
    val userProgress by userViewModel.userProgressState.collectAsStateWithLifecycle()

    val coursesWithLessons by courseViewModel.coursesWithLessonsState.collectAsStateWithLifecycle()

    val userRefreshState by userViewModel.refreshState.collectAsStateWithLifecycle()
    val courseRefreshState by courseViewModel.refreshState.collectAsStateWithLifecycle()
    val isRefreshing = userRefreshState is Response.Loading || courseRefreshState is Response.Loading

    LaunchedEffect(userRefreshState, courseRefreshState) {
        if (userRefreshState is Response.Success || userRefreshState is Response.Error) {
            userViewModel.resetRefreshState()
        }

        if (courseRefreshState is Response.Success || courseRefreshState is Response.Error) {
            courseViewModel.resetRefreshState()
        }
    }
    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    userViewModel.swipeToRefresh()
                    courseViewModel.swipeToRefresh()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundGray)
                ) {
                    val userData = userProfile
                    val progressData = userProgress

                    val activeCourseWithLessons = coursesWithLessons.find { courseItem ->
                        courseItem.lessons.any { it.id == progressData?.currentLessonId }
                    }

                    val courseData = activeCourseWithLessons?.course
                    val lessonsData = activeCourseWithLessons?.lessons?.sortedBy { it.orderIndex } ?: emptyList()

                    val currentLessonOrderIndex = lessonsData.find {
                        it.id == progressData?.currentLessonId
                    }?.orderIndex ?: 0


                    if (coursesWithLessons.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = KipuTeal)
                        }
                    } else {
                        HomeContent(
                            userName = userData?.name ?: "Test_User_Full_Name",
                            totalXp = progressData?.totalXp ?: 999,
                            streakDay =
                                getActiveStreak(
                                    progressData?.completedAt,
                                    progressData?.streakDay ?: 999),

                            courseTitle = courseData?.title ?: "Test_Course_Title",
                            lessons = lessonsData,
                            currentLessonOrderIndex = currentLessonOrderIndex,
                            onLessonClick = onNavigateToCode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    userName: String,
    totalXp: Int,
    streakDay: Int,

    courseTitle: String,
    lessons: List<LessonDomain>,
    currentLessonOrderIndex: Int,
    onLessonClick : (String) -> Unit
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // --- SECCIÓN 1: TOP BAR ---
        item {
            HeadlineHome(
                userName = userName,
                userXp = totalXp,
                userStreak = streakDay,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // --- SECCIÓN 2: TARJETA DE PROGRESO ---
        item {
            HomeCard(
                courseName = courseTitle,
                currentLessons = (currentLessonOrderIndex - 1).coerceAtLeast(0),
                totalLessons = lessons.size,
                iconResId = R.drawable.ic_python,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // --- SECCIÓN 3: TÍTULO SEPARADOR ---
        item {
            Text(
                text = "Learning Journey",
                fontSize = 20.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // --- SECCIÓN 4: LISTA DE LECCIONES ---
        itemsIndexed(lessons) { index, lesson ->

            val isCompleted = lesson.orderIndex < currentLessonOrderIndex
            val isLocked = lesson.orderIndex > currentLessonOrderIndex

            LessonCard(
                lessonId = lesson.id,
                title = lesson.title,
                isCompleted = isCompleted,
                isLocked = isLocked,
                onLessonClick = onLessonClick
            )

            if (index < lessons.size - 1) {
                val nextIsLocked = lessons[index + 1].orderIndex > currentLessonOrderIndex
                val lineColor = if (nextIsLocked) LightGray else KipuTeal

                Box(modifier = Modifier.padding(start = 28.dp)) {
                    Spacer(
                        modifier = Modifier
                            .width(2.dp)
                            .height(16.dp)
                            .background(lineColor)
                    )
                }
            }
        }
    }
}

fun getActiveStreak(lastCompletedMillis: Long?, databaseStreak: Int): Int {
    if (lastCompletedMillis == null) return 0

    val lastDate = Instant.ofEpochMilli(lastCompletedMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val todayDate = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)

    return if (daysBetween > 1L) 0 else databaseStreak
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val mockDomainLessons = listOf(
        LessonDomain(
            id = "1",
            courseId = "1",
            title = "1. Logic and Algorithms",
            content = "",
            points = 10,
            exp = 10,
            orderIndex = 1
        ),
        LessonDomain(
            id = "2",
            courseId = "1",
            title = "2. Variables and Data Types",
            content = "",
            points = 10,
            exp = 10,
            orderIndex = 2
        ),
        LessonDomain(
            id = "3",
            courseId = "1",
            title = "3. Control Structures",
            content = "",
            points = 10,
            exp = 10,
            orderIndex = 3
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
    ) {
        HomeContent(
            userName = "Test_User_Full_Name",
            totalXp = 999,
            streakDay = 999,
            courseTitle = "Introduction to Python Programing",
            lessons = mockDomainLessons,
            currentLessonOrderIndex = 2,
            onLessonClick = {}
        )
    }
}
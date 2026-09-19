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
import androidx.compose.runtime.getValue
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
import com.kipucode.R
import com.kipucode.domain.model.LessonDomain
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.card.HeadlineHome
import com.kipucode.ui.components.card.HomeCard
import com.kipucode.ui.components.card.LessonCard
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.LightGray
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    onNavigateToCode: (String) -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

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
                isRefreshing = uiState.isRefreshing,
                onRefresh = { homeViewModel.swipeToRefresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = KipuTeal)
                    }
                } else {
                    HomeContent(
                        userName = uiState.userName,
                        totalXp = uiState.totalXp,
                        streakDay = uiState.streakDay,
                        sectionTitle = stringResource(id = R.string.learning_journey),
                        courseTitle = uiState.courseTitle,
                        courseNumber = uiState.courseNumber,
                        currentLessonsProgress = uiState.currentLessonsProgress,
                        totalLessons = uiState.totalLessons,
                        lessons = uiState.lessons,

                        isLessonCompleted = { lesson -> lesson.id in uiState.completedLessonIds },
                        isLessonLocked = { lesson -> lesson.orderIndex > uiState.currentLessonOrderIndex },

                        onLessonClick = onNavigateToCode
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    userName: String? = null,
    totalXp: Int? = null,
    streakDay: Int? = null,

    onBackClick: (() -> Unit)? = null,

    sectionTitle: String = stringResource(id = R.string.learning_journey),
    courseTitle: String,
    courseNumber: Int = 0,
    currentLessonsProgress: Int,
    totalLessons: Int,
    lessons: List<LessonDomain>,

    isLessonCompleted: (LessonDomain) -> Boolean,
    isLessonLocked: (LessonDomain) -> Boolean,

    onLessonClick : (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // --- SECCIÓN 1: HEADER CONDICIONAL ---
        item {
            if (userName != null && totalXp != null && streakDay != null) {
                HeadlineHome(
                    userName = userName,
                    userXp = totalXp,
                    userStreak = streakDay,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else if (onBackClick != null) {
                KipuTopBar(
                    title = stringResource(id = R.string.back_to_modules),
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        // --- SECCIÓN 2: TARJETA DE PROGRESO ---
        item {
            HomeCard(
                courseName = courseTitle,
                currentLessons = currentLessonsProgress,
                totalLessons = totalLessons,
                courseNumber = courseNumber,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // --- SECCIÓN 3: TÍTULO SEPARADOR ---
        item {
            Text(
                text = sectionTitle,
                fontSize = 20.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // --- SECCIÓN 4: LISTA DE LECCIONES ---
        itemsIndexed(lessons) { index, lesson ->

            val isCompleted = isLessonCompleted(lesson)
            val isLocked = isLessonLocked(lesson)

            LessonCard(
                lessonId = lesson.id,
                title = lesson.title,
                isCompleted = isCompleted,
                isLocked = isLocked,
                onLessonClick = onLessonClick
            )

            if (index < lessons.size - 1) {
                val nextIsLocked = isLessonLocked(lessons[index + 1])
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


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val mockDomainLessons = listOf(
        LessonDomain(
            id = "1",
            courseId = "1",
            title = "1. Logic and Algorithms",
            content = "",
            xp = 10,
            orderIndex = 1
        ),
        LessonDomain(
            id = "2",
            courseId = "1",
            title = "2. Variables and Data Types",
            content = "",
            xp = 10,
            orderIndex = 2
        ),
        LessonDomain(
            id = "3",
            courseId = "1",
            title = "3. Control Structures",
            content = "",
            xp = 10,
            orderIndex = 3
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        val previewCurrentLessonOrderIndex = 3

        HomeContent(
            userName = "Test_User_Full_Name",
            totalXp = 999,
            streakDay = 999,
            courseTitle = "Introduction to Python Programing",

            currentLessonsProgress = previewCurrentLessonOrderIndex,
            totalLessons = mockDomainLessons.size,
            lessons = mockDomainLessons,

            isLessonCompleted = { lesson -> lesson.orderIndex <= previewCurrentLessonOrderIndex },
            isLessonLocked = { lesson -> lesson.orderIndex > previewCurrentLessonOrderIndex },

            onLessonClick = {}
        )
    }
}
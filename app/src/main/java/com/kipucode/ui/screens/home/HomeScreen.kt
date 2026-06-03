package com.kipucode.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.Course
import com.kipucode.domain.model.Response
import com.kipucode.ui.component.card.HeadlineHome
import com.kipucode.ui.component.card.HomeCard
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.LightGray
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    courseViewModel: CoursesViewModel = hiltViewModel()
) {
    val userProfile by userViewModel.userProfile.collectAsStateWithLifecycle()
    val userProgress by userViewModel.userProgress.collectAsStateWithLifecycle()
    val refreshState by userViewModel.refreshState.collectAsStateWithLifecycle()
    val courseState by courseViewModel.courseState.collectAsStateWithLifecycle()

    val isRefreshing = refreshState is Response.Loading

    LaunchedEffect(refreshState) {
        when (refreshState) {
            is Response.Loading -> {
            }
            is Response.Success -> {
//                Toast.makeText("TEST_HOMESCREEN", "Exito", Toast.LENGTH_SHORT).show()
                userViewModel.resetRefreshState()
            }
            is Response.Error -> {
//                Toast.makeText("TEST_HOMESCREEN", "Sin conexión", Toast.LENGTH_SHORT).show()
                userViewModel.resetRefreshState()
            }
            null -> {}
        }
    }


    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { userViewModel.swipeToRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(top = 16.dp)
        ) {
            when(val result = userProgress){
                is Response.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KipuTeal)
                    }
                }
                is Response.Success -> {
                    val progress = result.data
                    HeadlineHome(
                        userName = userProfile?.name ?: "",
                        userXp = progress.totalXp,
                        userStreak = progress.streakDay,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                is Response.Error -> {
                    result.message?.let {
                        HeadlineHome(
                            userName = it,
                            userXp = 0,
                            userStreak = 0,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                null -> {
                    HeadlineHome(
                        userName = "",
                        userXp = 0,
                        userStreak = 0,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when(val course = courseState){
                is Response.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KipuTeal)
                    }
                }
                is Response.Success -> {
                    HomeContent(
                        lessons = mockLessons,
                        course = course.data[0]
                    )
                }
                is Response.Error -> {}
                null -> {}
            }
        }
    }
}
@Composable
fun LessonCard(lesson: Lesson) {

    val borderColor = if (lesson.status == LessonStatus.LOCKED) LightGray else KipuTeal
    val statusText = when(lesson.status) {
        LessonStatus.COMPLETED -> "Completed"
        LessonStatus.IN_PROGRESS -> "In progress"
        LessonStatus.LOCKED -> "Locked"
    }
    val statusColor = if (lesson.status == LessonStatus.COMPLETED) KipuTeal else Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lesson.title, fontWeight = FontWeight.Bold, color = KipuDarkBlue, fontSize = 15.sp)
                Text(text = statusText, color = statusColor, fontSize = 13.sp)
            }

            when (lesson.status) {
                LessonStatus.COMPLETED -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Completed",
                        tint = KipuTeal
                    )
                }
                LessonStatus.IN_PROGRESS -> {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { lesson.progress },
                            modifier = Modifier.size(36.dp),
                            color = KipuTeal,
                            trackColor = BorderLightGray
                        )
                        Text("${(lesson.progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                LessonStatus.LOCKED -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Locked",
                        tint = BorderLightGray
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    lessons: List<Lesson>,
    course: Course
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        // --- SECCIÓN 2: TARJETA DE PROGRESO ---
        item {
            HomeCard(
                courseName = course.title,
                currentLessons = 1,
                description = course.description,
                totalLessons = lessons.size,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- SECCIÓN 3: TÍTULO SEPARADOR ---
        item {
            Text(
                text = "Learning Journey",
                fontSize = 20.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- SECCIÓN 4: LISTA DE LECCIONES ---
        itemsIndexed(lessons) { index, lesson ->
            LessonCard(lesson = lesson)

            if (index < lessons.size - 1) {
                val nextIsLocked = lessons[index + 1].status == LessonStatus.LOCKED
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


enum class LessonStatus { COMPLETED, IN_PROGRESS, LOCKED }

data class Lesson(
    val id: Int,
    val title: String,
    val status: LessonStatus,
    val progress: Float = 0f
)

val mockLessons = listOf(
    Lesson(1, "1. Logic and Algorithms", LessonStatus.COMPLETED),
    Lesson(2, "2. Variables and Data Types", LessonStatus.COMPLETED),
    Lesson(3, "3. Control Structures", LessonStatus.IN_PROGRESS, 0.1f),
    Lesson(4, "4. Functions", LessonStatus.LOCKED),
    Lesson(5, "5. Lists and Tuples", LessonStatus.LOCKED),
    Lesson(6, "6. Dictionaries and Sets", LessonStatus.LOCKED),
    Lesson(7, "7. Object-Oriented Programming", LessonStatus.LOCKED),
)

val mockCourse = Course("1", "Introduction to Python Programing", "Learning Python to beginner", 1,10,15,1231254112)


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
            .padding(top = 16.dp)
    ) {
        HeadlineHome(
            userName = "Marcelino Mamani Palma",
            userXp = 1000,
            userStreak = 365,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeContent(
            lessons = mockLessons,
            course = mockCourse
        )
    }
}
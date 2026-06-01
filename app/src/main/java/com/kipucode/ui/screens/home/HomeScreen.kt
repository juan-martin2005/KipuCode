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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.Response
import com.kipucode.ui.component.card.HeadlineHome
import com.kipucode.ui.component.card.HomeCard
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.UserViewModel

enum class LessonStatus { COMPLETED, IN_PROGRESS, LOCKED }

data class Lesson(
    val id: Int,
    val title: String,
    val status: LessonStatus,
    val progress: Float = 0f
)

val mockLessons = listOf(
    Lesson(1, "1. Logic and Algorithms", LessonStatus.COMPLETED, 1f),
    Lesson(2, "2. Variables and Data Types", LessonStatus.COMPLETED, 1f),
    Lesson(3, "3. Control Structures", LessonStatus.IN_PROGRESS, 0.1f),
    Lesson(4, "4. Functions", LessonStatus.LOCKED),
    Lesson(5, "5. Lists and Tuples", LessonStatus.LOCKED),
    Lesson(6, "6. Dictionaries and Sets", LessonStatus.LOCKED),
    Lesson(7, "7. Object-Oriented Programming", LessonStatus.LOCKED),
)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel
) {
    val userProfile by userViewModel.userProfile.collectAsStateWithLifecycle()
    val refreshState by userViewModel.refreshState.collectAsStateWithLifecycle()

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
                .background(Color(0xFFf6f7f9))
                .padding(top = 16.dp)
        ) {
            HeadlineHome(
                userName = userProfile?.name ?: "",
                userXp = 0,
                userStreak = 0,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeContent(
                lessons = mockLessons
            )
        }
    }
}
@Composable
fun LessonCard(lesson: Lesson, kipuTeal: Color, darkBlue: Color, lightGray: Color) {

    val borderColor = if (lesson.status == LessonStatus.LOCKED) lightGray else kipuTeal
    val statusText = when(lesson.status) {
        LessonStatus.COMPLETED -> "Completed"
        LessonStatus.IN_PROGRESS -> "In progress"
        LessonStatus.LOCKED -> "Locked"
    }
    val statusColor = if (lesson.status == LessonStatus.COMPLETED) kipuTeal else Color.Gray

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
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = lesson.title, fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 15.sp)
                Text(text = statusText, color = statusColor, fontSize = 13.sp)
            }

            when (lesson.status) {
                LessonStatus.COMPLETED -> {
                    Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = "Completado", tint = kipuTeal)
                }
                LessonStatus.IN_PROGRESS -> {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { lesson.progress },
                            modifier = Modifier.size(36.dp),
                            color = kipuTeal,
                            trackColor = lightGray
                        )
                        Text("${(lesson.progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                LessonStatus.LOCKED -> {
                    Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = "Bloqueado", tint = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    lessons: List<Lesson>
) {
    val darkBlue = Color(0xFF081c40)
    val kipuTeal = Color(0xFF0293a8)
    val lightGray = Color(0xFFE5E7EB)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        // --- SECCIÓN 2: TARJETA DE PROGRESO ---
        item {
            HomeCard(
                courseName = "Introduction to Python Programming",
                currentLessons = 1,
                totalLessons = 9,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- SECCIÓN 3: TÍTULO SEPARADOR ---
        item {
            Text(
                text = "Ruta de Aprendizaje",
                fontSize = 20.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = darkBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- SECCIÓN 4: LISTA DE LECCIONES ---
        itemsIndexed(lessons) { index, lesson ->
            LessonCard(lesson = lesson, kipuTeal = kipuTeal, darkBlue = darkBlue, lightGray = lightGray)

            if (index < lessons.size - 1) {
                val nextIsLocked = lessons[index + 1].status == LessonStatus.LOCKED
                val lineColor = if (nextIsLocked) lightGray else kipuTeal

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

//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFf6f7f9))
//            .padding(top = 16.dp)
//    ) {
//        HeadlineHome(
//            userName = "Marcelino Mamani Palma",
//            userXp = 1000,
//            userStreak = 365,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        HomeContent(
//            lessons = mockLessons
//        )
//    }
//}
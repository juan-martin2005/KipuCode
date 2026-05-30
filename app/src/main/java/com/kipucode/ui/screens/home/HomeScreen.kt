package com.kipucode.ui.screens.home

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.kipucode.domain.model.Response
import com.kipucode.ui.component.card.HeadlineHome
import com.kipucode.ui.component.card.HomeCard
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.UserViewModel

enum class LessonStatus { COMPLETADO, EN_PROGRESO, BLOQUEADO }

data class Lesson(
    val id: Int,
    val title: String,
    val status: LessonStatus,
    val progress: Float = 0f
)

val mockLessons = listOf(
    Lesson(1, "1. Lógica y algoritmos", LessonStatus.COMPLETADO, 1f),
    Lesson(2, "2. Variables y tipos de datos", LessonStatus.COMPLETADO, 1f),
    Lesson(3, "3. Estructuras de control", LessonStatus.EN_PROGRESO, 0.1f),
    Lesson(4, "4. Funciones", LessonStatus.BLOQUEADO),
    Lesson(5, "4. Funciones", LessonStatus.BLOQUEADO),
    Lesson(6, "4. Funciones", LessonStatus.BLOQUEADO),
    Lesson(7, "4. Funciones", LessonStatus.BLOQUEADO),
)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    var userName by remember { mutableStateOf("") }
    var userXp by remember { mutableIntStateOf(0) }
    var userStreak by remember { mutableIntStateOf(0) }

    when (val state = userState) {
        is Response.Loading -> {}
        is Response.Success -> {
            userName = state.data.name
            userXp = state.data.totalXp
            userStreak = state.data.streakDay
        }
        is Response.Error -> {
            val errorMessage = state.message ?: "Internal Error"
            Log.d("FIREBASE_ERROR", errorMessage)
        }
        null -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
            .padding(top = 16.dp)
    ) {
        HeadlineHome(
            userName = userName,
            userXp = userXp,
            userStreak = userStreak,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeContent(
            lessons = mockLessons
        )
    }
}
@Composable
fun LessonCard(lesson: Lesson, kipuTeal: Color, darkBlue: Color, lightGray: Color) {

    val borderColor = if (lesson.status == LessonStatus.BLOQUEADO) lightGray else kipuTeal
    val statusText = when(lesson.status) {
        LessonStatus.COMPLETADO -> "Completado"
        LessonStatus.EN_PROGRESO -> "En progreso"
        LessonStatus.BLOQUEADO -> "Bloqueado"
    }
    val statusColor = if (lesson.status == LessonStatus.COMPLETADO) kipuTeal else Color.Gray

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
                LessonStatus.COMPLETADO -> {
                    Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = "Completado", tint = kipuTeal)
                }
                LessonStatus.EN_PROGRESO -> {
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
                LessonStatus.BLOQUEADO -> {
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
                courseName = "Fundamentos Básicos de Programación Python",
                currentLessons = 5,
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
                val nextIsLocked = lessons[index + 1].status == LessonStatus.BLOQUEADO
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
            lessons = mockLessons
        )
    }
}
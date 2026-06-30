package com.kipucode.ui.screens.code

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.card.MultipleChoicesCard
import com.kipucode.ui.components.card.UserProfileCard
import com.kipucode.ui.screens.code.components.LessonRowItem
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel
import kotlin.String
import kotlin.collections.flatMap

// --- MODEL ---
data class LessonUiModel(
    val id: String,
    val title: String,
    val earnedPoints: Int,
    val maxPoints: Int
)

// --- SCREEN ---
@Composable
fun CodeScreen(
    navController: NavController,
    coursesViewModel: CoursesViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val coursesWithLessons by coursesViewModel.coursesWithLessonsState.collectAsStateWithLifecycle()
    val userProgress by userViewModel.userProgressState.collectAsStateWithLifecycle()

    val lessonsUiList = remember(coursesWithLessons, userProgress) {
        coursesWithLessons.flatMap { courseWithLessons ->
            courseWithLessons.lessons.map { lesson ->
                val earnedXp = userProgress?.lessonsXpRecord?.get(lesson.id) ?: 0

                LessonUiModel(
                    id = lesson.id,
                    title = lesson.title,
                    earnedPoints = earnedXp,
                    maxPoints = lesson.exp
                )
            }
        }
    }

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CodeContent(lessons = lessonsUiList)
        }
    }
}

// --- CONTENIDO ---
@Composable
fun CodeContent(
    lessons: List<LessonUiModel>
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))

            // Título
            Text(
                text = "Ejercicios",
                fontSize = 28.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        itemsIndexed(lessons) { index, lesson ->
            LessonRowItem(
                index = index + 1,
                title = lesson.title,
                earnedPoints = lesson.earnedPoints,
                maxPoints = lesson.maxPoints
            )
        }
    }
}

// --- DATOS PARA EL PREVIEW ---
fun getMockExercises(): List<LessonUiModel> {
    return listOf(
        LessonUiModel(
            id = "1",
            title = "¿Quién fue el creador de Python a principios de la década de 1990?",
            earnedPoints = 200,
            maxPoints = 200
        ),
        LessonUiModel(
            id = "2",
            title = "¿Qué significa Python?",
            earnedPoints = 200,
            maxPoints = 200
        ),
        LessonUiModel(
            id = "3",
            title = "¿En qué año se lanzó la primera versión de Python (0.9.0)?",
            earnedPoints = 100,
            maxPoints = 200
        ),
        LessonUiModel(
            id = "4",
            title = "Selecciona dos características principales de Python.",
            earnedPoints = 200,
            maxPoints = 200
        ),
        LessonUiModel(
            id = "5",
            title = "¿Para qué se utiliza principalmente Python hoy en día?",
            earnedPoints = 0,
            maxPoints = 200
        )
    )
}

// --- PREVIEW ---
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CodeScreenPreview() {
    val mockNavController = rememberNavController()

    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CodeContent(lessons = getMockExercises())
        }
    }
}
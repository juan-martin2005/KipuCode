package com.kipucode.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CoursesViewModel

@Composable
fun ExploreScreen(
    courseViewModel: CoursesViewModel = hiltViewModel()
) {
    val progressValue = 0.1f

    val courseState by courseViewModel.courseState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        when(val state = courseState){
            is Response.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KipuTeal)
                }
            }
            is Response.Success -> {
                val courses = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = KipuTeal)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(50.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "1",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = KipuTeal
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "Thinking and First Steps in Python",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Nunito,
                                        lineHeight = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Beginner",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Learn the fundamentals of programming, explore the history of Python, and set up your development environment..",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontFamily = Nunito
                                )

                                Spacer(modifier = Modifier.height(24.dp))


                                Text(
                                    "Module Progress",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .background(
                                                color = Color.White.copy(alpha = 0.3f),
                                                shape = CircleShape
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction = progressValue)
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "${(progressValue * 100).toInt()}%",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // --- TÍTULO LECCIONES ---
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            "Lessons",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Nunito,
                            color = KipuDarkBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- LISTA DE LECCIONES ---
                    items(courses) { course ->
                        DetailedLessonItem(course, KipuTeal, KipuDarkBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            is Response.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error al cargar: ${state.message}", color = Color.Red)
                }
            }
            null -> {}
        }

    }
}

@Composable
fun DetailedLessonItem(
    lesson: Course,
    accentColor: Color,
    darkBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de documento
            Surface(
                modifier = Modifier.size(45.dp),
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_description),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)) {
                Text(
                    text = "${lesson.title} ${lesson.description} ${lesson.completed} ",
                    fontWeight = FontWeight.Bold,
                    color = darkBlue,
                    fontSize = 14.sp,
                    fontFamily = Nunito
                )
                Text(
                    text = if (lesson.completed) "Completado" else "Pendiente",
                    color = if (lesson.completed) accentColor else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Nunito
                )
            }

            if (lesson.completed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Completado",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ExplorePreview() {
//    ExploreScreen()
//}
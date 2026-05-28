package com.kipucode.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Nunito

// Modelo de datos para las lecciones detalladas
data class DetailedLesson(
    val id: String,
    val title: String,
    val isCompleted: Boolean
)

@Composable
fun ExploreScreen() {
    val kipuTeal = Color(0xFF0293a8)
    val darkBlue = Color(0xFF081c40)
    val bgColor = Color(0xFFf6f7f9)

    val lessons = listOf(
        DetailedLesson("1.1", "¿Qué es la programación y por qué Python?", true),
        DetailedLesson("1.2", "Instalación de Python y primeros comandos", true),
        DetailedLesson("1.3", "Tipos de datos básicos: números y strings", true),
        DetailedLesson("1.4", "Errores: sintaxis vs. semántica", true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = kipuTeal)
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
                                        color = kipuTeal
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Pensamiento Computacional y Primeros Pasos en Python",
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
                                "Principiante",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Aprende los fundamentos de la programación, la historia de Python y configura tu entorno de desarrollo.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontFamily = Nunito
                        )

                        Spacer(modifier = Modifier.height(24.dp))


                        Text(
                            "Progreso del módulo",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val progressValue = 0.5f // 50%

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
                    "Lecciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = Nunito,
                    color = darkBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- LISTA DE LECCIONES ---
            items(lessons) { lesson ->
                DetailedLessonItem(lesson, kipuTeal, darkBlue)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun DetailedLessonItem(
    lesson: DetailedLesson,
    accentColor: Color,
    darkBlue: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Opcional, según diseño
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

            Column(modifier = Modifier.weight(1f)
                .padding(end = 16.dp)) {
                Text(
                    text = "${lesson.id} ${lesson.title}",
                    fontWeight = FontWeight.Bold,
                    color = darkBlue,
                    fontSize = 14.sp,
                    fontFamily = Nunito
                )
                Text(
                    text = if (lesson.isCompleted) "Completado" else "Pendiente",
                    color = if (lesson.isCompleted) accentColor else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Nunito
                )
            }

            if (lesson.isCompleted) {
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
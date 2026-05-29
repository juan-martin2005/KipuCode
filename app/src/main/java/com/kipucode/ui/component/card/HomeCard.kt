package com.kipucode.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Nunito

@Composable
fun HomeCard(
    courseName: String,
    languageName: String,
    currentLessons: Int,
    totalLessons: Int,
    progressPercentage: Int,
    totalXp: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val horizontalProgressFactor = currentLessons.toFloat() / totalLessons

    val cardBackground = Color(0xFF007A93)
    val whiteAlpha = Color.White.copy(alpha = 0.15f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = cardBackground, shape = RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        // --- SECCIÓN 1: TÍTULO DEL CURSO (Fila Completa) ---
        Text(
            text = courseName,
            color = Color.White,
            fontSize = 24.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN 2: CUERPO PRINCIPAL (Distribución 2f y 1f) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna Izquierda: Información de Lecciones y Progreso (2f)
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                // Badge de lenguaje actual
                Row(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_python),
                        contentDescription = languageName,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = languageName,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.home_bar_lessons, currentLessons, totalLessons),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(8.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(horizontalProgressFactor)
                            .height(8.dp)
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.home_underline_text_bar),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal
                )
            }

            // Columna Derecha: Indicador Circular (1f)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progressPercentage / 100f },
                    modifier = Modifier.size(85.dp),
                    color = Color.White,
                    strokeWidth = 7.dp,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Text(
                    text = "$progressPercentage%",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SECCIÓN 3: LÍNEA DIVISORIA ---
        HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(12.dp))

        // --- SECCIÓN 4: ESTADÍSTICAS INFERIORES (XP y Racha) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Recuadro XP total
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(whiteAlpha, shape = RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "XP",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.home_xp_text),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = stringResource(R.string.home_xp_value, totalXp),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Recuadro Racha actual
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(whiteAlpha, shape = RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fire),
                    contentDescription = "Racha",
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.home_streak_text),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = stringResource(R.string.home_streak_value, streakDays),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeCardPreview() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFf6f7f9))
//            .padding(16.dp)
//    ) {
//        HomeCard(
//            courseName = "Fundamentos Básicos de Programación Python",
//            languageName = "Python",
//            currentLessons = 3,
//            totalLessons = 10,
//            progressPercentage = 65,
//            totalXp = 1250,
//            streakDays = 7
//        )
//    }
//}
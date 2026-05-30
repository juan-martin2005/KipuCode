package com.kipucode.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
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
    currentLessons: Int,
    totalLessons: Int,

    modifier: Modifier = Modifier
) {
    val horizontalProgressFactor = currentLessons.toFloat() / totalLessons.toFloat()
    val progressPercentage = (horizontalProgressFactor * 100).toInt()

    val cardBackground = Color(0xFF007A93)
    val whiteAlpha = Color.White.copy(alpha = 0.2f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = cardBackground, shape = RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        // --- SECCIÓN 1: CABECERA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        whiteAlpha,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_python),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = courseName,
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECCIÓN 2: PROGRESO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna Derecha: Textos y Barra horizontal
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.home_bar_lessons, currentLessons, totalLessons),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(whiteAlpha, shape = RoundedCornerShape(4.dp))
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

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "$progressPercentage%",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
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
//            currentLessons = 3,
//            totalLessons = 10,
//        )
//    }
//}
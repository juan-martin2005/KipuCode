package com.kipucode.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,

    courseName: String,
    currentLessons: Int,
    totalLessons: Int,
    courseNumber: Int? = 0,

    iconResId: Int? = null,
) {
    val horizontalProgressFactor = currentLessons.toFloat() / totalLessons.toFloat()
    val progressPercentage = (horizontalProgressFactor * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = KipuTealDark,
                shape = RoundedCornerShape(24.dp)
            )
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
                        White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (iconResId != null) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (courseNumber != null) {
                    Text(
                        text = courseNumber.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = White.copy(alpha = 0.9f)
                    )
                }
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

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN 2: PROGRESO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.home_bar_lessons,
                        currentLessons,
                        totalLessons
                    ),
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
                        .background(
                            White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(horizontalProgressFactor)
                            .height(8.dp)
                            .background(
                                White,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.home_underline_text_bar),
                    color = White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "$progressPercentage%",
                color = White,
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Con Icono Drawable")
@Composable
fun HomeCardPreviewWithIcon() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundGray)
            .padding(16.dp)
    ) {
        HomeCard(
            courseName = "Fundamentos Básicos de Programación Python",
            currentLessons = 3,
            totalLessons = 10,
            iconResId = R.drawable.ic_python
        )
    }
}

@Preview(showBackground = true, name = "Con Número de Curso")
@Composable
fun HomeCardPreviewWithNumber() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundGray)
            .padding(16.dp)
    ) {
        HomeCard(
            courseName = "Fundamentos Básicos de Programación Python",
            currentLessons = 3,
            totalLessons = 10,
            courseNumber = 1
        )
    }
}
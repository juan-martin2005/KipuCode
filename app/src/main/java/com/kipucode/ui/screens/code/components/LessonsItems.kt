package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Red

// --- DISEÑO DE CADA FILA ---
@Composable
fun LessonRowItem(
    index: Int,
    title: String,
    earnedPoints: Int,
    maxPoints: Int
) {
    val statusColor = when {
        earnedPoints == 0 -> Gray
        earnedPoints <= maxPoints / 2 -> Red
        else -> Green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    KipuTealDark.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KipuDarkBlue
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Título lección
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = KipuDarkBlue,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Puntaje
        val scoreText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = statusColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                append("$earnedPoints")
            }
            withStyle(style = SpanStyle(color = Gray, fontSize = 14.sp)) {
                append(" / $maxPoints XP")
            }
        }
        Text(
            text = scoreText,
            modifier = Modifier.width(100.dp),
            fontFamily = Nunito,
            textAlign = TextAlign.End,
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun LessonRowItemPreview() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp)
    ) {
        LessonRowItem(
            index = 1,
            title = "¿Quién fue el creador de Python a principios de la década de 1990?",
            earnedPoints = 200,
            maxPoints = 200
        )

        LessonRowItem(
            index = 2,
            title = "¿En qué año se lanzó la primera versión de Python (0.9.0)?",
            earnedPoints = 80,
            maxPoints = 200
        )

        LessonRowItem(
            index = 3,
            title = "¿Para qué se utiliza principalmente Python hoy en día?",
            earnedPoints = 0,
            maxPoints = 200
        )
    }
}
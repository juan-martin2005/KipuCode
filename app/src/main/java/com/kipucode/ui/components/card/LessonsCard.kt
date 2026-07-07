package com.kipucode.ui.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.kipucode.ui.theme.*

@Composable
fun LessonCard(
    lessonId: String,
    title: String,
    isCompleted: Boolean,
    isLocked: Boolean,
    onLessonClick: (String) -> Unit
) {
    val borderColor = if (isLocked) LightGray else KipuTeal

    val statusText = when {
        isCompleted -> stringResource(id = R.string.status_completed)
        isLocked -> stringResource(id = R.string.status_locked)
        else -> stringResource(id = R.string.status_in_progress)
    }

    val statusColor = if (isCompleted) KipuTeal else Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, borderColor),
        onClick = {
            if (!isLocked) {
                onLessonClick(lessonId)
            }
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 17.sp,
                    color = KipuDarkBlue,
                    fontSize = 16.sp
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            when {
                isCompleted -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = stringResource(id = R.string.status_completed),
                        tint = KipuTeal
                    )
                }
                isLocked -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = stringResource(id = R.string.status_locked),
                        tint = BorderLightGray
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_unlock),
                        contentDescription = stringResource(id = R.string.status_in_progress),
                        tint = KipuTeal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FA)
@Composable
fun LessonCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LessonCard(
            lessonId = "",
            title = "Pensamiento Computacional y Lenguajes Formales",
            isCompleted = true,
            isLocked = false,
            onLessonClick = {}
        )
        LessonCard(
            lessonId = "",
            title = "El Intérprete de Python y tu Primer Script",
            isCompleted = true,
            isLocked = false,
            onLessonClick = {}
        )
        LessonCard(
            lessonId = "",
            title = "Tipos de Datos Fundamentales",
            isCompleted = false,
            isLocked = true,
            onLessonClick = {}
        )
    }
}
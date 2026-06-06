package com.kipucode.ui.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.LightGray
import com.kipucode.ui.theme.Nunito

@Composable
fun LessonCard(
    title: String,
    isCompleted: Boolean,
    isLocked: Boolean
) {
    val borderColor = if (isLocked) LightGray else KipuTeal

    val statusText = when {
        isCompleted -> "Completed"
        isLocked -> "Locked"
        else -> "In progress"
    }

    val statusColor = if (isCompleted) KipuTeal else Color.Gray

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
                        contentDescription = "Completed",
                        tint = KipuTeal
                    )
                }
                isLocked -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Locked",
                        tint = BorderLightGray
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_unlock),
                        contentDescription = "Unlocked",
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
            title = "Pensamiento Computacional y Lenguajes Formales",
            isCompleted = true,
            isLocked = false
        )
        LessonCard(
            title = "El Intérprete de Python y tu Primer Script",
            isCompleted = false,
            isLocked = false
        )
        LessonCard(
            title = "Tipos de Datos Fundamentales",
            isCompleted = false,
            isLocked = true
        )
    }
}
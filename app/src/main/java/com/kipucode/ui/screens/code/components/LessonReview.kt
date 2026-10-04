package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

// --- DISEÑO DE CADA FILA ---
@Composable
fun CardExerciseReview(
    masteryPercentage: Int? = null,
    stability: Double = 0.0,
    dueTime: Long,
    lessonTitle: String,
    exerciseType: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val isExpired = dueTime < now
    val difference = (now - dueTime).milliseconds
    val remaining = (dueTime - now).milliseconds

    val hours = if (isExpired) difference.inWholeHours else remaining.inWholeHours

    val statusDue = when {
        isExpired -> "PENDIENTE A REPASAR"
        hours <= 0 -> "REPASO SUGERIDO HOY"
        hours < 24 -> "REPASO EN ${hours}H"
        else -> "REPASO EN ${hours / 24}D"
    }

    val dueBgColor = if (isExpired) Color(0xFFFFF3E0) else Color(0xFFE3F2FD)
    val dueTextColor = if (isExpired) Color(0xFFE65100) else Color(0xFF1565C0)

    val statusColor = when {
        masteryPercentage != null && masteryPercentage >= 80 -> Color(0xFF2E7D32)
        masteryPercentage != null && masteryPercentage >= 40 -> Color(0xFFE65100)
        else -> Color(0xFFF51414)
    }

    val formattedStability = when {
        stability <= 0.0 -> "--"
        stability < 1.0 -> "${(stability * 24).toInt()}h"
        else -> String.format(Locale.US, "%.1fd", stability)
    }

    val (typeLabel, typeIcon) = when (exerciseType.uppercase()) {
        "FLASHCARD" -> "Flashcard" to R.drawable.ic_flash_card
        "UNIQUE_CHOICE" -> "Opción múltiple" to R.drawable.ic_quiz
        else -> (if (exerciseType.isNotBlank()) exerciseType else "Ejercicio") to R.drawable.ic_quiz
    }

    val (typeBgColor, typeTextColor) = when (exerciseType.uppercase()) {
        "FLASHCARD" -> Color(0xFFE0F2F1) to KipuTealDark
        "UNIQUE_CHOICE" -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)
        else -> Color(0xFFF0F4F8) to Color(0xFF5A6E85)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = dueBgColor
                ) {
                    Text(
                        text = statusDue,
                        fontFamily = Nunito,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = dueTextColor
                    )
                }

                if (exerciseType.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = typeBgColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(typeIcon),
                                contentDescription = null,
                                tint = typeTextColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = typeLabel,
                                fontFamily = Nunito,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = typeTextColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = lessonTitle,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = KipuDarkBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RETENCIÓN",
                            fontFamily = Nunito,
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${masteryPercentage ?: 0}%",
                            fontFamily = Nunito,
                            color = statusColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .padding(horizontal = 14.dp)
                            .height(30.dp)
                            .width(1.5.dp),
                        color = Color(0xFFE0E0E0)
                    )

                    Column {
                        Text(
                            text = "ESTABILIDAD",
                            fontFamily = Nunito,
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = formattedStability,
                            fontFamily = Nunito,
                            color = KipuDarkBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = KipuTealDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Empezar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_filled),
                        contentDescription = "Flecha derecha",
                        tint = Color.White,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(start = 6.dp)
                    )
                }
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun LessonRowItemPreview() {
    CardExerciseReview(
        masteryPercentage = 40,
        stability = 2.5,
        dueTime = 1790881315441,
        lessonTitle = "Manejo de errores",
        exerciseType = "FLASHCARD",
        onClick = {}
    )
}
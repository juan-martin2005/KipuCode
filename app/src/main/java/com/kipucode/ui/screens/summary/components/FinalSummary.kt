package com.kipucode.ui.screens.summary.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Red
import com.kipucode.ui.theme.Yellow

@Composable
fun FinalSummary(
    modifier: Modifier = Modifier,
    xpEarned: Int,
    accuracy: Float,
    correctCount: Int,
    reinforceCount: Int,
    timeFormatted: String,
    summaryMessage: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .background(Yellow.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Yellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = "+$xpEarned XP Ganados",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = KipuDarkBlue
            )
        }

        CircularGauge(modifier = Modifier.padding(vertical = 14.dp), percentage = accuracy)

        Text(
            modifier = Modifier.padding(vertical = 12.dp),
            text = summaryMessage,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Gray,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Correctas
            StatPill(
                modifier = Modifier.weight(1f),
                color = Green,
                count = correctCount.toString(),
                label = "Correctas",
                iconRes = R.drawable.ic_correct
            )
            // A reforzar
            StatPill(
                modifier = Modifier.weight(1f),
                color = Red,
                count = reinforceCount.toString(),
                label = "A reforzar",
                iconRes = R.drawable.ic_incorrect
            )
            // Tiempo total
            StatPill(
                modifier = Modifier.weight(1f),
                color = KipuTeal,
                count = timeFormatted,
                label = "Tiempo",
                iconRes = R.drawable.ic_info
            )
        }
    }
}

@Composable
fun CircularGauge(
    modifier: Modifier = Modifier,
    percentage: Float,
    diameter: Dp = 130.dp,
    strokeWidth: Dp = 12.dp,
    arcColor: Color = Green.copy(alpha = 0.85f),
    trackColor: Color = Red.copy(alpha = 0.85f)
) {
    val progress = percentage.coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(diameter)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(strokePx / 2, strokePx / 2)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx)
            )

            if (progress > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontFamily = Nunito,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                color = KipuDarkBlue
            )
            Text(
                text = "PRECISIÓN",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Gray,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun StatPill(
    count: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    iconRes: Int? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = lerp(color, Color.Black, 0.2f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
//                .border(2.dp, color.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Text(
                    text = count,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = lerp(color, Color.Black, 0.2f)
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF3F7FA, name = "FinalSummary")
@Composable
fun FinalSummaryPreview() {
    FinalSummary(
        xpEarned = 180,
        accuracy = 0.80f,
        correctCount = 8,
        reinforceCount = 2,
        timeFormatted = "12m",
        modifier = Modifier.padding(16.dp),
        summaryMessage = "¡Felicidades! Dominaste la mayoría de conceptos"
    )
}
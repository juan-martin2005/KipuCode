package com.kipucode.ui.screens.summary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@Composable
fun RepetitionSchedule(
    memoryRetentionPct: Int,
    nextReviewText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                tint = KipuTeal,
                modifier = Modifier.size(28.dp)
            )

            Column {
                Text(
                    text = "Próximo repaso óptimo",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = KipuDarkBlue
                )
                Text(
                    text = "Calculado automáticamente",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = Color(0xFF6C757D)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
//                    .border(2.dp, KipuTeal.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .background(KipuTeal.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = nextReviewText,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = KipuTeal
                )
            }
        }

        Text(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            text = "Programamos tus repasos en el momento justo para que no olvides lo aprendido.",
            fontFamily = Nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = KipuDarkBlue
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    KipuDarkBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((memoryRetentionPct / 100f).coerceIn(0.01f, 1f))
                    .height(10.dp)
                    .background(
                        KipuTeal,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Porcentaje de retención estimada",
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF495057)
            )
            Text(
                text = "$memoryRetentionPct%",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF007A93)
            )
        }
    }
}

// -------------------------------------------------------------
// PREVIEW
// -------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFFF3F7FA, name = "RepetitionSchedule")
@Composable
fun RepetitionSchedulePreview() {
    RepetitionSchedule(
        memoryRetentionPct = 0,
        nextReviewText = "En 3 días",
        modifier = Modifier.padding(16.dp)
    )
}
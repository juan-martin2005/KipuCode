package com.kipucode.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Orange
import com.kipucode.ui.theme.White

enum class DayStatus { COMPLETED, CURRENT, PENDING }

data class DayStreakUI(
    val letter: String,
    val status: DayStatus,
)

@Composable
fun WeeklyStreak(
    modifier: Modifier = Modifier,
    currentStreakDays: Int,
    weekDays: List<DayStreakUI>,
    nextStreakText: String
) {
    val isTodayPending = weekDays.any { it.status == DayStatus.CURRENT }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderLightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fire),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )

                    Column {
                        Text(
                            text = "¡Racha de $currentStreakDays días!",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = KipuDarkBlue
                        )
                        Text(
                            text = if (isTodayPending) {
                                "¡Completa un ejercicio hoy para mantener tu racha!"
                            } else {
                                "Completaste tu cuota del día de hoy"
                            },
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Gray.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDays.forEach { day ->
                        DayStreakItem(day = day)
                    }
                }
            }

            if (nextStreakText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = nextStreakText,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )
            }
        }
    }
}

@Composable
private fun DayStreakItem(day: DayStreakUI) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = day.letter,
            fontFamily = Nunito,
            fontWeight = if (day.status == DayStatus.CURRENT) FontWeight.Black else FontWeight.Bold,
            fontSize = 11.sp,
            color = if (day.status == DayStatus.CURRENT) Orange else Gray
        )

        Spacer(modifier = Modifier.height(6.dp))

        when (day.status) {
            DayStatus.COMPLETED -> {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Green, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_correct),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            DayStatus.CURRENT -> {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(White, CircleShape)
                        .border(1.5.dp, Orange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fire),
                        contentDescription = null,
                        tint = Orange,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DayStatus.PENDING -> {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(MoonFrost, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color(0xFF94A3B8), CircleShape)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PREVIEW
// -------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFFF3F7FA, name = "WeeklyStreak")
@Composable
private fun WeeklyStreakPreview() {
    KipuCodeTheme {
        WeeklyStreak(
            currentStreakDays = 6,
            weekDays = listOf(
                DayStreakUI("L", DayStatus.COMPLETED),
                DayStreakUI("M", DayStatus.COMPLETED),
                DayStreakUI("X", DayStatus.COMPLETED),
                DayStreakUI("J", DayStatus.COMPLETED),
                DayStreakUI("V", DayStatus.COMPLETED),
                DayStreakUI("S", DayStatus.COMPLETED),
                DayStreakUI("D", DayStatus.PENDING)
            ),
            modifier = Modifier.padding(16.dp),
            nextStreakText = "A solo 1 dia de superar la Racha"
        )
    }
}

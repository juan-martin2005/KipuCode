package com.kipucode.ui.screens.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.screens.summary.components.DayStatus
import com.kipucode.ui.screens.summary.components.DayStreakUI
import com.kipucode.ui.screens.summary.components.FinalSummary
import com.kipucode.ui.screens.summary.components.RepetitionSchedule
import com.kipucode.ui.screens.summary.components.WeeklyStreak
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.viewmodel.SummaryUiState
import com.kipucode.viewmodel.SummaryViewModel

// --- SCREEN (Stateful / Hilt) ---
@Composable
fun SummaryScreen(
    onContinue: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BackgroundGray,
    ) { paddingValues ->
        SummaryScreenContent(
            uiState = uiState,
            onClickFilledButton = onContinue,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// --- CONTENT  ---
@Composable
fun SummaryScreenContent(
    uiState: SummaryUiState?,
    onClickFilledButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Resumen final con métricas
        item(key = "final_summary") {
            FinalSummary(
                xpEarned = uiState.xpEarned,
                accuracy = uiState.accuracy,
                correctCount = uiState.correctCount,
                reinforceCount = uiState.reinforceCount,
                timeFormatted = uiState.timeFormatted,
                summaryMessage = uiState.summaryMessage
            )
        }

        // Repaso inteligente espaciado
        item(key = "repetition_schedule") {
            RepetitionSchedule(
                memoryRetentionPct = uiState.memoryRetentionPct,
                nextReviewText = uiState.nextReviewText
            )
        }

        // Racha semanal
        item(key = "weekly_streak") {
            WeeklyStreak(
                currentStreakDays = uiState.currentStreakDays,
                weekDays = uiState.weekDays,
                nextStreakText = uiState.nextStreakText
            )
        }

        // Botón continuar al final del flujo
        item(key = "button") {
            FilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textButton = "Volver al inicio",
                onClickFilledButton = onClickFilledButton
            )
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Summary Screen Preview")
@Composable
fun SummaryScreenContentPreview() {
    SummaryScreenContent(
        uiState = SummaryUiState(
            xpEarned = 180,
            accuracy = 0.80f,
            correctCount = 8,
            reinforceCount = 2,
            timeFormatted = "1m 45s",
            summaryMessage = "¡Compiló con éxito!",
            memoryRetentionPct = 85,
            nextReviewText = "En 3 días",
            currentStreakDays = 6,
            weekDays = listOf(
                DayStreakUI("L", DayStatus.COMPLETED),
                DayStreakUI("M", DayStatus.COMPLETED),
                DayStreakUI("X", DayStatus.COMPLETED),
                DayStreakUI("J", DayStatus.COMPLETED),
                DayStreakUI("V", DayStatus.COMPLETED),
                DayStreakUI("S", DayStatus.CURRENT),
                DayStreakUI("D", DayStatus.PENDING)
            ),
            nextStreakText = "¡A solo 1 día de alcanzar tu objetivo semanal!"
        ),
        onClickFilledButton = {}
    )
}
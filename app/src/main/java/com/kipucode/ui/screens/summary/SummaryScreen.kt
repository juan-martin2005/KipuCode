package com.kipucode.ui.screens.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.kipucode.ui.components.buttons.FilledButton
import com.kipucode.ui.components.buttons.OutlineButton
import com.kipucode.ui.screens.summary.components.FinalSummary
import com.kipucode.ui.screens.summary.components.RepetitionSchedule
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuCodeTheme

// --- SCREEN (Stateful / Hilt) ---
@Composable
fun SummaryScreen(
    onReset: () -> Unit,
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
            onClickResetFilledButton = onReset,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// --- CONTENT ---
@Composable
fun SummaryScreenContent(
    uiState: SummaryUiState?,
    onClickFilledButton: () -> Unit,
    onClickResetFilledButton: () -> Unit,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // --- ZONA CENTRAL: Ocupa todo el espacio disponible y se centra ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resumen final con métricas
                FinalSummary(
                    xpEarned = uiState.xpEarned,
                    accuracy = uiState.accuracy,
                    correctCount = uiState.correctCount,
                    reinforceCount = uiState.reinforceCount,
                    timeFormatted = uiState.timeFormatted,
                    summaryMessage = uiState.summaryMessage
                )

                // Repaso inteligente espaciado (si aplica a flashcards)
                if (uiState.showRepetitionSchedule) {
                    RepetitionSchedule(
                        memoryRetentionPct = uiState.memoryRetentionPct,
                        nextReviewText = uiState.nextReviewText
                    )
                }
            }
        }

        // --- ZONA INFERIOR: Botones fijos abajo con jerarquía clara ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Acción principal
            FilledButton(
                modifier = Modifier.fillMaxWidth(),
                textButton = "Volver al inicio",
                onClickFilledButton = onClickFilledButton
            )

            // Acción secundaria estilizada
            OutlineButton(
                modifier = Modifier.fillMaxWidth(),
                textButton = "Reiniciar ejercicio",
                onClickFilledButton = onClickResetFilledButton,
            )
        }
    }
}

// --- PREVIEWS ---
@Preview(showBackground = true, name = "Summary Screen - Flashcards", showSystemUi = true, heightDp = 1000)
@Composable
private fun SummaryScreenContentPreview() {
    KipuCodeTheme {
        SummaryScreenContent(
            uiState = SummaryUiState(
                xpEarned = 180,
                accuracy = 0.80f,
                correctCount = 8,
                reinforceCount = 2,
                timeFormatted = "1m 45s",
                summaryMessage = "¡Compiló con éxito!",
                showRepetitionSchedule = true,
                memoryRetentionPct = 85,
                nextReviewText = "En 3 días"
            ),
            onClickResetFilledButton = {},
            onClickFilledButton = {}
        )
    }
}

@Preview(showBackground = true, name = "Summary Screen - Code Exercise", showSystemUi = true, heightDp = 800)
@Composable
private fun SummaryScreenContentCodePreview() {
    KipuCodeTheme {
        SummaryScreenContent(
            uiState = SummaryUiState(
                xpEarned = 150,
                accuracy = 1.0f,
                correctCount = 5,
                reinforceCount = 0,
                timeFormatted = "2m 10s",
                summaryMessage = "¡Dominio total!",
                showRepetitionSchedule = false
            ),
            onClickResetFilledButton = {},
            onClickFilledButton = {}
        )
    }
}

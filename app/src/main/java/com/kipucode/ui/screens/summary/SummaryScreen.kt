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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.ui.components.buttons.FilledButton
import com.kipucode.ui.screens.summary.components.FinalSummary
import com.kipucode.ui.screens.summary.components.RepetitionSchedule
import com.kipucode.ui.theme.BackgroundGray
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

// --- CONTENT  ---
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

        // Repaso inteligente espaciado (solo para ejercicios flashcards)
        if (uiState.showRepetitionSchedule) {
            item(key = "repetition_schedule") {
                RepetitionSchedule(
                    memoryRetentionPct = uiState.memoryRetentionPct,
                    nextReviewText = uiState.nextReviewText
                )
            }
        }

        // Botón reiniciar los ejercicios
        item(key = "button_reset") {
            FilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textButton = "Reiniciar ejercicio",
                onClickFilledButton = onClickResetFilledButton,
                containerColor = Color(0xFF607D8B)
            )
        }

        // Botón continuar al final del flujo
        item(key = "button") {
            FilledButton(
                modifier = Modifier
                    .fillMaxWidth(),
                textButton = "Volver al inicio",
                onClickFilledButton = onClickFilledButton
            )
        }
    }
}

// --- PREVIEW ---
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

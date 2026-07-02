package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Red

// --- DIALOG ---
@Composable
fun FeedbackDialog(
    modifier: Modifier = Modifier,
    isCorrect: Boolean,
    isLoading: Boolean = false,
    onContinue: () -> Unit,
) {
    val backgroundColor = if (isCorrect) Green.copy(alpha = 0.15f) else Red.copy(alpha = 0.15f)
    val accentColor = if (isCorrect) Green else Red
    val iconRes = if (isCorrect) R.drawable.ic_correct else R.drawable.ic_incorrect
    val label = if (isCorrect) stringResource(R.string.custom_dialog_correct) else stringResource(R.string.custom_dialog_incorrect)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = label,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = accentColor
            )
        }

        FilledButton(
            textButton = "Continuar",
            onClickFilledButton = onContinue,
            isLoading = isLoading
        )
    }
}

@Preview(showBackground = true, name = "Feedback Dialog")
@Composable
fun FeedbackDialogCombinedPreview() {
    Column {
        // Correcto
        FeedbackDialog(
            isCorrect = true,
            isLoading = false,
            onContinue = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Incorrecto
        FeedbackDialog(
            isCorrect = false,
            isLoading = false,
            onContinue = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cargando
        FeedbackDialog(
            isCorrect = true,
            isLoading = true,
            onContinue = {}
        )
    }
}
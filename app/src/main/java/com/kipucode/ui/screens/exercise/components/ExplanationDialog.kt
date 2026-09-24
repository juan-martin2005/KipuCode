package com.kipucode.ui.screens.exercise.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.screens.lesson.components.ContentMarkdown
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Red
import com.kipucode.ui.theme.White

// --- DIALOG ---
@Composable
fun ExplanationDialog(
    modifier: Modifier = Modifier,
    isCorrect: Boolean,
    isLoading: Boolean = false,
    onContinue: () -> Unit,
    experience: String,
    explanation: String,
    message: String,
    ) {
    val backgroundColor = if (isCorrect) Green.copy(alpha = 0.12f) else Red.copy(alpha = 0.12f)
    val accentColor = if (isCorrect) lerp(Green, Color.Black, 0.12f)
        else lerp(Red, Color.Black, 0.12f)
    val iconRes = if (isCorrect) R.drawable.ic_correct else R.drawable.ic_incorrect
    val label = if (isCorrect) stringResource(R.string.custom_dialog_correct) else stringResource(R.string.custom_dialog_incorrect)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(28.dp)
            )

            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = label,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 21.sp,
                color = accentColor
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(
                        color = White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = "+$experience xp",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }
        }

        Text(
            modifier = Modifier.padding(4.dp),
            text = message,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = lerp(accentColor, Color.Black, 0.3f)
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "EXPLICACIÓN",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                }

                ContentMarkdown(
                    content = explanation,
                )
            }
        }


        FilledButton(
            modifier = Modifier.padding(vertical = 4.dp),
            textButton = "Siguiente",
            onClickFilledButton = onContinue,
            isLoading = isLoading,
            containerColor = accentColor
        )
    }
}

@Preview(showBackground = true, name = "Feedback Dialog")
@Composable
fun ExplanationDialogCombinedPreview() {
    Column {
        // Correcto
        ExplanationDialog(
            isCorrect = true,
            isLoading = false,
            onContinue = {},
            experience = "50",
            explanation = """
            Comprender el origen de `Python`, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.
        """,
            message = "¡Excelente! Concepto dominado"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Incorrecto
        ExplanationDialog(
            isCorrect = false,
            isLoading = false,
            onContinue = {},
            experience = "25",
            explanation = """
            Comprender el origen de Python, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.
        """,
            message = "¡Cerca! Equivocarse es parte de aprender"
        )

        Spacer(modifier = Modifier.height(24.dp))

    }
}
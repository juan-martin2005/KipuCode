package com.kipucode.ui.screens.exercise.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kipucode.R
import com.kipucode.ui.screens.lesson.components.ContentMarkdown
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.LightGreen
import com.kipucode.ui.theme.Red
import com.kipucode.ui.theme.White

@Composable
fun OptionCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        !showResult -> White
        isSelected && isCorrect -> Green
        isSelected && !isCorrect -> Red
        !isSelected && isCorrect -> Green
        else -> White
    }
    val contentColor = when {
        !showResult -> Gray
        isSelected && isCorrect -> lerp(Green, Color.Black, 0.4f)
        isSelected && !isCorrect -> lerp(Red, Color.Black, 0.4f)
        !isSelected && isCorrect -> lerp(Green, Color.Black, 0.4f)
        else -> Gray
    }

    val borderColor = when {
        !showResult -> KipuDarkBlue.copy(alpha = 0.12f)
        isSelected && isCorrect -> Green
        isSelected && !isCorrect -> Red
        !isSelected && isCorrect -> LightGreen
        else -> KipuDarkBlue.copy(alpha = 0.12f)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor.copy(alpha = 0.07f)),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ContentMarkdown(
                modifier = Modifier.weight(1f),
                content = text,
                color = contentColor
            )

            val showIcon = showResult && (isSelected || isCorrect)
            if (showIcon) {
                val iconRes = if (isCorrect) R.drawable.ic_correct else R.drawable.ic_incorrect
                val iconBgColor = if (isCorrect) Green else Red
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = iconBgColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F4F6)
@Composable
fun OptionCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        OptionCard(
            text = "##### Opción seleccionada (Correcta)",
            isSelected = true,
            isCorrect = true,
            showResult = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OptionCard(
            text = "##### Opción seleccionada (Incorrecta)",
            isSelected = true,
            isCorrect = false,
            showResult = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OptionCard(
            text = "##### Esta era la respuesta correcta",
            isSelected = false,
            isCorrect = true,
            showResult = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OptionCard(
            text = "##### Opción no seleccionada",
            isSelected = false,
            isCorrect = false,
            showResult = true,
            onClick = {}
        )
    }
}
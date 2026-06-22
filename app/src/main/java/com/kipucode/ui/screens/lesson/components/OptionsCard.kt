package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito
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
        isSelected && isCorrect -> Green.copy(alpha = 0.7f)
        isSelected && !isCorrect -> Red.copy(alpha = 0.7f)
        showResult && isCorrect -> Green.copy(alpha = 0.2f)
        else -> White
    }

    val contentColor = when {
        showResult && isSelected -> White
        showResult && isCorrect -> Green
        else -> KipuDarkBlue
    }

    val borderColor = when {
        !showResult -> KipuDarkBlue.copy(alpha = 0.2f)
        isSelected && isCorrect -> Green
        isSelected && !isCorrect -> Red
        !isSelected && isCorrect -> Green.copy(alpha = 0.5f)
        else -> KipuDarkBlue.copy(alpha = 0.2f)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 18.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            if (showResult && isSelected) {
                val iconRes = if (isCorrect) R.drawable.ic_correct else R.drawable.ic_incorrect
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Result",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
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
            text = "Sin seleccionar",
            isSelected = false,
            isCorrect = false,
            showResult = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OptionCard(
            text = "Opción seleccionada (Correcta)",
            isSelected = true,
            isCorrect = true,
            showResult = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OptionCard(
            text = "Opción seleccionada (Incorrecta)",
            isSelected = true,
            isCorrect = false,
            showResult = true,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OptionCard(
            text = "Esta era la respuesta correcta",
            isSelected = false,
            isCorrect = true,
            showResult = true,
            onClick = {}
        )
    }
}
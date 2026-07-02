package com.kipucode.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@Composable
fun FilledButton(
    textButton: String,
    onClickFilledButton: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fontSize: TextUnit = 18.sp
) {
    Button(
        onClick = {
            if (!isLoading && enabled) {
                onClickFilledButton()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KipuTeal,
            contentColor = Color.White,
            disabledContainerColor = KipuTeal.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = textButton,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = Color.White
            )
        }
    }
}

@Composable
fun OutlineButton(
    textButton: String,
    onClickFilledButton: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fontSize: TextUnit = 18.sp
) {
    OutlinedButton(
        onClick = {
            if (!isLoading && enabled) {
                onClickFilledButton()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (enabled && !isLoading) KipuTeal else KipuTeal.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = KipuTeal,
            disabledContentColor = KipuTeal.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = KipuTeal,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = textButton,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = if (enabled) KipuTeal else KipuTeal.copy(alpha = 0.5f)
            )
        }
    }
}
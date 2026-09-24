package com.kipucode.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    fontSize: TextUnit = 18.sp,
    containerColor: Color = KipuTeal,
    contentColor: Color = Color.White
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
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = contentColor,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = textButton,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = contentColor
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
    fontSize: TextUnit = 18.sp,
    color: Color = KipuTeal
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
            color = if (enabled && !isLoading) color else color.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
            disabledContentColor = color.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = color,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = textButton,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = if (enabled) color else color.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Vista de Botones")
@Composable
fun ButtonsPreview() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // --- Filled Button ---
        FilledButton(
            textButton = "Botón Principal",
            onClickFilledButton = {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        FilledButton(
            textButton = "Cargando...",
            isLoading = true,
            onClickFilledButton = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Outline Button ---
        OutlineButton(
            textButton = "Botón Secundario",
            onClickFilledButton = {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlineButton(
            textButton = "Cargando...",
            isLoading = true,
            onClickFilledButton = {}
        )
    }
}
package com.kipucode.ui.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@Composable
fun FilledButton(
    textButton: String,
    onClickFilledButton: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fontSize: TextUnit = 18.sp,
    containerColor: Color = KipuTeal,
    contentColor: Color = Color.White,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    Button(
        onClick = {
            if (!isLoading && enabled) {
                onClickFilledButton()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        contentPadding = contentPadding,
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

@Preview(showBackground = true, name = "Buttons - Variants")
@Composable
private fun ButtonsPreview() {
    KipuCodeTheme {
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
        }
    }
}

package com.kipucode.ui.components.card

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kipucode.R
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.button.OutlineButton
import com.kipucode.ui.theme.Nunito

@Composable
fun KipuDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,

    title: String,
    description: String,
    confirmButtonText: String,
    dismissButtonText: String,

    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,

    @DrawableRes iconRes: Int? = null,
    iconTint: Color,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        KipuDialogContent(
            modifier = modifier,
            title = title,
            description = description,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            onConfirmClick = onConfirmClick,
            onDismissClick = onDismissClick,
            iconRes = iconRes,
            iconTint = iconTint
        )
    }
}

@Composable
fun KipuDialogContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    @DrawableRes iconRes: Int? = null,
    iconTint: Color = Color(0xFFEF4444),
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ICONO  ---
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Dialog Icon",
                    tint = iconTint,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- TÍTULO ---
            Text(
                text = title,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color(0xFF262626),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- DESCRIPCIÓN ---
            Text(
                text = description,
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color(0xFF565656),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- FILA DE BOTONES ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlineButton(
                    textButton = dismissButtonText,
                    onClickFilledButton = onDismissClick,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp
                )
                FilledButton(
                    textButton = confirmButtonText,
                    onClickFilledButton = onConfirmClick,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KipuDialogPreview() {
    KipuDialogContent(
        title = "Delete Account",
        description = "You're going to delete your \"Account\"",
        dismissButtonText = "No, keep it.",
        confirmButtonText = "Yes, Delete!",
        onConfirmClick = {},
        onDismissClick = {},
        iconRes = R.drawable.ic_correct
    )
}
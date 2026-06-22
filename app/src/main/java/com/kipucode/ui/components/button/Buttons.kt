package com.kipucode.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    onClickFilledButton:() -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp
){
    Button(
        onClick = {
            onClickFilledButton()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = KipuTeal)
    ) {
        Text(
            text = textButton,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = Color.White
        )
    }
}

@Composable
fun OutlineButton(
    textButton: String,
    onClickFilledButton:() -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp

){
    OutlinedButton(
        onClick = {
            onClickFilledButton()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(width = 2.dp, color = KipuTeal),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = KipuTeal)
    ) {
        Text(
            text = textButton,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = KipuTeal
        )
    }
}
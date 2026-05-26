package com.kipucode.ui.component.button

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.Nunito

@Composable
fun FilledButton(
    textButton: String,
    onClickFilledButton:() -> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = {
            onClickFilledButton()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0293a8))
    ) {
        Text(
            text = textButton,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun OutlineButton(
    textButton: String,
    onClickFilledButton:() -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedButton(
        onClick = {
            onClickFilledButton()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(width = 2.dp, color = Color(0xFF0293a8)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0293a8))
    ) {
        Text(
            text = textButton,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF0293a8)
        )
    }
}
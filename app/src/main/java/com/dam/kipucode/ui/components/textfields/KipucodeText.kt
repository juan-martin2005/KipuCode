package com.dam.kipucode.ui.components.textfields

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.dam.kipucode.ui.theme.Nunito

@Composable
fun KipucodeText(){
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            ) {
                append("Kipu")
            }

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF0bb8cd),
                )
            ) {
                append("Code")
            }
        },
        fontFamily = Nunito,
        fontSize = 64.sp
    )
}
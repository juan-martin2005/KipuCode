package com.kipucode.ui.components.text_field

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.KipuCyan
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

// Banner KipuCode
@Composable
fun KipucodeText(){
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = White,
                )
            ) {
                append("Kipu")
            }

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    color = KipuCyan,
                )
            ) {
                append("Code")
            }
        },
        fontFamily = Nunito,
        fontSize = 64.sp
    )
}
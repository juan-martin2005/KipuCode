package com.kipucode.ui.components.text_field

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.KipuCodeTheme
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

@Preview(name = "KipucodeText - Dark", showBackground = true, backgroundColor = 0xFF101922)
@Composable
private fun KipucodeTextPreview() {
    KipuCodeTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            KipucodeText()
        }
    }
}
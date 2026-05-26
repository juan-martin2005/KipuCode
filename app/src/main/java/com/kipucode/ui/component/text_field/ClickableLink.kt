package com.kipucode.ui.component.text_field

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

// Componente reutilizable en Login & Register Screen.
// Nos ahorra estar repitiendo la misma lógica para cada vista.
@Composable
fun ClickableLink(
    normalText: String,
    linkText: String,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 16.sp)) {
            append(normalText)
        }
        val clickableLink = LinkAnnotation.Clickable(tag = "Link") {
            onLinkClick()
        }
        withLink(clickableLink) {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF0293a8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.None
                )
            ) {
                append(linkText)
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier
    )
}

//@Preview(showBackground = true, name = "Fragmento de Texto Clickable")
//@Composable
//fun ClickableLinkPreview() {
//    ClickableLink(
//        normalText = "Test sin click, ",
//        linkText = "Test Clickable",
//        onLinkClick = {}
//    )
//}
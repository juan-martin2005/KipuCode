package com.kipucode.ui.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.JetBrains
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.MoonFrost

@Composable
fun FormattedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = KipuParagraph,
    color: Color = Color.Unspecified,
    boldColor: Color = Color.Unspecified,
    codeColor: Color = KipuTeal,
    codeBackground: Color = MoonFrost.copy(alpha = 0.2f),
    textAlign: TextAlign = TextAlign.Start
) {
    val targetColor = if (color != Color.Unspecified) color else (style.color.takeIf { it != Color.Unspecified } ?: KipuDarkBlue)
    val targetBoldColor = if (boldColor != Color.Unspecified) boldColor else targetColor

    val annotatedString = remember(text, targetColor, targetBoldColor, codeColor, codeBackground) {
        parseFormattedSpans(
            input = text,
            defaultColor = targetColor,
            boldColor = targetBoldColor,
            codeColor = codeColor,
            codeBackground = codeBackground
        )
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = style.copy(color = targetColor, textAlign = textAlign)
    )
}

fun parseFormattedSpans(
    input: String,
    defaultColor: Color,
    boldColor: Color = defaultColor,
    codeColor: Color = KipuTeal,
    codeBackground: Color = MoonFrost.copy(alpha = 0.2f)
): AnnotatedString {
    return buildAnnotatedString {
        // Regex para capturar `código inline`, **negrita**, *cursiva* o _cursiva_
        val regex = Regex("""(`[^`]+`|\*\*[^*]+\*\*|\*[^*]+\*|_[^_]+_)""")
        var lastIndex = 0

        for (match in regex.findAll(input)) {
            val range = match.range
            if (range.first > lastIndex) {
                append(input.substring(lastIndex, range.first))
            }

            val value = match.value
            when {
                value.startsWith("`") && value.endsWith("`") -> {
                    val code = value.removeSurrounding("`")
                    val start = length
                    append(code)
                    addStyle(
                        SpanStyle(
                            fontFamily = JetBrains,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = codeColor,
                            background = codeBackground
                        ),
                        start,
                        length
                    )
                }
                value.startsWith("**") && value.endsWith("**") -> {
                    val bold = value.removeSurrounding("**")
                    val start = length
                    append(bold)
                    addStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = boldColor
                        ),
                        start,
                        length
                    )
                }
                (value.startsWith("*") && value.endsWith("*")) ||
                (value.startsWith("_") && value.endsWith("_")) -> {
                    val italic = if (value.startsWith("*")) value.removeSurrounding("*") else value.removeSurrounding("_")
                    val start = length
                    append(italic)
                    addStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic,
                            color = defaultColor.copy(alpha = 0.95f)
                        ),
                        start,
                        length
                    )
                }
            }
            lastIndex = range.last + 1
        }

        if (lastIndex < input.length) {
            append(input.substring(lastIndex))
        }
    }
}

@Preview(showBackground = true, name = "Formatted Text Preview")
@Composable
fun FormattedTextPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormattedText(
            text = "Texto base con **palabras en negrita**, *texto en cursiva* y código inline como `dotnet run` o `Program.cs`."
        )
        FormattedText(
            text = "El compilador Roslyn genera código CIL que el **CLR** compila con **JIT** (`Just-In-Time`).",
            style = KipuParagraph
        )
    }
}

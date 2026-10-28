package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kipucode.ui.screens.exercise.components.formats.FormattedText
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost

@Composable
fun FormattedLessonText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = KipuParagraph,
    color: Color = Color.Unspecified,
    boldColor: Color = Color.Unspecified,
    codeColor: Color = Color.Unspecified,
    codeBackground: Color = MoonFrost.copy(alpha = 0.2f),
    codeFontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Start
) {
    val targetColor = if (color != Color.Unspecified) color else (style.color.takeIf { it != Color.Unspecified } ?: KipuDarkBlue)
    val targetBoldColor = if (boldColor != Color.Unspecified) boldColor else targetColor
    val targetCodeColor = when {
        codeColor != Color.Unspecified -> codeColor
        targetColor == KipuTeal -> KipuTealDark
        else -> KipuTeal
    }

    FormattedText(
        text = text,
        modifier = modifier,
        style = style,
        color = targetColor,
        boldColor = targetBoldColor,
        codeColor = targetCodeColor,
        codeBackground = codeBackground,
        codeFontSize = codeFontSize,
        textAlign = textAlign
    )
}

@Preview(showBackground = true, name = "FormattedLessonText - Default")
@Composable
private fun FormattedLessonTextPreview() {
    KipuCodeTheme {
        FormattedLessonText(
            text = "La **plataforma** .NET provee el **CLR** (_Common Language Runtime_) y la `BCL` (_Base Class Library_).",
            modifier = Modifier.padding(16.dp)
        )
    }
}

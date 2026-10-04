package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kipucode.ui.components.text.FormattedText
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuParagraph

@Composable
fun FormattedLessonText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = KipuParagraph,
    color: Color = KipuDarkBlue,
    textAlign: TextAlign = TextAlign.Start
) {
    FormattedText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign
    )
}

@Preview(showBackground = true, name = "Formatted Lesson Text Preview")
@Composable
fun FormattedLessonTextPreview() {
    FormattedLessonText(
        text = "La plataforma .NET provee el **CLR** (_Common Language Runtime_) y la `BCL` (_Base Class Library_).",
        modifier = Modifier.padding(16.dp)
    )
}

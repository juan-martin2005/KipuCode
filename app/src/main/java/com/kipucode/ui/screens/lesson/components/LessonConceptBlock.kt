package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.kipucode.domain.model.LessonBlock
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import com.kipucode.ui.theme.KipuH2
import com.kipucode.ui.theme.KipuParagraph

@Composable
fun LessonConceptBlock(
    block: LessonBlock.Concept,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (block.title.isNotBlank()) {
            Text(
                text = block.title,
                style = KipuH2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (block.text.isNotBlank()) {
            FormattedLessonText(
                text = block.text,
                style = KipuParagraph,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Lesson Concept Block Preview")
@Composable
fun LessonConceptBlockPreview() {
    LessonConceptBlock(
        block = LessonBlock.Concept(
            title = "Arquitectura general de la plataforma .NET",
            text = "La plataforma .NET se compone de tres pilares esenciales: el **CLR** (_Common Language Runtime_), que administra la memoria y ejecución; la `BCL` (_Base Class Library_), que provee tipos estándar; y el `SDK`, que incluye el compilador Roslyn y la CLI."
        ),
        modifier = Modifier.padding(16.dp)
    )
}

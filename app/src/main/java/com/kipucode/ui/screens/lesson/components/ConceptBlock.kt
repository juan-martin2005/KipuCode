package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kipucode.domain.model.LessonBlock
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuH2
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal

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
            val cleanTitle = block.title.replace(Regex("""^#{1,6}\s+"""), "").trim()
            FormattedLessonText(
                text = cleanTitle,
                style = KipuH2,
                color = KipuTeal,
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

@Preview(showBackground = true, name = "LessonConceptBlock - Default")
@Composable
private fun LessonConceptBlockPreview() {
    KipuCodeTheme {
        LessonConceptBlock(
            block = LessonBlock.Concept(
                title = "Arquitectura general de la plataforma .NET",
                text = "La plataforma .NET se compone de tres pilares esenciales: el **CLR** (_Common Language Runtime_), que administra la memoria y ejecución; la `BCL` (_Base Class Library_), que provee tipos estándar; y el `SDK`, que incluye el compilador Roslyn y la CLI."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "LessonConceptBlock - Code in Title")
@Composable
private fun LessonConceptBlockCodeInTitlePreview() {
    KipuCodeTheme {
        LessonConceptBlock(
            block = LessonBlock.Concept(
                title = "## 1.X Enteros con signo: `int` y `long`",
                text = "C# provee tipos primitivos enteros como `int` (32 bits) y `long` (64 bits)."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

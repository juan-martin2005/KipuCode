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
import com.kipucode.ui.theme.KipuH3
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal

@Composable
fun LessonSubconceptBlock(
    block: LessonBlock.Subconcept,
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
                style = KipuH3,
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

@Preview(showBackground = true, name = "LessonSubconceptBlock - Default")
@Composable
private fun LessonSubconceptBlockPreview() {
    KipuCodeTheme {
        LessonSubconceptBlock(
            block = LessonBlock.Subconcept(
                title = "Compilación Just-In-Time (JIT)",
                text = "El compilador **JIT** convierte el código **CIL** intermedio a código máquina nativo bajo demanda."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "LessonSubconceptBlock - Code in Title")
@Composable
private fun LessonSubconceptBlockCodeInTitlePreview() {
    KipuCodeTheme {
        LessonSubconceptBlock(
            block = LessonBlock.Subconcept(
                title = "### 4.1 Anatomía de `int.TryParse` y el parámetro `out`",
                text = "El método `int.TryParse` retorna un booleano indicando el éxito de la conversión."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

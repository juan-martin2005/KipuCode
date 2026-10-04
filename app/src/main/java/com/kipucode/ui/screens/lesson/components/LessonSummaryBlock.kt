package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.LessonBlock
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.KipuH4
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.White
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LessonSummaryBlock(
    block: LessonBlock.Summary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = BorderLightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Título del Resumen ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_resume),
                contentDescription = null,
                tint = KipuTeal,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Resumen de la Lección",
                style = KipuH4.copy(fontSize = 18.sp, color = KipuTeal)
            )
        }

        // --- Puntos clave con viñetas estilizadas ---
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            block.items.forEach { itemText ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(7.dp)
                            .background(
                                color = KipuTeal,
                                shape = CircleShape
                            )
                    )

                    FormattedLessonText(
                        text = itemText,
                        style = KipuParagraph,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lesson Summary Block Preview")
@Composable
fun LessonSummaryBlockPreview() {
    LessonSummaryBlock(
        block = LessonBlock.Summary(
            items = listOf(
                ".NET divide su arquitectura en el **CLR** (ejecución y memoria), la **BCL** (biblioteca base) y el **SDK** (herramientas).",
                "El **.NET Runtime** combina `CLR` y `BCL` para ejecutar aplicaciones ya compiladas.",
                "El comando `dotnet run` compila en memoria y ejecuta el proyecto en un solo paso."
            )
        ),
        modifier = Modifier.padding(16.dp)
    )
}

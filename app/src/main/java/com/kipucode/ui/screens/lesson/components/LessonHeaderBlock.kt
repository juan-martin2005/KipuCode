package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kipucode.R
import com.kipucode.domain.model.LessonBlock
import com.kipucode.ui.theme.KipuH1
import com.kipucode.ui.theme.KipuH5
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.MoonFrost
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LessonHeaderBlock(
    block: LessonBlock.Header,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = block.title,
            style = KipuH1,
            modifier = Modifier.fillMaxWidth()
        )

        if (block.objective.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MoonFrost.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = KipuTeal.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_resume),
                        contentDescription = null,
                        tint = KipuTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Objetivo de Aprendizaje",
                        style = KipuH5.copy(color = KipuTeal)
                    )
                }

                FormattedLessonText(
                    text = block.objective,
                    style = KipuParagraph
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Lesson Header Block Preview")
@Composable
fun LessonHeaderBlockPreview() {
    LessonHeaderBlock(
        block = LessonBlock.Header(
            title = "01 - Introducción a la Plataforma .NET",
            objective = "Comprender la **arquitectura general** de .NET, los pilares esenciales (`CLR`, `BCL`, `SDK`) y la diferencia práctica entre el **SDK** y el **Runtime**."
        ),
        modifier = Modifier.padding(16.dp)
    )
}

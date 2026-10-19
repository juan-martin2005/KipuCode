package com.kipucode.ui.screens.lesson.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.LessonBlock
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuH4
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Orange
import com.kipucode.ui.theme.White

@Composable
fun LessonFactBlock(
    block: LessonBlock.Fact,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    var isAnswerVisible by remember { mutableStateOf(initialExpanded) }

    LessonFactBlockContent(
        block = block,
        isAnswerVisible = isAnswerVisible,
        onToggleVisibility = { isAnswerVisible = !isAnswerVisible },
        modifier = modifier
    )
}

@Composable
fun LessonFactBlockContent(
    block: LessonBlock.Fact,
    isAnswerVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(
                width = 0.dp,
                color = Orange.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Encabezado ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bulb),
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "DATO CURIOSO",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                color = Orange
            )
        }

        // --- Pregunta ---
        FormattedLessonText(
            text = block.question,
            style = KipuH4.copy(fontSize = 17.sp),
            color = KipuDarkBlue
        )

        // --- Botón de revelado ---
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onToggleVisibility)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isAnswerVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
                ),
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isAnswerVisible) "Ocultar dato" else "Mostrar dato",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Orange
            )
        }

        // --- Contenido animado ---
        AnimatedVisibility(
            visible = isAnswerVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FormattedLessonText(
                    text = block.answer,
                    style = KipuParagraph.copy(fontSize = 15.sp),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================

private val sampleFact = LessonBlock.Fact(
    question = "¿Sabías que Roslyn solo compila C# y Visual Basic?",
    answer = "El `SDK` contiene los compiladores y herramientas para programar y compilar. El `Runtime` (`CLR` + `BCL`) es el paquete mínimo necesario únicamente para ejecutar binarios ya creados."
)

@Preview(name = "LessonCueBlock - Collapsed", showBackground = true)
@Composable
private fun LessonCueBlockCollapsedPreview() {
    KipuCodeTheme {
        LessonFactBlockContent(
            block = sampleFact,
            isAnswerVisible = false,
            onToggleVisibility = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "LessonCueBlock - Expanded", showBackground = true)
@Composable
private fun LessonCueBlockExpandedPreview() {
    KipuCodeTheme {
        LessonFactBlockContent(
            block = sampleFact,
            isAnswerVisible = true,
            onToggleVisibility = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
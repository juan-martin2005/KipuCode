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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.LessonBlock
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuH4
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LessonCueBlock(
    block: LessonBlock.Cue,
    modifier: Modifier = Modifier
) {
    var isAnswerVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(
                width = 1.5.dp,
                color = KipuTeal.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Encabezado de la tarjeta Cue ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_quiz),
                contentDescription = null,
                tint = KipuTeal,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "PREGUNTA",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                color = KipuTeal
            )
        }

        // --- Pregunta ---
        FormattedLessonText(
            text = block.question,
            style = KipuH4.copy(fontSize = 17.sp),
            color = KipuDarkBlue
        )

        // --- Botón de revelado (Active Recall) ---
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { isAnswerVisible = !isAnswerVisible }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isAnswerVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
                ),
                contentDescription = null,
                tint = KipuTeal,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isAnswerVisible) "Ocultar respuesta" else "Ver respuesta",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = KipuTeal
            )
        }

        // --- Respuesta revelable con animación suave ---
        AnimatedVisibility(
            visible = isAnswerVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MoonFrost.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = BorderLightGray,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Respuesta:",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = KipuTeal
                )
                FormattedLessonText(
                    text = block.answer,
                    style = KipuParagraph.copy(fontSize = 14.sp),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Lesson Cue Block Preview")
@Composable
fun LessonCueBlockPreview() {
    LessonCueBlock(
        block = LessonBlock.Cue(
            question = "¿Cuál es la diferencia fundamental entre el `.NET SDK` y el `.NET Runtime`?",
            answer = "El `SDK` contiene los compiladores y herramientas para programar y compilar. El `Runtime` (`CLR` + `BCL`) es el paquete mínimo necesario únicamente para ejecutar binarios ya creados."
        ),
        modifier = Modifier.padding(16.dp)
    )
}

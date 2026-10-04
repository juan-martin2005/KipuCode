package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kipucode.R
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito

@Composable
fun CombinedExerciseTypeCard(
    title: String,
    description: String,
    totalCount: Int,
    iconRes: Int,
    accentColor: Color,
    containerColor: Color,
    exerciseType: String = "",
    onClick: () -> Unit
) {
    var showHelpBubble by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = KipuDarkBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$totalCount ejercicios disponibles",
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        color = Gray
                    )
                }

                // ÍCONO DE PREGUNTA CON GLOBO DE TEXTO FLOTANTE
                Box {
                    IconButton(
                        onClick = { showHelpBubble = !showHelpBubble },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_question),
                            contentDescription = "Cómo funciona este ejercicio",
                            tint = accentColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (showHelpBubble) {
                        Popup(
                            alignment = Alignment.TopEnd,
                            offset = IntOffset(x = 0, y = 80),
                            onDismissRequest = { showHelpBubble = false },
                            properties = PopupProperties(
                                focusable = true,
                                dismissOnClickOutside = true
                            )
                        ) {
                            ExerciseHelpSpeechBubble(
                                title = title,
                                exerciseType = exerciseType,
                                accentColor = accentColor,
                                onDismiss = { showHelpBubble = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontFamily = Nunito,
                fontSize = 12.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor,
                    shadowElevation = 1.dp,
                    modifier = Modifier.clickable(onClick = onClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Comencemos",
                            fontFamily = Nunito,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_filled),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- GLOBO DE TEXTO (SPEECH BUBBLE) ---
@Composable
private fun ExerciseHelpSpeechBubble(
    title: String,
    exerciseType: String,
    accentColor: Color,
    onDismiss: () -> Unit
) {
    val isFlashcard = exerciseType.equals("FLASHCARD", ignoreCase = true) ||
            title.contains("Memoria", ignoreCase = true) ||
            title.contains("Flashcard", ignoreCase = true)

    val (whatIsText, howToText) = if (isFlashcard) {
        Pair(
            "Tarjetas interactivas de dos caras diseñadas para activar el recuerdo voluntario y consolidar conceptos.",
            "1. Lee la consigna en el anverso e intenta recordar la solución en tu mente.\n" +
            "2. Toca la tarjeta para voltearla y comprobar la respuesta.\n" +
            "3. Califica con sinceridad qué tan fácil o difícil te resultó para sincronizar tu próximo repaso inteligente."
        )
    } else {
        Pair(
            "Preguntas de selección única para evaluar y reforzar tu comprensión teórica y de código.",
            "1. Lee con atención el enunciado.\n" +
            "2. Analiza las alternativas y elige la opción que consideres correcta.\n" +
            "3. Recibe retroalimentación inmediata con la explicación detallada de la respuesta."
        )
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.width(280.dp)
    ) {
        // Pico del globo de texto apuntando al ícono de pregunta
        Canvas(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(width = 14.dp, height = 8.dp)
        ) {
            val path = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path = path, color = Color.White)
            drawPath(
                path = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                },
                color = accentColor.copy(alpha = 0.35f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Cuerpo principal del globo
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Cómo funciona?",
                        fontFamily = Nunito,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_incorrect),
                            contentDescription = "Cerrar",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Text(
                    text = whatIsText,
                    fontFamily = Nunito,
                    fontSize = 12.sp,
                    color = KipuDarkBlue,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿CÓMO REALIZARLO?",
                    fontFamily = Nunito,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray
                )
                Text(
                    text = howToText,
                    fontFamily = Nunito,
                    fontSize = 11.5.sp,
                    color = Color(0xFF2C3E50),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                ) {
                    Text(
                        text = "Entendido",
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Card con Repasos Pendientes")
@Composable
fun CombinedExerciseTypeCardPendingPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        CombinedExerciseTypeCard(
            title = "Tarjetas de Memoria",
            description = "Repaso activo espaciado para consolidar conceptos en memoria.",
            totalCount = 10,
            iconRes = R.drawable.ic_flash_card,
            accentColor = KipuTealDark,
            containerColor = MoonFrost.copy(alpha = 0.3f),
            exerciseType = "FLASHCARD",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Card Al Día")
@Composable
fun CombinedExerciseTypeCardUpToDatePreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        CombinedExerciseTypeCard(
            title = "Opción Múltiple",
            description = "Preguntas prácticas y autoevaluación teórica de la lección.",
            totalCount = 8,
            iconRes = R.drawable.ic_quiz,
            accentColor = KipuTeal,
            containerColor = Color(0xFFF6F8FB),
            exerciseType = "UNIQUE_CHOICE",
            onClick = {}
        )
    }
}

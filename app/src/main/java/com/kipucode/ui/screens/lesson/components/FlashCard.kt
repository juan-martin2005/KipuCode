package com.kipucode.ui.screens.lesson.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Red

@Composable
fun Flashcard(
    currentFc: Int,
    totalFc: Int,
    instruction: String,
    answer: String,
    onCorrectClick: () -> Unit,
    onIncorrectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "flipAnimation"
    )
    val horizontalProgressFactor = currentFc.toFloat() / totalFc.toFloat()


    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // --- TARJETA (FLASHCARD) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { isFlipped = !isFlipped },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MoonFrost.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Contenido central (Pregunta o Respuesta)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation <= 90f) {
                        // --- CARA FRONTAL (Pregunta) ---
                        Text(
                            text = instruction,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = KipuDarkBlue,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // --- CARA TRASERA (Respuesta) ---
                        Text(
                            text = answer,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = KipuDarkBlue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    }
                }

                // --- SECCIÓN DE BOTONES ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón INCORRECTO
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(3.dp, Red.copy(alpha = 0.8f), CircleShape)
                            .clickable {
                                if (rotation == 180f || rotation == 0f) onIncorrectClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_incorrect),
                            contentDescription = "Incorrecto",
                            tint = Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Botón CORRECTO
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(3.dp, Green.copy(alpha = 0.8f), CircleShape)
                            .clickable {
                                if (rotation == 180f || rotation == 0f) onCorrectClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_correct),
                            contentDescription = "Correcto",
                            tint = Green.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    MoonFrost.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(horizontalProgressFactor)
                    .height(10.dp)
                    .background(
                        KipuTeal,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }

        // --- CONTADOR ---
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentFc.toString(),
                    color = KipuTealDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text(
                    text = " / $totalFc",
                    color = KipuDarkBlue.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardPreview() {
    KipuTopBar(
        title = "",
        onBackClick = {},
        modifier = Modifier.padding(top = 16.dp)
    )

    Flashcard(
        currentFc = 1,
        totalFc = 10,
        instruction = "Zen de Python: ¿Qué principio promueve 'Explicit is better than implicit'?",
        answer = "Significa que el código debe ser claro, directo y evitar 'magia' o comportamientos ocultos.",
        onCorrectClick = { },
        onIncorrectClick = { },
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}
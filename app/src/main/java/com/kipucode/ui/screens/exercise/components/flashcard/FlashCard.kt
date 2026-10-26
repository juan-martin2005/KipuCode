package com.kipucode.ui.screens.exercise.components.flashcard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.buttons.FilledButton
import com.kipucode.ui.screens.exercise.components.ExerciseProgressBar
import com.kipucode.ui.screens.exercise.components.formats.FormattedText
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuH4
import com.kipucode.ui.theme.KipuParagraph
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito

@Composable
fun Flashcard(
    current: Int,
    total: Int,
    instruction: String,
    answer: String,
    modifier: Modifier = Modifier,
    onRatingSelect: (rating: Int) -> Unit
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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRA DE PROGRESO REUTILIZABLE ---
        ExerciseProgressBar(
            current = current,
            total = total,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- TARJETA CON EFECTO 3D ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable {
                    isFlipped = !isFlipped
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MoonFrost.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation <= 90f) {
                        // --- CARA FRONTAL (Pregunta) ---
                        FormattedText(
                            text = instruction,
                            style = KipuH4.copy(fontSize = 18.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // --- CARA TRASERA (Respuesta) ---
                        FormattedText(
                            text = answer,
                            style = KipuParagraph,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { rotationY = 180f }
                        )
                    }
                }

                Icon(
                    imageVector = ImageVector.vectorResource(
                        R.drawable.ic_bent_arrow_right
                    ),
                    tint = LocalContentColor.current.copy(alpha = 0.2f),
                    contentDescription = "Girar tarjeta",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.End)
                        .padding(5.dp)
                        .scale(scaleX = -1f, scaleY = -1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.rating_question),
            fontSize = 16.sp,
            fontFamily = Nunito
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN DE BOTONES DE EVALUACIÓN FSRS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledButton(
                textButton = stringResource(R.string.rating_again),
                onClickFilledButton = {
                    isFlipped = false
                    onRatingSelect(1)
                },
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                containerColor = Color(0xFFF1B8B7),
                contentColor = Color(0xFFA9302E)
            )
            FilledButton(
                textButton = stringResource(R.string.rating_hard),
                onClickFilledButton = {
                    isFlipped = false
                    onRatingSelect(2)
                },
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                containerColor = Color(0xFFFFDE98),
                contentColor = Color(0xFFA2750A)
            )
            FilledButton(
                textButton = stringResource(R.string.rating_good),
                onClickFilledButton = {
                    isFlipped = false
                    onRatingSelect(3)
                },
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                containerColor = Color(0xFF9DC1FF),
                contentColor = Color(0xFF12489A)
            )
            FilledButton(
                textButton = stringResource(R.string.rating_easy),
                onClickFilledButton = {
                    isFlipped = false
                    onRatingSelect(4)
                },
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                containerColor = Color(0xFF9DB98E),
                contentColor = Color(0xFF1B5E20)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, name = "Flashcard - Default")
@Composable
private fun FlashcardPreview() {
    KipuCodeTheme {
        Flashcard(
            current = 4,
            total = 15,
            instruction = "Zen de Python: ¿Qué principio promueve `Explicit is better than implicit`?",
            answer = "Significa que el código debe ser `claro`, `directo` y evitar **'magia'** o comportamientos ocultos.",
            modifier = Modifier.padding(horizontal = 24.dp),
            onRatingSelect = {}
        )
    }
}

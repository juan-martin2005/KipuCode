package com.kipucode.ui.screens.exercise.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.screens.lesson.components.ContentMarkdown
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import kotlin.math.roundToInt

@Composable
fun Flashcard(
    currentFc: Int,
    totalFc: Int,
    instruction: String,
    answer: String,
    module : String,
    modifier: Modifier = Modifier,
    onRatingSelect : (rating : Int) -> Unit
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
    val progressPorcentaje = horizontalProgressFactor * 100

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)

        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_terminal_rounded),
                contentDescription = "Terminal icon",
                modifier = Modifier.size(18.dp),
                tint = KipuTealDark

            )

            Text(
                text = "MODULO: ${module.uppercase()}",
                fontSize = 13.sp,
                fontFamily = Nunito,
                modifier = Modifier.weight(1f)

            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .height(60.dp)
                .background(
                    MoonFrost.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
            ,
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentFc.toString(),
                        color = KipuTealDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        fontFamily = Nunito
                    )
                    Text(
                        text = " / $totalFc \t\tTarjetas",
                        color = KipuDarkBlue.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontFamily = Nunito
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = " ${progressPorcentaje.roundToInt()}% COMPLETADO",
                        color = KipuTealDark,
                        fontSize = 12.sp,
                        fontFamily = Nunito
                    )
                }

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth()
                        .background(
                            color = MoonFrost,
                            shape = RoundedCornerShape(4.dp)
                        )

                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(horizontalProgressFactor)
                            .height(8.dp)
                            .background(
                                KipuTeal,
                                shape = RoundedCornerShape(4.dp)
                            )

                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

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
                        ContentMarkdown(modifier = Modifier, content = instruction)
                    } else {
                        // --- CARA TRASERA (Respuesta) ---
                        ContentMarkdown(
                            modifier = Modifier.graphicsLayer { rotationY = 180f },
                            content = answer
                        )
                        //
                    }
                }

                Icon(
                    imageVector = ImageVector.vectorResource(
                        if(rotation <= 90f) R.drawable.ic_bent_arrow_right else R.drawable.ic_bent_arrow_left
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.End)
                        .padding(5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.rating_question),
            fontSize = 16.sp,
            fontFamily = Nunito

        )

        Spacer(modifier = Modifier.height(24.dp))


        // --- SECCIÓN DE BOTONES ---
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            FilledButton(
                textButton = stringResource(R.string.rating_again),
                onClickFilledButton = {
                    isFlipped = false
                    onRatingSelect(1)
                },
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                containerColor = Color(0xFFF1B8B7),
                contentColor = Color(0xFFA9302E)
            )
            FilledButton(
                textButton = stringResource(R.string.rating_hard),
                onClickFilledButton = {
                    isFlipped = false
                    onRatingSelect(2)
                },
                modifier = Modifier.weight(1.1f),
                fontSize = 15.sp,
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
                fontSize = 15.sp,
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
                fontSize = 15.sp,
                containerColor = Color(0xFF9DB98E)
            )
        }


        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardPreview() {
    Flashcard(
        currentFc = 4,
        totalFc = 15,
        instruction = "Zen de Python: ¿Qué principio promueve `Explicit is better than implicit`?",
        answer = "Significa que el código debe ser `claro`, `directo` y evitar **'magia'** o comportamientos ocultos.",
        module = "Gestion de errores",
        modifier = Modifier.padding(horizontal = 24.dp),
        onRatingSelect = {}
    )
}
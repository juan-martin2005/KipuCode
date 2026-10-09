package com.kipucode.ui.screens.exercise.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

/**
 * Componente modular y reutilizable para representar la barra de progreso
 * en cualquier sesión de ejercicios (opción múltiple, completar bloques, tarjetas, etc.).
 *
 * Incluye contador actual/total, porcentaje dinámico y animación fluida del llenado.
 */
@Composable
fun ExerciseProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val targetFactor = if (total > 0) (current.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedFactor by animateFloatAsState(
        targetValue = targetFactor,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "exercise_progress_bar_animation"
    )
    val percentage = (targetFactor * 100).toInt()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- FILA DE CONTADOR Y PORCENTAJE ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = current.toString(),
                color = lerp(KipuTeal, Color.Black, 0.1f),
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier
                    .alignByBaseline()
                    .padding(horizontal = 2.dp),
                text = "/",
                color = KipuDarkBlue.copy(alpha = 0.5f),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = "$total",
                color = KipuDarkBlue.copy(alpha = 0.5f),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            if (!label.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = label,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = KipuDarkBlue.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.alignByBaseline(),
                text = "$percentage% COMPLETADO",
                color = lerp(KipuTeal, Color.Black, 0.1f),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        // --- BARRA DE PROGRESO ANIMADA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    KipuDarkBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFactor)
                    .height(10.dp)
                    .background(
                        KipuTeal,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Preview(showBackground = true, name = "Exercise Progress Bar Preview")
@Composable
fun ExerciseProgressBarPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExerciseProgressBar(current = 2, total = 5)
        ExerciseProgressBar(current = 4, total = 10, label = "Preguntas")
    }
}

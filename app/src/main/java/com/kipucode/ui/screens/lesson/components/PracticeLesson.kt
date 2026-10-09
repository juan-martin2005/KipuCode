package com.kipucode.ui.screens.lesson.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.Green
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.LightGreen
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White


@Composable
fun PracticeMethodSelector(
    availableMethods: List<PracticeMethod>,
    selectedMethod: PracticeMethod,
    onMethodSelected: (PracticeMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    PracticeMethodSelectorContent(
        availableMethods = availableMethods,
        selectedMethod = selectedMethod,
        onMethodSelected = onMethodSelected,
        modifier = modifier
    )
}


@Composable
fun PracticeMethodSelectorContent(
    availableMethods: List<PracticeMethod>,
    selectedMethod: PracticeMethod,
    onMethodSelected: (PracticeMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    if (availableMethods.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Separador sutil entre el contenido teórico y la sección de práctica
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = 1.dp,
            color = BorderLightGray
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Modo de práctica",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KipuDarkBlue
            )
            Text(
                text = "Elige cómo deseas evaluar y reforzar lo aprendido.",
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Gray
            )
        }

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Opción única
            if (availableMethods.contains(PracticeMethod.UNIQUE_CHOICE)) {
                PracticeMethodCard(
                    title = "Opción única",
                    subtitle = "Evaluación conceptual",
                    badgeText = "Rápido · 2 min",
                    description = "Preguntas con alternativas para validar conceptos y sintaxis clave.",
                    detailedDescription = "Modo de evaluación inmediata con retroalimentación instantánea: se te presentan preguntas de opción múltiple con justificación detallada de cada alternativa tras responder.",
                    iconRes = R.drawable.ic_multiple_choice,
                    selected = selectedMethod == PracticeMethod.UNIQUE_CHOICE,
                    onClick = { onMethodSelected(PracticeMethod.UNIQUE_CHOICE) }
                )
            }

            // 2. Tarjetas de memoria (FSRS-6)
            if (availableMethods.contains(PracticeMethod.DEFAULT_FLASHCARDS)) {
                PracticeMethodCard(
                    title = "Tarjetas de memoria",
                    subtitle = "Repaso espaciado · FSRS-6",
                    badgeText = "Recomendado",
                    isRecommended = true,
                    description = "Recuerda la respuesta activamente antes de voltear cada tarjeta.",
                    detailedDescription = "Algoritmo de repetición espaciada FSRS-6 que calcula el intervalo óptimo para repasar cada tarjeta según tu dificultad percibida (Otra vez, Difícil, Bien, Fácil), maximizando la retención a largo plazo.",
                    iconRes = R.drawable.ic_flashcards,
                    selected = selectedMethod == PracticeMethod.DEFAULT_FLASHCARDS,
                    onClick = { onMethodSelected(PracticeMethod.DEFAULT_FLASHCARDS) }
                )
            }

            // 3. Tarjetas con Técnica Feynman
            if (availableMethods.contains(PracticeMethod.FEYNMAN_FLASHCARDS)) {
                PracticeMethodCard(
                    title = "Técnica Feynman",
                    subtitle = "Aprendizaje profundo",
                    badgeText = "Explicación activa",
                    description = "Explica el concepto con tus propias palabras con sencillez antes de comparar la tarjeta.",
                    detailedDescription = "Antes de voltear la tarjeta, formula una explicación clara y sencilla en voz alta o mentalmente como si se la enseñaras a un principiante de 10 años. Luego compara tu explicación con la definición técnica.",
                    iconRes = R.drawable.ic_flash_card,
                    selected = selectedMethod == PracticeMethod.FEYNMAN_FLASHCARDS,
                    onClick = { onMethodSelected(PracticeMethod.FEYNMAN_FLASHCARDS) }
                )
            }
        }
    }
}


@Composable
fun PracticeMethodCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    description: String,
    detailedDescription: String? = null,
    iconRes: Int,
    selected: Boolean,
    badgeText: String? = null,
    isRecommended: Boolean = false,
    onClick: () -> Unit,
    initialExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    PracticeMethodCardContent(
        title = title,
        subtitle = subtitle,
        badgeText = badgeText,
        description = description,
        detailedDescription = detailedDescription,
        iconRes = iconRes,
        selected = selected,
        isRecommended = isRecommended,
        isExpanded = isExpanded,
        onToggleExpand = { isExpanded = !isExpanded },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun PracticeMethodCardContent(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    description: String,
    detailedDescription: String?,
    iconRes: Int,
    selected: Boolean,
    isRecommended: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        if (selected) KipuTeal else BorderLightGray,
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        if (selected) KipuTeal.copy(alpha = 0.04f) else White,
        label = "bgColor"
    )

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevronRotation"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.RadioButton, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Cabecera principal: Ícono + Títulos + Badge + RadioButton
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = if (selected) KipuTeal else Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = KipuDarkBlue
                            )
                            if (isRecommended) {
                                Surface(
                                    color = LightGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Recomendado",
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Green,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (badgeText != null) {
                                Surface(
                                    color = BorderLightGray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Gray,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = subtitle,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Gray
                        )
                    }
                }

                RadioButton(
                    selected = selected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = KipuTeal,
                        unselectedColor = Gray
                    )
                )
            }

            // 2. Fila inferior: Descripción corta + Flecha desplegable a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = description,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Gray,
                    modifier = Modifier.weight(1f)
                )

                if (detailedDescription != null) {
                    Surface(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onToggleExpand)
                            .padding(6.dp),
                        color = androidx.compose.ui.graphics.Color.Transparent
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = if (isExpanded) "Ocultar detalles" else "Ver detalles",
                            tint = if (selected) KipuTeal else Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(chevronRotation)
                        )
                    }
                }
            }

            // 3. Bloque de detalle con fondo blanco limpio y borde sutil
            if (detailedDescription != null) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = White,
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
                            text = "Detalles del modo:",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = KipuTeal
                        )
                        Text(
                            text = detailedDescription,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = KipuDarkBlue
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================================
// PREVIEWS
// ============================================================================================

@Preview(name = "1. Tarjeta Colapsada (No seleccionada)", showBackground = true)
@Composable
private fun PracticeMethodCardUnselectedPreview() {
    PracticeMethodCardContent(
        title = "Opción única",
        subtitle = "Evaluación conceptual",
        description = "Preguntas con alternativas para validar conceptos y sintaxis clave.",
        detailedDescription = "Modo de evaluación inmediata con retroalimentación instantánea: se te presentan preguntas de opción múltiple con justificación detallada de cada alternativa tras responder.",
        iconRes = R.drawable.ic_multiple_choice,
        selected = false,
        isRecommended = false,
        isExpanded = false,
        onToggleExpand = {},
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "2. Tarjeta Desplegada (Seleccionada)", showBackground = true)
@Composable
private fun PracticeMethodCardSelectedPreview() {
    PracticeMethodCardContent(
        title = "Tarjetas de memoria",
        subtitle = "Repaso espaciado · FSRS-6",
        description = "Recuerda la respuesta activamente antes de voltear cada tarjeta.",
        detailedDescription = "Algoritmo de repetición espaciada FSRS-6 que calcula el intervalo óptimo para repasar cada tarjeta según tu dificultad percibida (Otra vez, Difícil, Bien, Fácil), maximizando la retención a largo plazo.",
        iconRes = R.drawable.ic_flashcards,
        selected = true,
        isRecommended = true,
        isExpanded = true,
        onToggleExpand = {},
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "3. Selector Completo", showBackground = true)
@Composable
private fun PracticeMethodSelectorFullPreview() {
    var selected by remember { mutableStateOf(PracticeMethod.DEFAULT_FLASHCARDS) }

    Surface(color = BackgroundGray, modifier = Modifier.padding(16.dp)) {
        PracticeMethodSelectorContent(
            availableMethods = listOf(
                PracticeMethod.UNIQUE_CHOICE,
                PracticeMethod.DEFAULT_FLASHCARDS,
                PracticeMethod.FEYNMAN_FLASHCARDS
            ),
            selectedMethod = selected,
            onMethodSelected = { selected = it }
        )
    }
}

package com.kipucode.ui.screens.code.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
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
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

/**
 * Componente Stateful para el selector de ejercicios de código.
 */
@Composable
fun PracticeCodeSelector(
    selectedMethod: PracticeMethod,
    completeCodeCount: Int,
    whatsOutputCount: Int,
    onMethodSelected: (PracticeMethod) -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = false
) {
    PracticeCodeSelectorContent(
        selectedMethod = selectedMethod,
        completeCodeCount = completeCodeCount,
        whatsOutputCount = whatsOutputCount,
        onMethodSelected = onMethodSelected,
        modifier = modifier,
        showHeader = showHeader
    )
}

/**
 * Componente Stateless / "Tonto" desacoplado para previews y pruebas directas.
 */
@Composable
fun PracticeCodeSelectorContent(
    selectedMethod: PracticeMethod,
    completeCodeCount: Int,
    whatsOutputCount: Int,
    onMethodSelected: (PracticeMethod) -> Unit,
    modifier: Modifier = Modifier,
    showHeader: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (showHeader) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Entrenamiento de código",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    color = KipuDarkBlue
                )
                Text(
                    text = "Desarrolla lógica mental y dominio de sintaxis en tus temas completados.",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    color = Gray
                )
            }
        }

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Completar código
            val isCompleteLocked = completeCodeCount == 0
            PracticeCodeCard(
                title = "Completar código",
                subtitle = "Sintaxis y estructura",
                badgeText = if (!isCompleteLocked) "Bloques interactivos" else null,
                description = "Arrastra y encaja bloques de código para estructurar la solución correcta.",
                detailedDescription = "Construcción lógica por ranuras: te ayuda a interiorizar la estructura y orden de ejecución del lenguaje sin tener que lidiar con errores de tipeo en celular.",
                iconRes = R.drawable.ic_code,
                selected = selectedMethod == PracticeMethod.COMPLETE_CODE,
                isLocked = isCompleteLocked,
                onClick = {
                    if (!isCompleteLocked) onMethodSelected(PracticeMethod.COMPLETE_CODE)
                }
            )

            // 2. ¿Cuál es la salida?
            val isWhatsOutputLocked = whatsOutputCount == 0
            PracticeCodeCard(
                title = "¿Cuál es la salida?",
                subtitle = "Rastreo mental",
                badgeText = if (!isWhatsOutputLocked) "Depuración lógica" else null,
                description = "Analiza el fragmento de código e identifica qué imprime o devuelve la consola.",
                detailedDescription = "Lectura crítica y ejecución mental: ejercitas tu mente para interpretar variables, condiciones y bucles prediciendo el resultado exacto como un compilador.",
                iconRes = R.drawable.ic_terminal_rounded,
                selected = selectedMethod == PracticeMethod.WHATS_OUTPUT,
                isLocked = isWhatsOutputLocked,
                onClick = {
                    if (!isWhatsOutputLocked) onMethodSelected(PracticeMethod.WHATS_OUTPUT)
                }
            )
        }
    }
}
/**
 * Componente Stateful de cada tarjeta de código (gestiona isExpanded).
 */
@Composable
fun PracticeCodeCard(
    title: String,
    subtitle: String,
    description: String,
    detailedDescription: String? = null,
    iconRes: Int,
    selected: Boolean,
    isLocked: Boolean = false,
    badgeText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    PracticeCodeCardContent(
        title = title,
        subtitle = subtitle,
        badgeText = badgeText,
        description = description,
        detailedDescription = detailedDescription,
        iconRes = iconRes,
        selected = selected,
        isLocked = isLocked,
        isExpanded = isExpanded,
        onToggleExpand = { isExpanded = !isExpanded },
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * Componente Stateless: tarjeta con cabecera, descripción corta, flecha giratoria y detalles desplegables.
 */
@Composable
fun PracticeCodeCardContent(
    title: String,
    subtitle: String,
    description: String,
    detailedDescription: String?,
    iconRes: Int,
    selected: Boolean,
    isLocked: Boolean,
    badgeText: String?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isLocked -> BorderLightGray.copy(alpha = 0.6f)
            selected -> KipuTeal
            else -> BorderLightGray
        },
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isLocked -> White
            selected -> KipuTeal.copy(alpha = 0.04f)
            else -> White
        },
        label = "bgColor"
    )
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevronRotation"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(if (selected && !isLocked) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = !isLocked,
                role = Role.RadioButton,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Cabecera principal: Icono + Título/Badge/Subtítulo + RadioButton / Candado
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
                        tint = if (selected && !isLocked) KipuTeal else Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                color = if (isLocked) Gray else KipuDarkBlue
                            )
                            if (isLocked) {
                                Surface(
                                    color = BorderLightGray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_lock),
                                            contentDescription = null,
                                            tint = Gray,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = "Bloqueado",
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                            color = Gray
                                        )
                                    }
                                }
                            } else if (badgeText != null) {
                                Surface(
                                    color = KipuTeal.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontFamily = Nunito,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                        color = KipuTeal,
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
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            color = Gray
                        )
                    }
                }

                // Indicador derecho: Candado fijo si está bloqueado, RadioButton si está habilitado
                if (isLocked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Bloqueado",
                        tint = Gray,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    RadioButton(
                        selected = selected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = KipuTeal,
                            unselectedColor = Gray
                        )
                    )
                }
            }

            // 2. Fila descriptiva: Explicación corta + Botón desplegable a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isLocked) "Completa lecciones para desbloquear este modo de práctica." else description,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    color = Gray,
                    modifier = Modifier.weight(1f)
                )

                if (detailedDescription != null && !isLocked) {
                    Surface(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onToggleExpand)
                            .padding(4.dp),
                        color = Color.Transparent
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

            // 3. Bloque desplegable de detalles
            if (detailedDescription != null && !isLocked) {
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
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            color = KipuTeal
                        )
                        Text(
                            text = detailedDescription,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            color = KipuDarkBlue
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================================
// PREVIEWS EN ANDROID STUDIO
// ============================================================================================

@Preview(name = "1. Tarjeta Desbloqueada", showBackground = true)
@Composable
private fun PracticeCodeCardUnlockedPreview() {
    PracticeCodeCardContent(
        title = "Completar código",
        subtitle = "Sintaxis y estructura",
        badgeText = "Bloques interactivos",
        description = "Arrastra y encaja bloques de código para estructurar la solución correcta.",
        detailedDescription = "Construcción lógica por ranuras: te ayuda a interiorizar la estructura y orden de ejecución del lenguaje sin tener que lidiar con errores de tipeo en celular.",
        iconRes = R.drawable.ic_code,
        selected = true,
        isLocked = false,
        isExpanded = false,
        onToggleExpand = {},
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "2. Tarjeta Bloqueada", showBackground = true)
@Composable
private fun PracticeCodeCardLockedPreview() {
    PracticeCodeCardContent(
        title = "¿Cuál es la salida?",
        subtitle = "Rastreo mental",
        badgeText = null,
        description = "Analiza el fragmento de código e identifica qué imprime o devuelve la consola.",
        detailedDescription = "Lectura crítica y ejecución mental: ejercitas tu mente para interpretar variables, condiciones y bucles prediciendo el resultado exacto como un compilador.",
        iconRes = R.drawable.ic_terminal_rounded,
        selected = false,
        isLocked = true,
        isExpanded = false,
        onToggleExpand = {},
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(name = "3. Selector Completo (1 activo, 1 bloqueado)", showBackground = true)
@Composable
private fun PracticeCodeSelectorPreview() {
    var selected by remember { mutableStateOf(PracticeMethod.COMPLETE_CODE) }

    Surface(color = BackgroundGray, modifier = Modifier.padding(16.dp)) {
        PracticeCodeSelectorContent(
            selectedMethod = selected,
            completeCodeCount = 1,
            whatsOutputCount = 0,
            onMethodSelected = { selected = it }
        )
    }
}

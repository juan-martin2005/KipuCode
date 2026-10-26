package com.kipucode.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.AmberBrown
import com.kipucode.ui.theme.Coffee
import com.kipucode.ui.theme.DarkCoffeeBrown
import com.kipucode.ui.theme.EspressoBrown
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.LightPeach
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

@Composable
fun UrgentReviewsCard(
    onStartClick: () -> Unit,
    onLaterClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    UrgentReviewsContent(
        isExpanded = isExpanded,
        onToggleExpand = { isExpanded = !isExpanded },
        onStartClick = onStartClick,
        onLaterClick = onLaterClick,
        modifier = modifier
    )
}

@Composable
fun UrgentReviewsContent(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onStartClick: () -> Unit,
    onLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LightPeach,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // --- Encabezado compacto ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícono de reloj de arena proporcionado
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = White.copy(alpha = 0.85f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sandglass),
                        contentDescription = null,
                        tint = AmberBrown,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Títulos e información
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Repaso urgente",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = EspressoBrown
                        )

                        // Chip de tiempo nivelado
                        Surface(
                            shape = CircleShape,
                            color = White.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = "10 min",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = AmberBrown
                            )
                        }
                    }

                    // Subtítulo con disparador para expandir/colapsar
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onToggleExpand)
                            .padding(vertical = 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Tienes repasos pendientes",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AmberBrown
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = if (isExpanded) "Ocultar detalle" else "Ver detalle",
                            tint = AmberBrown,
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(arrowRotation)
                        )
                    }
                }
            }

            // --- Explicación expandible animada ---
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = "Detectamos conceptos en riesgo según tu curva de retención. Refuérzalos antes de continuar con nuevo material.",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = DarkCoffeeBrown,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp, top = 4.dp)
                )
            }

            // --- Botones de acción alineados ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onLaterClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Coffee),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Más tarde",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = onStartClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberBrown,
                        contentColor = White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "Comenzar",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "UrgentReviews - Collapsed", showBackground = true)
@Composable
private fun UrgentReviewsCollapsedPreview() {
    KipuCodeTheme {
        UrgentReviewsContent(
            isExpanded = false,
            onToggleExpand = {},
            onStartClick = {},
            onLaterClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "UrgentReviews - Expanded", showBackground = true)
@Composable
private fun UrgentReviewsExpandedPreview() {
    KipuCodeTheme {
        UrgentReviewsContent(
            isExpanded = true,
            onToggleExpand = {},
            onStartClick = {},
            onLaterClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

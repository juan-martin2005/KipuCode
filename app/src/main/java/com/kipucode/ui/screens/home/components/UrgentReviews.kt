package com.kipucode.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.*

@Composable
fun UrgentReviewsCard(
    onStartClick: () -> Unit,
    onLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UrgentReviewsContent(
        onStartClick = onStartClick,
        onLaterClick = onLaterClick,
        modifier = modifier
    )
}

@Composable
fun UrgentReviewsScreen(
    modifier: Modifier = Modifier
) {
    UrgentReviewsContent(
        onStartClick = {},
        onLaterClick = {},
        modifier = modifier
    )
}

@Composable
fun UrgentReviewsContent(
    onStartClick: () -> Unit,
    onLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = LightPeach,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = White.copy(alpha = 0.8f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_sandglass
                        ),
                        contentDescription = null,
                        tint = AmberBrown,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Repaso urgente",
                            modifier = Modifier.weight(1f),
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = EspressoBrown
                        )

                        Surface(
                            shape = CircleShape,
                            color = White.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "10 min",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 3.dp
                                ),
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AmberBrown
                            )
                        }
                    }

                    Text(
                        text = "Tienes repasos pendientes",
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        color = AmberBrown
                    )
                }
            }

            Text(
                text = "Detectamos conceptos en riesgo según tu curva de " +
                        "retención. Refuérzalos antes de continuar con nuevo material.",
                fontFamily = Nunito,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                color = DarkCoffeeBrown
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onLaterClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Coffee
                    )
                ) {
                    Text(
                        text = "Más tarde",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onStartClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberBrown,
                        contentColor = White
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 12.dp
                    )
                ) {
                    Text(
                        text = "Comenzar",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UrgentReviewsContentPreview() {
    KipuCodeTheme {
        UrgentReviewsContent(
            onStartClick = {},
            onLaterClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
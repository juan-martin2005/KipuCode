package com.kipucode.ui.components.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.Red
import com.kipucode.ui.theme.White

@Composable
fun MultipleChoicesCard(
    text: String,
    @DrawableRes iconRes: Int? = null,
    orderIndex: Int? = null,
    disableArrow: Boolean = false,
    isRed: Boolean = false,
    isFirst: Boolean = false,
    isMiddle: Boolean = false,
    isEnd: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (isRed) Red else KipuDarkBlue

    val cardShape = when {
        isFirst -> RoundedCornerShape(
            topStart = 16.dp, topEnd = 16.dp,
            bottomStart = 0.dp, bottomEnd = 0.dp
        )
        isMiddle -> RoundedCornerShape(0.dp)
        isEnd -> RoundedCornerShape(
            topStart = 0.dp, topEnd = 0.dp,
            bottomStart = 16.dp, bottomEnd = 16.dp
        )
        else -> RoundedCornerShape(16.dp)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderLightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Personalizado
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            } else if (orderIndex != null) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = orderIndex.toString(),
                        color = contentColor,
                        fontSize = 18.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texto Central
            Text(
                text = text,
                color = contentColor,
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            // Icono Flecha
            if (!disableArrow) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Go forward",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F4F6)
@Composable
fun ProfileMenuCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        MultipleChoicesCard(
            text = "Feedback",
            iconRes = R.drawable.ic_feedback,
            isFirst = true,
            onClick = {}
        )

        MultipleChoicesCard(
            text = "Cerrar sesión",
            iconRes = R.drawable.ic_exit,
            isRed = true,
            disableArrow = true,
            isMiddle = true,
            onClick = {}
        )
        MultipleChoicesCard(
            text = "Utiliza en Python para mostrar texto ",
            orderIndex = 3,
            disableArrow = true,
            isEnd = true,
            onClick = {}
        )
    }
}
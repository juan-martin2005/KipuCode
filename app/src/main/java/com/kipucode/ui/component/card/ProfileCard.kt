package com.kipucode.ui.component.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Nunito

@Composable
fun ProfileMenuCard(
    text: String,
    @DrawableRes iconRes: Int,
    isRed: Boolean = false,
    isFirst: Boolean = false,
    isMiddle: Boolean = false,
    isEnd: Boolean = false,
    onClick: () -> Unit
) {
    val lightGray = Color(0xFFE5E7EB)
    val contentColor = if (isRed) Color(0xFF9d0007) else Color(0xFF081c40)

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, lightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Personalizado
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

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
            if (!isRed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Go forward",
                    tint = Color.Gray,
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
        ProfileMenuCard(
            text = "Feedback",
            iconRes = R.drawable.ic_feedback,
            isFirst = true,
            onClick = {}
        )

        ProfileMenuCard(
            text = "Cerrar sesión",
            iconRes = R.drawable.ic_exit,
            isRed = true,
            isEnd = true,
            onClick = {}
        )
    }
}
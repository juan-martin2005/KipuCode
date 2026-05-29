package com.kipucode.ui.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Nunito

@Composable
fun ProfileMenuCard(
    text: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val lightGray = Color(0xFFE5E7EB)
    val contentColor = if (isDestructive) Color(0xFFE53935) else Color(0xFFD5D5D5)
    val darkBlue = Color(0xFF081c40)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onClick()},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, lightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
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
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Ir",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
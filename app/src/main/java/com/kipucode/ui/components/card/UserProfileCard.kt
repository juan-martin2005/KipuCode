package com.kipucode.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

@Composable
fun UserProfileCard(
    name: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = KipuTealDark,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- SECCIÓN 1: AVATAR ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Avatar de Usuario",
                    tint = White.copy(alpha = 0.85f),
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // --- SECCIÓN 2: TEXTO ---
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    color = White,
                    fontSize = 18.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = email,
                    color = White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFf6f7f9))
            .padding(16.dp)
    ) {
        UserProfileCard(
            name = "User Test Alpha",
            email = "n00123456@upn.pe"
        )
    }
}
package com.kipucode.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
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
    modifier: Modifier = Modifier,
    avatarId : Int,
    onClick: () -> Unit
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
                    .background(color = Color.Transparent)
                    .size(72.dp)
                    .clickable(onClick = onClick )
            ) {
                Image(
                    painter = painterResource(id = avatarId),
                    contentDescription = "Avatar de Usuario",
                    modifier = Modifier.size(90.dp)
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                    contentDescription = "Editar",
                    modifier = Modifier
                        .clip(CircleShape)
                        .align(Alignment.TopEnd)
                        .background(color = Color.Transparent)
                        .size(19.dp),
                    tint = Color.DarkGray
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
            email = "n00123456@upn.pe",
            avatarId = R.drawable.avatar_002,
            onClick = {}
        )
    }
}
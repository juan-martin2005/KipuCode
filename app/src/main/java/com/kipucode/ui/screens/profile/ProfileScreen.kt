package com.kipucode.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.ui.component.card.ProfileMenuCard
import com.kipucode.ui.component.card.UserProfileCard
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.UserViewModel


@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    onLogoutClick: () -> Unit
) {
    val userProfile by userViewModel.userProfile.collectAsStateWithLifecycle()

    ProfileContent(
        name = userProfile?.name ?: "",
        email = userProfile?.email ?: "",
        onFeedbackClick = {
        },
        onLogoutClick = {
            onLogoutClick()
        }
    )
}


@Composable
fun ProfileContent(
    name: String,
    email: String,
    onFeedbackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val darkBlue = Color(0xFF081c40)
    val backgroundColor = Color(0xFFf6f7f9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // --- SECCIÓN 1: TÍTULO ---
        Text(
            text = "Mi Perfil",
            fontSize = 28.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            color = darkBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECCIÓN 2: TARJETA DE USUARIO ---
        UserProfileCard(
            name = name,
            email = email,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN 3: MENÚ EN BLOQUE ---
        ProfileMenuCard(
            text = "Feedback",
            iconRes = R.drawable.ic_feedback,
            isFirst = true,
            onClick = onFeedbackClick
        )
        ProfileMenuCard(
            text = "Cerrar sesión",
            iconRes = R.drawable.ic_exit,
            isRed = true,
            isEnd = true,
            onClick = onLogoutClick
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ProfileScreenPreview() {
//    ProfileContent(
//        name = "Pepeito Gonzles",
//        email = "pedro@upn.pe",
//        onFeedbackClick = {},
//        onLogoutClick = {}
//    )
//}
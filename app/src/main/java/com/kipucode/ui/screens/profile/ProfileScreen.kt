package com.kipucode.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kipucode.ui.component.card.ProfileMenuCard
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
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

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN 2: OPCIÓN CERRAR SESIÓN ---
        ProfileMenuCard(
            text = "Cerrar sesión",
            isDestructive = true,
            onClick = {
                authViewModel.logout()
                onLogoutClick()
            }
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ProfileScreenPreview() {
//    ProfileScreen(
//        onLogoutClick = {}
//    )
//}
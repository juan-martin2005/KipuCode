package com.kipucode.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.Response
import com.kipucode.ui.component.card.ProfileMenuCard
import com.kipucode.ui.component.card.UserProfileCard
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.UserViewModel


@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    onLogoutClick: () -> Unit
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    when (val user = userState) {
        is Response.Loading -> {}
        is Response.Success -> {
            userName = user.data.name
            userEmail = user.data.email
        }
        is Response.Error -> {
            Log.d("PROFILE_ERROR", user.message ?: "Error desconocido")
        }
        null -> {}
    }

    ProfileContent(
        name = userName,
        email = userEmail,
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
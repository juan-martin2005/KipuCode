package com.kipucode.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.Response
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.text_field.KipuForm
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel

// --- SCREEN ---
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val resetState by authViewModel.resetPasswordState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(resetState) {
        when (resetState) {
            is Response.Loading -> {}
            is Response.Success -> {
//                Toast.makeText(context, "Correo de recuperación enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                authViewModel.resetForgotPasswordState()
                onBack()
            }
            is Response.Error -> {
                val errorMsg = (resetState as Response.Error).message
                emailError = errorMsg ?: "Ocurrió un error inesperado"
                Log.d("FIREBASE_ERROR", errorMsg ?: "Unknown Error")
            }
            null -> {}
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
    ) { paddingValues ->
        ForgotPasswordScreenContent(
            email = email,
            emailError = emailError,
            onEmailChange = {
                email = it
                emailError = null
            },
            onBackClick = onBack,
            onSendClick = {
                var hasError = false
                val emailTrimmed = email.trim().lowercase()

                if (emailTrimmed.isBlank()) {
                    emailError = "El correo es obligatorio"
                    hasError = true
                } else if (!emailTrimmed.endsWith("@upn.pe")) {
                    emailError = "Debe usar el correo institucional"
                    hasError = true
                }

                if (!hasError) {
                    authViewModel.resetPassword(emailTrimmed)
                }
            },
            isLoading = resetState is Response.Loading,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// --- CONTENT ---
@Composable
fun ForgotPasswordScreenContent(
    email: String,
    emailError: String?,
    onEmailChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Botón Atrás
        IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Atrás",
                tint = Color(0xFF081c40),
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.reset_password),
                fontSize = 32.sp,
                fontFamily = Nunito,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.subheadline_forgot_password),
                fontSize = 16.sp,
                fontFamily = Nunito,
                lineHeight = 18.sp,
                color = Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            KipuForm(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = onEmailChange,
                placeholder = "n00123456@upn.pe",
                iconRes = R.drawable.ic_mail,
                keyboardType = KeyboardType.Email,
                isError = emailError != null,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilledButton(
                textButton = stringResource(R.string.send_email),
                onClickFilledButton = onSendClick,
                isLoading = isLoading
            )
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, name = "Recuperar Contraseña")
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        Scaffold(containerColor = BackgroundGray) { padding ->
            ForgotPasswordScreenContent(
                email = "",
                emailError = null,
                onEmailChange = {},
                onBackClick = {},
                onSendClick = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Preview(showBackground = true, name = "Recuperar Contraseña (Con Error)")
@Composable
fun ForgotPasswordScreenErrorPreview() {
    MaterialTheme {
        Scaffold(containerColor = BackgroundGray) { padding ->
            ForgotPasswordScreenContent(
                email = "usuario@gmail.com",
                emailError = "Debe usar el correo institucional",
                onEmailChange = {},
                onBackClick = {},
                onSendClick = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}
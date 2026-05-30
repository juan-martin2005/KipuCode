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
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.ui.component.button.FilledButton
import com.kipucode.ui.component.text_field.KipuForm
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel? = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val authMsgErrorCredentialInvalid = stringResource(id = R.string.auth_repository_credential_invalid)

    val resetStateFlow = authViewModel?.resetPasswordState
    val resetState = resetStateFlow?.collectAsStateWithLifecycle()?.value

    val context = LocalContext.current

    LaunchedEffect(resetState) {
        when (resetState) {
            is Response.Loading -> {}
            is Response.Success -> {
                Toast.makeText(context, "Correo de recuperación enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                authViewModel.resetForgotPasswordState()
                onBack()
            }
            is Response.Error -> {
                emailError = resetState.message ?: "Ocurrió un error inesperado"
                Log.d("FIREBASE_ERROR", resetState.message ?: "Unknown Error")
            }
            null -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
            .padding(12.dp)
    ) {
        // Botón Atrás
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
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
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF081c40),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.subheadline_forgot_password),
                fontSize = 16.sp,
                fontFamily = Nunito,
                color = Color(0xFF686b75),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            KipuForm(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                placeholder = "n00123456@upn.pe",
                iconRes = R.drawable.ic_mail,
                keyboardType = KeyboardType.Email,
                isError = emailError != null,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilledButton(
                textButton = stringResource(R.string.send_email),
                onClickFilledButton = {
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
                        authViewModel?.resetPassword(emailTrimmed)
                    }
                }
            )
        }
    }
}

// PREVIEWS
@Preview(showBackground = true, name = "Recuperar Contraseña")
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen(
            onBack = {},
            authViewModel = null
        )
    }
}
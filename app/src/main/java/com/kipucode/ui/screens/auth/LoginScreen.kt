package com.kipucode.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.kipucode.R
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.ui.component.button.FilledButton
import com.kipucode.ui.component.text_field.ClickableLink
import com.kipucode.ui.component.text_field.KipuForm
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,

    authViewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Estados para atrapar los errores del ViewModel
    var authEmailError by remember { mutableStateOf<String?>(null) }
    var authPasswordError by remember { mutableStateOf<String?>(null) }

    val authMsgErrorEmailNotVerified = stringResource(id = R.string.auth_repository_email_not_verified)
    val authMsgErrorCredentialInvalid = stringResource(id = R.string.auth_repository_credential_invalid)

    LaunchedEffect(loginState) {
        when (loginState) {
            is Response.Loading -> {
            }
            is Response.Success -> {
                Toast.makeText(context, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is Response.Error -> {
                val errorMessage = (loginState as Response.Error).message ?: "Internal Error"
                val errorType = (loginState as Response.Error).error

                when (errorType) {
                    ErrorType.EMAIL_NOT_VERIFIED -> authEmailError = authMsgErrorEmailNotVerified
                    ErrorType.CREDENTIAL_INVALID -> authPasswordError = authMsgErrorCredentialInvalid
                    else -> Log.d("FIREBASE_ERROR", errorMessage)
                }
            }
            null -> {}
        }
    }

    LoginContent(
        onNavigateToRegister = onNavigateToRegister,
        onBack = onBack,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
        onLoginClick = { email, password ->
            authEmailError = null
            authPasswordError = null
            authViewModel.resetState()
            authViewModel.login(email, password)
        },
        externalEmailError = authEmailError,
        externalPasswordError = authPasswordError
    )
}

@Composable
fun LoginContent(
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginClick: (String, String) -> Unit,

    externalEmailError: String? = null,
    externalPasswordError: String? = null
) {
    // ESTADOS PARA LOS CAMPOS
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ESTADOS PARA LAS CONTRASEÑAS
    var passwordVisible by remember { mutableStateOf(false) }

    // ESTADOS DE ERROR (VALIDACIONES)
    var localEmailError by remember { mutableStateOf<String?>(null) }
    var localPasswordError by remember { mutableStateOf<String?>(null) }

    val emailError = localEmailError ?: externalEmailError
    val passwordError = localPasswordError ?: externalPasswordError

    val msgErrorEmailRequired = stringResource(id = R.string.error_email_required)
    val msgErrorEmailDomain = stringResource(id = R.string.error_email_domain)
    val msgErrorPassRequired = stringResource(id = R.string.error_password_required)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Start)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.cd_go_back),
                tint = Color(0xFF081c40),
                modifier = Modifier
                    .size(32.dp)
                    .fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.img_kipucode_logo),
                contentDescription = stringResource(id = R.string.cd_logo),
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.login_title),
                fontSize = 32.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF081c40),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.login_desc),
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF686b75),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CAMPO: EMAIL ---
            KipuForm(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = {
                    email = it
                    localEmailError = null
                },
                placeholder = "n00123456@upn.pe",
                iconRes = R.drawable.ic_mail,
                keyboardType = KeyboardType.Email,
                isError = emailError != null,
                errorMessage = emailError
            )

            // --- CAMPO: PASSWORD ---
            KipuForm(
                label = stringResource(R.string.password),
                value = password,
                onValueChange = {
                    password = it
                    localPasswordError = null
                },
                placeholder = if (passwordVisible) stringResource(R.string.password).lowercase() else "••••••••",
                iconRes = if (passwordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
                isPasswordField = true,
                isPasswordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible },
                keyboardType = KeyboardType.Password,
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.text_forgot_password),
                    color = Color(0xFF0293a8),
                    fontSize = 14.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Transparent)
                        .clickable {
                            onNavigateToForgotPassword()
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilledButton(
                stringResource(id = R.string.logIn),
                {
                    var hasError = false
                    val emailTrimmed = email.trim().lowercase()

                    localEmailError = null
                    localPasswordError = null

                    if (emailTrimmed.isBlank()) {
                        localEmailError = msgErrorEmailRequired
                        hasError = true
                    } else if (!emailTrimmed.endsWith("@upn.pe")) {
                        localEmailError = msgErrorEmailDomain
                        hasError = true
                    }

                    if (password.isBlank()) {
                        localPasswordError = msgErrorPassRequired
                        hasError = true
                    }

                    if (!hasError) {
                        onLoginClick(emailTrimmed, password)
                    }
                }
            )

            ClickableLink(
                stringResource(R.string.login_clickable_start),
                stringResource(R.string.login_clickable_end),
                { onNavigateToRegister() },
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(3f))
        }
    }
}

//// PREVIEWS
//@Preview(showBackground = true, name = "Login")
//@Composable
//fun LoginScreenPreview() {
//    MaterialTheme {
//        LoginContent(
//            onNavigateToRegister = {},
//            onBack = {},
//            onNavigateToForgotPassword = {},
//            onLoginClick = { _, _ -> },
////            externalEmailError = "Email is required",
////            externalPasswordError = "Password is required"
//        )
//    }
//}
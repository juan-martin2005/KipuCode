package com.kipucode.ui.screens.auth

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.kipucode.R
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.text_field.ClickableLink
import com.kipucode.ui.components.text_field.KipuForm
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.LoginFormErrors

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,

    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val loginState by authViewModel.authState.collectAsStateWithLifecycle()
    val formErrors by authViewModel.loginFormErrorsState.collectAsStateWithLifecycle()

    var serverEmailVerified by remember { mutableStateOf<String?>(null) }
    var serverCredentialsError by remember { mutableStateOf<String?>(null) }

    val authMsgErrorEmailNotVerified = stringResource(id = R.string.auth_repository_email_not_verified)
    val authMsgErrorCredentialInvalid = stringResource(id = R.string.auth_repository_credential_invalid)
    val authMsgErrorNetworkError = stringResource(id = R.string.auth_repository_network_error)

    LaunchedEffect(loginState) {
        when (loginState) {
            is Response.Loading -> {
            }
            is Response.Success -> {
                Toast.makeText(context, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
                onLoginSuccess()
            }
            is Response.Error -> {
                val errorType = (loginState as Response.Error).error
                when (errorType) {
                    ServerErrorType.EMAIL_NOT_VERIFIED -> {
                        serverEmailVerified = authMsgErrorEmailNotVerified
                    }
                    ServerErrorType.CREDENTIAL_INVALID -> {
                        serverCredentialsError = authMsgErrorCredentialInvalid
                    }
                    ServerErrorType.NETWORK_ERROR -> {
                        Toast.makeText(context, authMsgErrorNetworkError, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val msg = (loginState as Response.Error).message ?: "Error al iniciar sesión"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            null -> {}
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
    ) { paddingValues ->
        LoginContent(
            modifier = Modifier.padding(paddingValues),
            onNavigateToRegister = onNavigateToRegister,
            onBack = onBack,
            onNavigateToForgotPassword = onNavigateToForgotPassword,

            formErrors = formErrors,
            serverCredentialsError = serverCredentialsError,
            serverEmailVerified = serverEmailVerified,

            onClearCredentialsError = { serverCredentialsError = null },
            onClearEmailVerified = { serverEmailVerified = null },

            onClearEmailError = { authViewModel.clearEmailError() },
            onClearPasswordError = { authViewModel.clearPasswordError() },

            onValidate = { email, pass ->
                authViewModel.validateLoginForm(email, pass)
            },
            onLoginClick = { email, password ->
                authViewModel.resetState()
                authViewModel.login(email, password)
            },

            isLoading = loginState is Response.Loading,

        )
    }
}

@Composable
fun LoginContent(
    modifier: Modifier,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,

    formErrors: LoginFormErrors = LoginFormErrors(),
    serverCredentialsError: String? = null,
    serverEmailVerified: String? = null,

    onClearCredentialsError: () -> Unit = {},
    onClearEmailVerified: () -> Unit = {},

    onClearEmailError: () -> Unit = {},
    onClearPasswordError: () -> Unit = {},

    onValidate: (email: String, pass: String) -> Boolean,
    onLoginClick: (String, String) -> Unit,
    isLoading: Boolean = false,
) {
    // ESTADOS PARA LOS CAMPOS
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ESTADOS PARA LAS CONTRASEÑAS
    var passwordVisible by remember { mutableStateOf(false) }

    val localEmailErrorText = when (formErrors.emailError) {
        ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_email_required)
        ValidationErrorType.INVALID_EMAIL_DOMAIN -> stringResource(R.string.error_email_domain)
        else -> null
    }
    val emailErrorText = localEmailErrorText ?: serverEmailVerified

    val localPasswordErrorText = when (formErrors.passwordError) {
        ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_password_required)
        ValidationErrorType.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
        else -> null
    }

    val passwordErrorText = localPasswordErrorText ?: serverCredentialsError

    Column(
        modifier = modifier
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
                    onClearEmailError()
                    onClearEmailVerified()
                    onClearCredentialsError()
                },
                placeholder = "n00123456@upn.pe",
                iconRes = R.drawable.ic_mail,
                keyboardType = KeyboardType.Email,
                isError = emailErrorText != null,
                errorMessage = emailErrorText
            )

            // --- CAMPO: PASSWORD ---
            KipuForm(
                label = stringResource(R.string.password),
                value = password,
                onValueChange = {
                    password = it
                    onClearPasswordError()
                },
                placeholder = if (passwordVisible) stringResource(R.string.password).lowercase() else "••••••••",
                iconRes = if (passwordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
                isPasswordField = true,
                isPasswordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible },
                keyboardType = KeyboardType.Password,
                isError = passwordErrorText != null,
                errorMessage = passwordErrorText
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
                textButton = stringResource(id = R.string.logIn),
                onClickFilledButton = {
                    val isValid = onValidate(email, password)
                    if (isValid) {
                        onLoginClick(email, password)
                    }
                },
                isLoading = isLoading
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

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    Scaffold(
        containerColor = BackgroundGray
    ) { paddingValues ->
        LoginContent(
            onNavigateToRegister = {},
            onBack = {},
            onNavigateToForgotPassword = {},
            onValidate = { _, _ -> true },
            onLoginClick = { _, _ -> },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
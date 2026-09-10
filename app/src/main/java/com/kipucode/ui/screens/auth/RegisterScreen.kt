package com.kipucode.ui.screens.auth

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.kipucode.R
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.text_field.ClickableLink
import com.kipucode.ui.components.text_field.KipuForm
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.RegisterFormErrors

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val registerState by authViewModel.authState.collectAsStateWithLifecycle()
    val formErrors by authViewModel.registerFormErrorsState.collectAsStateWithLifecycle()

    var serverEmailError by remember { mutableStateOf<String?>(null) }

    val authMsgErrorEmailAlreadyExist = stringResource(id = R.string.auth_repository_email_already_exist)
    val authMsgErrorNetworkError = stringResource(id = R.string.auth_repository_network_error)


    LaunchedEffect(registerState) {
        when (registerState) {
            is Response.Loading -> {
            }
            is Response.Success -> {
                Toast.makeText(
                    context,
                    "¡Estudiante registrado!\nRevisa tu correo para ser verificado",
                    Toast.LENGTH_LONG
                ).show()
                authViewModel.resetState()
                onRegisterSuccess()
            }
            is Response.Error -> {
                val errorType = (registerState as Response.Error).error
                when (errorType) {
                    ServerErrorType.EMAIL_ALREADY_EXIST -> {
                        serverEmailError = authMsgErrorEmailAlreadyExist
                    }
                    ServerErrorType.NETWORK_ERROR -> {
                        Toast.makeText(context, authMsgErrorNetworkError, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val msg = (registerState as Response.Error).message ?: "Error al registrarse"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            null -> {}
        }
    }

    Scaffold(containerColor = BackgroundGray) { paddingValues ->
        RegisterContent(
            modifier = Modifier.padding(paddingValues),
            onNavigateToLogin = onNavigateToLogin,
            onBack = onBack,

            formErrors = formErrors,
            serverEmailError = serverEmailError,
            onClearServerEmailError = { serverEmailError = null },

            onClearNameError = { authViewModel.clearNameError() },
            onClearEmailError = { authViewModel.clearEmailError() },
            onClearPasswordError = { authViewModel.clearPasswordError() },
            onClearConfirmPasswordError = { authViewModel.clearConfirmPasswordError() },

            onValidate = { name, email, pass, confirmPass ->
                authViewModel.validateRegisterForm(name, email, pass, confirmPass)
            },
            onRegisterClick = { name, email, password ->
                val userDomain = UserDomain(name = name.trim(), email = email.trim().lowercase())
                authViewModel.register(userDomain, password)
            }
        )
    }
}

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,

    formErrors: RegisterFormErrors = RegisterFormErrors(),
    serverEmailError: String? = null,
    onClearServerEmailError: () -> Unit = {},

    onClearNameError: () -> Unit = {},
    onClearEmailError: () -> Unit = {},
    onClearPasswordError: () -> Unit = {},
    onClearConfirmPasswordError: () -> Unit = {},

    onValidate: (name: String, email: String, pass: String, confirmPass: String) -> Boolean,
    onRegisterClick: (String, String, String) -> Unit,
) {
    // ESTADOS PARA LOS CAMPOS
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // ESTADOS PARA LAS CONTRASEÑAS
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val nameErrorText = when (formErrors.nameError) {
        ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_name_required)
        else -> null
    }

    val localEmailErrorText = when (formErrors.emailError) {
        ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_email_required)
        ValidationErrorType.INVALID_EMAIL_DOMAIN -> stringResource(R.string.error_email_domain)
        else -> null
    }
    val emailErrorText = localEmailErrorText ?: serverEmailError

    val passwordErrorText = when (formErrors.passwordError) {
        ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_password_required)
        ValidationErrorType.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
        else -> null
    }

    val confirmPasswordErrorText = when (formErrors.confirmPasswordError) {
        ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_confirm_password_required)
        ValidationErrorType.PASSWORDS_DONT_MATCH -> stringResource(R.string.error_password_mismatch)
        else -> null
    }

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

            Text(
                text = stringResource(R.string.register_title),
                fontSize = 32.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF081c40),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            )

            Text(
                text = stringResource(R.string.register_desc),
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF686b75),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CAMPO: NAME ---
            KipuForm(
                label = stringResource(R.string.full_name),
                value = name,
                onValueChange = {
                    name = it
                    onClearNameError()
                },
                placeholder = stringResource(R.string.ph_full_name),
                iconRes = R.drawable.ic_user,
                isError = nameErrorText != null,
                errorMessage = nameErrorText
            )

            // --- CAMPO: EMAIL ---
            KipuForm(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = {
                    email = it
                    onClearEmailError()
                    onClearServerEmailError()
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

            // --- CAMPO: CONFIRM PASSWORD ---
            KipuForm(
                label = stringResource(R.string.confirm_password),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    onClearConfirmPasswordError()
                },
                placeholder = if (confirmPasswordVisible) stringResource(R.string.confirm_password).lowercase() else "••••••••",
                iconRes = if (confirmPasswordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
                isPasswordField = true,
                isPasswordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                keyboardType = KeyboardType.Password,
                isError = confirmPasswordErrorText != null,
                errorMessage = confirmPasswordErrorText
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledButton(
                textButton = stringResource(id = R.string.register_next),
                onClickFilledButton = {
                    val isValid = onValidate(name, email, password, confirmPassword)
                    if (isValid) {
                        onRegisterClick(name, email, password)
                    }
                },
                isLoading = false
            )

            ClickableLink(
                stringResource(R.string.register_clickable_start),
                stringResource(R.string.register_clickable_end),
                { onNavigateToLogin() },
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// PREVIEWS
@Preview(showBackground = true, name = "Register Screen")
@Composable
fun RegisterScreenPreview() {
    Scaffold(
        containerColor = BackgroundGray
    ) { paddingValues ->
        RegisterContent(
            onNavigateToLogin = {},
            onBack = {},
            onValidate = { _, _, _, _ -> true },
            onRegisterClick = { _, _, _ -> },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
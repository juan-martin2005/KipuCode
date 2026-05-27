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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kipucode.R
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.ui.component.button.FilledButton
import com.kipucode.ui.component.text_field.ClickableLink
import com.kipucode.ui.component.text_field.KipuForm
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel (factory = AuthViewModel.Factory)
) {
    // ESTADOS PARA LOS CAMPOS
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // ESTADOS PARA LAS CONTRASEÑAS
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // ESTADOS DE ERROR (VALIDACIONES)
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(loginState) {
        when (loginState) {
            is Response.Loading -> {
            }
            is Response.Success -> {
                onRegisterSuccess()
                Toast.makeText(context, "¡Estudiante registrado!\n revisa tu correo para ser verificado", Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }
            is Response.Error -> {
                val errorMessage = (loginState as Response.Error).message ?: "Internal Error"
                val pos = (loginState as Response.Error).error
                when(pos){
                    ErrorType.NAME_EMPTY -> nameError = errorMessage
                    ErrorType.EMAIL_EMPTY,
                    ErrorType.EMAIL_DOMAIN_NOT_VALID,
                    ErrorType.EMAIL_ALREADY_EXIST -> emailError = errorMessage
                    ErrorType.PASSWORD_EMPTY -> passwordError = errorMessage
                    ErrorType.CREDENTIAL_INVALID -> passwordError = errorMessage
                }
                authViewModel.resetState()
            }
            null -> {
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
            .padding(vertical = 16.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Start)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
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
                    nameError = null
                },                placeholder = stringResource(R.string.ph_full_name),
                iconRes = R.drawable.ic_user,
                isError = nameError != null,
                errorMessage = nameError
            )

            // --- CAMPO: EMAIL ---
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

            // --- CAMPO: PASSWORD ---
            KipuForm(
                label = stringResource(R.string.password),
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
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

            // --- CAMPO: CONFIRM PASSWORD ---
            KipuForm(
                label = stringResource(R.string.confirm_password),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                placeholder = if (confirmPasswordVisible) stringResource(R.string.confirm_password).lowercase() else "••••••••",
                iconRes = if (confirmPasswordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
                isPasswordField = true,
                isPasswordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                keyboardType = KeyboardType.Password,
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledButton(
                "Sign Up",
                {
                    val emailTrimmed = email.trim()
                    val user = User( "",name, emailTrimmed )
                    authViewModel.register(user, password)
                }
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

//@Preview(showBackground = true, name = "Pantalla de Register")
//@Composable
//fun RegisterPreview() {
//    RegisterScreen(
//        onRegisterSuccess = {name, mail, password, confirmPassword ->},
//        onNavigateToLogin = {},
//        onBack = {}
//    )
//}
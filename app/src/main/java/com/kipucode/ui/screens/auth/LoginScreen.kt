package com.kipucode.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kipucode.R
import com.kipucode.domain.model.Response
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    // ESTADOS PARA LOS CAMPOS
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()

    // ESTADOS PARA LAS CONTRASEÑAS
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(loginState) {
        when (loginState) {
            is Response.Loading -> {
            }
            is Response.Success -> {
                // A) Mostramos un mensaje de éxito
                Toast.makeText(context, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show()

                // B) Ejecutamos el callback que activa la navegación en AppNavigation
                onLoginSuccess()

                // C) MUY IMPORTANTE: Limpiamos el estado en el ViewModel.
                // Si no lo haces, al regresar a esta pantalla por accidente,
                // se volverá a disparar el éxito automáticamente.
                authViewModel.resetState()
            }
            is Response.Error -> {
                // Mostramos el mensaje de error que viene desde Firebase/Backend
                val errorMessage = (loginState as Response.Error).message ?: "Error desconocido"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

                // Limpiamos el estado para poder intentar loguearnos otra vez
                authViewModel.resetState()
            }
            null -> {
                // Estado inicial o resetseado, no hacemos nada
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
                contentDescription = "Go back",
                tint = Color(0xFF081c40),
                modifier = Modifier.size(32.dp)
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
                contentDescription = "Logo KipuCode",
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Welcome back!",
                fontSize = 32.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF081c40),
                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Continue your programming journey. Ready to learn something new?",
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF686b75),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CAMPO: EMAIL ---
            Text(
                text = "Email",
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF081c40),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "n00123456@upn.pe", color = Color.Gray) },
                leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_mail), contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLeadingIconColor = Color.Gray,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color(0xFF0293a8),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO: PASSWORD ---
            Text(
                text = "Password",
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF081c40),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {Text(
                    text = if (passwordVisible) "password" else "••••••••",
                    color = Color.Gray
                )},
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = null)
                },

                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                trailingIcon = {
                    val imageId = if (passwordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = imageId),
                            contentDescription = "Toggle password visibility",
                            tint = Color.Gray
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLeadingIconColor = Color.Gray,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color(0xFF0293a8),
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.login(email,password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0293a8))
            ) {
                Text(
                    text = "Log In",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            val annotatedText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 16.sp)) {
                    append("Don't have an account yet?, ")
                }
                val clickableLink = LinkAnnotation.Clickable(tag = "Register") {
                    onNavigateToRegister()
                }
                withLink(clickableLink) {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF0293a8),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            textDecoration = TextDecoration.None
                        )
                    ) {
                        append("Register")
                    }
                }
            }

            Text(
                text = annotatedText,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.weight(2f))
        }

    }


}

//@Preview(showBackground = true, name = "Pantalla de Login")
//@Composable
//fun LoginPreview() {
//    LoginScreen(
//        onLoginSuccess = {},
//        onNavigateToRegister = {},
//        onBack = {}
//    )
//}
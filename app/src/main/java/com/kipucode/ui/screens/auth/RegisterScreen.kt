package com.kipucode.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Nunito

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    // ESTADOS PARA LOS CAMPOS
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // ESTADOS PARA LAS CONTRASEÑAS
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }



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

            Text(
                text = "Create account",
                fontSize = 32.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF081c40),
                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)
            )

            Text(
                text = "Join KipuCode and start your journey into the world of programming.",
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF686b75),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CAMPO: NAME ---
            Text(
                text = "Full Name",
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF081c40),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(text = "Your name", color = Color.Gray) },
                leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_user), contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO: CONFIRM PASSWORD ---
            Text(
                text = "Confirm Password",
                fontSize = 16.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF081c40),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {Text(
                    text = if (confirmPasswordVisible) "confirmPassword" else "••••••••",
                    color = Color.Gray
                )},            leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = null)
                },

                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                trailingIcon = {
                    val imageId = if (confirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
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
                    onRegisterSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0293a8))
            ) {
                Text(
                    text = "Sign Up",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            val annotatedText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 16.sp)) {
                    append("Do you already have an account?, ")
                }
                val clickableLink = LinkAnnotation.Clickable(tag = "LogIn") {
                    onNavigateToLogin()
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
                        append(" Log in")
                    }
                }
            }

            Text(
                text = annotatedText,
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
//        onRegisterSuccess = {},
//        onNavigateToLogin = {},
//        onBack = {}
//    )
//}
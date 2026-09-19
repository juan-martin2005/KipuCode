package com.kipucode.ui.components.text_field

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Nunito

@Composable
fun KipuForm(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    isPasswordField: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            color = if (isError) Color(0xFFE35D5B) else Color(0xFF081c40),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(painter = painterResource(id = iconRes), contentDescription = null)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,

            visualTransformation = if (isPasswordField && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPasswordField) {
                {
                    val eyeIconRes = if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                    IconButton(onClick = onVisibilityChange) {
                        Icon(
                            painter = painterResource(id = eyeIconRes),
                            contentDescription = null,
                            tint = if (isError) Color(0xFFE35D5B) else Color.Gray)
                    }
                }
            } else null,

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF081c40),
                unfocusedTextColor = Color(0xFF081c40),
                errorTextColor = Color(0xFF081c40),

                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLeadingIconColor = Color.Gray,
                unfocusedLeadingIconColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color(0xFF0293a8),

                errorBorderColor = Color(0xFFE35D5B),
                errorLeadingIconColor = Color(0xFFE35D5B),
                errorTrailingIconColor = Color(0xFFE35D5B)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color(0xFFE35D5B),
                fontSize = 12.sp,
                fontFamily = Nunito,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 1.dp),
                textAlign = TextAlign.Start
            )
        }
        if (isError) Spacer(modifier = Modifier.height(4.dp)) else Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, name = "Vista del KipuForm (Texto Normal)")
@Composable
fun KipuFormNormalPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        KipuForm(
            label = "Correo Electrónico",
            value = "",
            onValueChange = {},
            placeholder = "ejemplo@correo.com",
            iconRes = R.drawable.ic_mail
        )
    }
}

@Preview(showBackground = true, name = "Vista del KipuForm (Contraseña)")
@Composable
fun KipuFormPasswordPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        KipuForm(
            label = "Contraseña",
            value = "miSuperClave123",
            onValueChange = {},
            placeholder = "Ingresa tu contraseña",
            iconRes = R.drawable.ic_lock,
            isPasswordField = true,
            isPasswordVisible = false
        )
    }
}

@Preview(showBackground = true, name = "Vista del KipuForm (Error)")
@Composable
fun KipuFormErrorPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        KipuForm(
            label = "Nombre de Usuario",
            value = "abc",
            onValueChange = {},
            placeholder = "Ingresa tu usuario",
            iconRes = R.drawable.ic_user,
            isError = true,
            errorMessage = "El nombre de usuario debe tener al menos 6 caracteres."
        )
    }
}
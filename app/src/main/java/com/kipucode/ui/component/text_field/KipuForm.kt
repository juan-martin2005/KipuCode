package com.kipucode.ui.component.text_field

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
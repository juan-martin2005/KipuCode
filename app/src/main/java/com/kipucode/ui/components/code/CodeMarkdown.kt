package com.kipucode.ui.components.code

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.BorderLightGray
import com.kipucode.ui.theme.JetBrains
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Contenedor nativo de bloque de código y diagramas tipo terminal,
 * con barra superior, botón de copiar con animación y tipografía JetBrains Mono.
 */
@Composable
fun CodeMarkdown(
    code: String,
    language: String = "text",
    modifier: Modifier = Modifier,
    isDiagram: Boolean = false
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000.milliseconds)
            isCopied = false
        }
    }

    val headerTitle = when {
        isDiagram -> "DIAGRAMA"
        language.isNotBlank() && language.lowercase() != "text" -> language.uppercase()
        else -> "TERMINAL"
    }

    val iconRes = if (isDiagram || headerTitle == "TERMINAL") R.drawable.ic_terminal_rounded else R.drawable.ic_code

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MoonFrost.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = BorderLightGray,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // --- Barra superior del bloque de código ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MoonFrost.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = KipuTeal,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = headerTitle,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = KipuTeal
                )
            }

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                    isCopied = true
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isCopied) R.drawable.ic_check else R.drawable.ic_edit
                    ),
                    contentDescription = "Copiar código",
                    tint = if (isCopied) KipuTeal else KipuDarkBlue.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // --- Contenido de código con scroll horizontal y JetBrains Mono ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            Text(
                text = code,
                fontFamily = JetBrains,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = KipuDarkBlue
            )
        }
    }
}

@Preview(showBackground = true, name = "Code Terminal Preview")
@Composable
fun CodeMarkdownPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CodeMarkdown(
            code = "dotnet new console -n HolaMundo\ncd HolaMundo\ndotnet run",
            language = "terminal"
        )
        CodeMarkdown(
            code = "using System;\n\nConsole.WriteLine(\"¡Hola desde C#!\");",
            language = "c#"
        )
    }
}

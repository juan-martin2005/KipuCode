package com.kipucode.ui.screens.editor.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.kipucode.ui.theme.KipuCodeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.excecution.CodeDiagnosticDomain
import com.kipucode.domain.model.excecution.CodeExecutionResultDomain
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

enum class ConsoleTab(val title: String) {
    INPUT("Entrada"),
    OUTPUT("Salida"),
    PROBLEMS("Problemas")
}

@Composable
fun ExecutionPanel(
    isRunning: Boolean,
    result: CodeExecutionResultDomain?,
    diagnostics: List<CodeDiagnosticDomain>,
    errorMessage: String?,
    stdin: String,
    onStdinChanged: (String) -> Unit,
    onExecuteClick: () -> Unit,
    onJumpToDiagnostic: (line: Int, column: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ConsoleTab.OUTPUT) }
    var isExpanded by remember { mutableStateOf(true) }

    // Sí hay diagnósticos nuevos tras la ejecución, cambiar automáticamente a la pestaña Problemas
    LaunchedEffect(diagnostics) {
        if (diagnostics.isNotEmpty()) {
            selectedTab = ConsoleTab.PROBLEMS
        } else if (result != null) {
            selectedTab = ConsoleTab.OUTPUT
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // --- MANIJA SUPERIOR (Drag Handle) Y BOTÓN EXPANDIR/COLAPSAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCBD2DF))
                )
            }

            // --- BOTÓN PRINCIPAL DE EJECUCIÓN (▶ Ejecutar) ---
            Button(
                onClick = onExecuteClick,
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KipuTeal,
                    disabledContainerColor = KipuTeal.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                if (isRunning) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ejecutando...",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "▶  Ejecutar",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    // --- FILA DE PESTAÑAS (Entrada | Salida | Problemas) ---
                    val tabs = listOf(ConsoleTab.INPUT, ConsoleTab.OUTPUT, ConsoleTab.PROBLEMS)
                    val selectedTabIndex = tabs.indexOf(selectedTab)

                    androidx.compose.material3.SecondaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = KipuDarkBlue,
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                                color = if (selectedTab == ConsoleTab.PROBLEMS && diagnostics.isNotEmpty()) Color(0xFFE53935) else KipuTeal,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = tab == selectedTab
                            Tab(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tab.title,
                                            fontFamily = Nunito,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = if (isSelected) KipuDarkBlue else Gray
                                        )

                                        // Badge de problemas
                                        if (tab == ConsoleTab.PROBLEMS && diagnostics.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE53935)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = diagnostics.size.toString(),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- CONTENIDO SEGÚN LA PESTAÑA ACTIVA ---
                    when (selectedTab) {
                        ConsoleTab.INPUT -> {
                            // Pestaña Entrada (stdin)
                            OutlinedTextField(
                                value = stdin,
                                onValueChange = onStdinChanged,
                                placeholder = {
                                    Text(
                                        text = "Entrada estándar para el programa (stdin)...",
                                        fontFamily = Nunito,
                                        fontSize = 13.sp,
                                        color = Gray
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = KipuTeal,
                                    unfocusedBorderColor = Color(0xFFD6DBE4)
                                )
                            )
                        }

                        ConsoleTab.OUTPUT -> {
                            // Pestaña Salida (stdout)
                            val stdout = result?.stdout
                            val executionTime = result?.executionTimeSeconds
                            val memoryKb = result?.memoryKb
                            val isSuccess = result?.status == "SUCCESS"

                            if (result != null) {
                                // Insignia de estado + Copiar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSuccess) Color(0xFFE8F8F5) else Color(0xFFFFEBEE))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = if (isSuccess) R.drawable.ic_check else R.drawable.ic_warning),
                                            contentDescription = null,
                                            tint = if (isSuccess) Color(0xFF00A896) else Color(0xFFE53935),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isSuccess) "Ejecución completada" else "Ejecución finalizada con errores",
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSuccess) Color(0xFF00796B) else Color(0xFFC62828)
                                        )
                                    }

                                    // Botón Copiar Salida
                                    if (!stdout.isNullOrBlank()) {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Salida KipuCode", stdout))
                                                Toast.makeText(context, "Salida copiada al portapapeles", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_description),
                                                contentDescription = "Copiar salida",
                                                tint = KipuDarkBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Terminal / Consola de texto
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 95.dp, max = 160.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF7F9FC))
                                        .border(1.dp, Color(0xFFE2E7EE), RoundedCornerShape(10.dp))
                                        .verticalScroll(rememberScrollState())
                                        .horizontalScroll(rememberScrollState())
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = stdout?.ifBlank { "(Sin salida por consola)" }
                                            ?: result.stderr
                                            ?: result.compileOutput
                                            ?: errorMessage
                                            ?: "(Sin salida)",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = KipuDarkBlue
                                    )
                                }

                                // Métricas de rendimiento al pie
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (executionTime != null) "⏱ ${String.format("%.2f", executionTime)} s" else "⏱ 0.00 s",
                                        fontFamily = Nunito,
                                        fontSize = 12.sp,
                                        color = Gray
                                    )
                                    Text(
                                        text = if (memoryKb != null) "💾 ${String.format("%.1f", memoryKb / 1024.0)} MB" else "💾 0.0 MB",
                                        fontFamily = Nunito,
                                        fontSize = 12.sp,
                                        color = Gray
                                    )
                                }
                            } else {
                                // Estado inicial
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF7F9FC))
                                        .border(1.dp, Color(0xFFE2E7EE), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Presiona Ejecutar para ver los resultados aquí",
                                        fontFamily = Nunito,
                                        fontSize = 13.sp,
                                        color = Gray
                                    )
                                }
                            }
                        }

                        ConsoleTab.PROBLEMS -> {
                            // Pestaña Problemas
                            if (diagnostics.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Banner de Error de Compilación
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_warning),
                                            contentDescription = null,
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Error de compilación",
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFC62828)
                                        )
                                    }

                                    // Lista de errores
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        items(diagnostics) { diag ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFF7F9FC))
                                                    .border(1.dp, Color(0xFFE2E7EE), RoundedCornerShape(8.dp))
                                                    .clickable { onJumpToDiagnostic(diag.line, diag.column) }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFE53935)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "!",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = diag.code ?: "Error",
                                                        fontFamily = Nunito,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = Color(0xFFC62828)
                                                    )
                                                    Text(
                                                        text = diag.message,
                                                        fontFamily = Nunito,
                                                        fontSize = 12.sp,
                                                        color = KipuDarkBlue
                                                    )
                                                }

                                                Text(
                                                    text = "L${diag.line}",
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 12.sp,
                                                    color = Gray
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_arrow_right),
                                                    contentDescription = null,
                                                    tint = Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Detectado al ejecutar",
                                        fontFamily = Nunito,
                                        fontSize = 11.sp,
                                        color = Gray
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No se encontraron problemas ni errores de sintaxis",
                                        fontFamily = Nunito,
                                        fontSize = 13.sp,
                                        color = Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "01 - Estado Inicial", showBackground = true)
@Composable
private fun ExecutionPanelInitialPreview() {
    KipuCodeTheme {
        ExecutionPanel(
            isRunning = false,
            result = null,
            diagnostics = emptyList(),
            errorMessage = null,
            stdin = "",
            onStdinChanged = {},
            onExecuteClick = {},
            onJumpToDiagnostic = { _, _ -> }
        )
    }
}

@Preview(name = "02 - Ver Resultados (Salida Exitosa)", showBackground = true)
@Composable
private fun ExecutionPanelSuccessPreview() {
    KipuCodeTheme {
        ExecutionPanel(
            isRunning = false,
            result = CodeExecutionResultDomain(
                status = "SUCCESS",
                stdout = "12\n",
                executionTimeSeconds = 0.02,
                memoryKb = 2150
            ),
            diagnostics = emptyList(),
            errorMessage = null,
            stdin = "",
            onStdinChanged = {},
            onExecuteClick = {},
            onJumpToDiagnostic = { _, _ -> }
        )
    }
}

@Preview(name = "03 - Corregir Errores (Problemas)", showBackground = true)
@Composable
private fun ExecutionPanelProblemsPreview() {
    KipuCodeTheme {
        ExecutionPanel(
            isRunning = false,
            result = CodeExecutionResultDomain(
                status = "COMPILATION_ERROR",
                compileOutput = "Main.cs(9,32): error CS1002: ; expected"
            ),
            diagnostics = listOf(
                CodeDiagnosticDomain(
                    line = 9,
                    column = 32,
                    severity = "ERROR",
                    code = "CS1002",
                    message = "Falta un punto y coma."
                )
            ),
            errorMessage = "Error de compilación",
            stdin = "",
            onStdinChanged = {},
            onExecuteClick = {},
            onJumpToDiagnostic = { _, _ -> }
        )
    }
}


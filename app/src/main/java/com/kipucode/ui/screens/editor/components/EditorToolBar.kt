package com.kipucode.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@Composable
fun EditorToolBar(
    fileName: String = "Main.cs",
    currentLanguageLabel: String = "C# (.NET)",
    canUndo: Boolean,
    canRedo: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onResetTemplate: () -> Unit,
    onClearCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // --- FILA 1: Top Bar (Atrás, Título, Opciones) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Volver",
                        tint = KipuDarkBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Editor de Código",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = KipuDarkBlue
                )
            }

            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_explore),
                        contentDescription = "Opciones",
                        tint = KipuDarkBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Restaurar plantilla inicial", fontFamily = Nunito) },
                        onClick = {
                            isMenuExpanded = false
                            onResetTemplate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Limpiar todo el código", fontFamily = Nunito, color = Color.Red) },
                        onClick = {
                            isMenuExpanded = false
                            onClearCode()
                        }
                    )
                }
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFF1F3F7)
        )

        // --- FILA 2: Archivo, Badge Lenguaje C# y Botones Undo/Redo ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_code),
                contentDescription = null,
                tint = KipuDarkBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = fileName,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = KipuDarkBlue
            )

            Spacer(modifier = Modifier.width(60.dp))

            // Badge fijo de lenguaje (C# .NET)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF8FA))
                    .border(1.dp, Color(0xFFD3EEF3), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentLanguageLabel,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = KipuTeal
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Deshacer (Undo)
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bent_arrow_left),
                    contentDescription = "Deshacer",
                    tint = if (canUndo) KipuDarkBlue else Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Rehacer (Redo)
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bent_arrow_right),
                    contentDescription = "Rehacer",
                    tint = if (canRedo) KipuDarkBlue else Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(name = "Barra de Herramientas del Editor", showBackground = true)
@Composable
private fun EditorToolBarPreview() {
    KipuCodeTheme {
        EditorToolBar(
            fileName = "Main.cs",
            currentLanguageLabel = "C# (.NET)",
            canUndo = true,
            canRedo = false,
            onBack = {},
            onUndo = {},
            onRedo = {},
            onResetTemplate = {},
            onClearCode = {}
        )
    }
}

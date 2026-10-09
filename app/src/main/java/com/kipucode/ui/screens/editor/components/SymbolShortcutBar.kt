package com.kipucode.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.KipuDarkBlue

enum class EditorShortcutType {
    TEXT,
    LEFT,
    RIGHT,
    TAB
}

data class EditorShortcut(
    val label: String,
    val type: EditorShortcutType,
    val insertText: String = ""
)

@Composable
fun SymbolShortcutBar(
    onInsertText: (String) -> Unit,
    onMoveCursorLeft: () -> Unit,
    onMoveCursorRight: () -> Unit,
    onInsertTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shortcuts = listOf(
        EditorShortcut("{ }", EditorShortcutType.TEXT, "{}"),
        EditorShortcut("( )", EditorShortcutType.TEXT, "()"),
        EditorShortcut(";", EditorShortcutType.TEXT, ";"),
        EditorShortcut("\"", EditorShortcutType.TEXT, "\"\""),
        EditorShortcut("⇥", EditorShortcutType.TAB, "    "),
        EditorShortcut("←", EditorShortcutType.LEFT),
        EditorShortcut("→", EditorShortcutType.RIGHT)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F3F7))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        shortcuts.forEach { shortcut ->
            Box(
                modifier = Modifier
                    .height(38.dp)
                    .widthIn(min = 44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFD6DBE4), RoundedCornerShape(8.dp))
                    .clickable {
                        when (shortcut.type) {
                            EditorShortcutType.TEXT -> onInsertText(shortcut.insertText)
                            EditorShortcutType.TAB -> onInsertTab()
                            EditorShortcutType.LEFT -> onMoveCursorLeft()
                            EditorShortcutType.RIGHT -> onMoveCursorRight()
                        }
                    }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortcut.label,
                    color = KipuDarkBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Barra de Atajos de Símbolos", showBackground = true)
@Composable
private fun SymbolShortcutBarPreview() {
    com.kipucode.ui.theme.KipuCodeTheme {
        SymbolShortcutBar(
            onInsertText = {},
            onMoveCursorLeft = {},
            onMoveCursorRight = {},
            onInsertTab = {}
        )
    }
}


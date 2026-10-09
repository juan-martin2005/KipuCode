package com.kipucode.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.kipucode.domain.model.excecution.CodeDiagnosticDomain
import com.kipucode.ui.editor.SoraDiagnosticMapper
import com.kipucode.ui.editor.SoraEditorFacttory
import com.kipucode.ui.editor.csharp.CSharpEditorLanguage
import com.kipucode.ui.theme.KipuCodeTheme
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion

@Composable
fun SoraCodeEditor(
    code: String,
    languageKey: String,
    diagnostics: List<CodeDiagnosticDomain>,
    onCodeChange: (String) -> Unit,
    onEditorReady: (CodeEditor) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        // En modo Preview de Android Studio (evita crasheos de AndroidView sin ventana real)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF131C26))
                .padding(16.dp)
        ) {
            Text(
                text = code,
                color = Color(0xFFD4D4D4),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }
        return
    }

    val context = LocalContext.current
    val editor = remember {
        SoraEditorFacttory.createEditor(context).apply {
            setEditorLanguage(CSharpEditorLanguage())
            getComponent(EditorAutoCompletion::class.java).isEnabled = true
            if (code.isNotEmpty()) {
                setText(code)
            }
        }
    }

    LaunchedEffect(editor) {
        onEditorReady(editor)
    }

    // Carga inicial segura del código si llega con retardo sin alterar el cursor durante edición activa
    LaunchedEffect(code) {
        if (editor.text.isEmpty() && code.isNotEmpty()) {
            editor.setText(code)
        }
    }

    // Suscribirse a cambios de texto en tiempo real
    DisposableEffect(editor) {
        val subscription = editor.subscribeEvent(ContentChangeEvent::class.java) { _, _ ->
            val updatedText = editor.text.toString()
            onCodeChange(updatedText)
        }

        onDispose {
            subscription.unsubscribe()
        }
    }

    // Actualizar marcadores de diagnóstico (subrayados de error / advertencia)
    LaunchedEffect(diagnostics) {
        SoraDiagnosticMapper.applyDiagnostics(editor, diagnostics)
    }

    AndroidView(
        factory = { editor },
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(name = "Editor de Código Sora", showBackground = true)
@Composable
private fun SoraCodeEditorPreview() {
    SoraCodeEditor(
        code = "using System;\n\nclass Program {\n    static void Main() {\n        Console.WriteLine(\"Hola KipuCode\");\n    }\n}",
        languageKey = "csharp",
        diagnostics = emptyList(),
        onCodeChange = {},
        onEditorReady = {}
    )
}

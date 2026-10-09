package com.kipucode.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.domain.model.excecution.CodeDiagnosticDomain
import com.kipucode.domain.model.excecution.CodeExecutionResultDomain
import com.kipucode.ui.editor.SoraDiagnosticMapper
import com.kipucode.ui.screens.editor.components.EditorToolBar
import com.kipucode.ui.screens.editor.components.ExecutionPanel
import com.kipucode.ui.screens.editor.components.SoraCodeEditor
import com.kipucode.ui.screens.editor.components.SymbolShortcutBar
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.viewmodel.DEFAULT_CSHARP_TEMPLATE
import com.kipucode.viewmodel.CodeEditorUiState
import com.kipucode.viewmodel.CodeEditorViewModel
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
fun CodeEditorScreen(
    languageKey: String = "csharp",
    onBack: () -> Unit,
    viewModel: CodeEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setInitialCode(DEFAULT_CSHARP_TEMPLATE, "csharp")
    }

    CodeEditorContent(
        uiState = uiState,
        defaultTemplate = DEFAULT_CSHARP_TEMPLATE,
        onBack = onBack,
        onCodeChanged = { viewModel.onCodeChanged(it) },
        onResetTemplate = { viewModel.setInitialCode(DEFAULT_CSHARP_TEMPLATE, "csharp") },
        onExecute = { stdin -> viewModel.executeCode(stdin) }
    )
}

@Composable
fun CodeEditorContent(
    uiState: CodeEditorUiState,
    defaultTemplate: String,
    onBack: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onResetTemplate: () -> Unit,
    onExecute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editorInstance by remember { mutableStateOf<CodeEditor?>(null) }
    var stdinInput by remember { mutableStateOf("") }
    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EditorToolBar(
                fileName = "Main.cs",
                currentLanguageLabel = "C# (.NET)",
                canUndo = canUndo,
                canRedo = canRedo,
                onBack = onBack,
                onUndo = {
                    editorInstance?.undo()
                    canUndo = editorInstance?.canUndo() == true
                    canRedo = editorInstance?.canRedo() == true
                },
                onRedo = {
                    editorInstance?.redo()
                    canUndo = editorInstance?.canUndo() == true
                    canRedo = editorInstance?.canRedo() == true
                },
                onResetTemplate = {
                    onResetTemplate()
                    editorInstance?.setText(defaultTemplate)
                },
                onClearCode = {
                    onCodeChanged("")
                    editorInstance?.setText("")
                }
            )
        },
        containerColor = Color(0xFFDEE5F6),
        modifier = modifier.imePadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- ÁREA CENTRAL: EDITOR DE CÓDIGO SORA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF181A20))
            ) {
                SoraCodeEditor(
                    code = uiState.sourceCode,
                    languageKey = "csharp",
                    diagnostics = uiState.diagnostics,
                    onCodeChange = { newCode ->
                        onCodeChanged(newCode)
                        canUndo = editorInstance?.canUndo() == true
                        canRedo = editorInstance?.canRedo() == true
                    },
                    onEditorReady = { editor ->
                        editorInstance = editor
                        canUndo = editor.canUndo()
                        canRedo = editor.canRedo()
                    }
                )
            }

            // --- BARRA DE ATAJOS DE SÍMBOLOS ({}, (), ;, ", ⇥, ←, →) ---
            SymbolShortcutBar(
                onInsertText = { text ->
                    editorInstance?.let { editor ->
                        try {
                            val cursor = editor.cursor
                            editor.text.insert(cursor.leftLine, cursor.leftColumn, text)
                            if (text == "{}" || text == "()" || text == "\"\"") {
                                editor.setSelection(cursor.leftLine, cursor.leftColumn - 1)
                            }
                        } catch (_: Exception) {}
                    }
                },
                onInsertTab = {
                    editorInstance?.let { editor ->
                        try {
                            val cursor = editor.cursor
                            editor.text.insert(cursor.leftLine, cursor.leftColumn, "    ")
                        } catch (_: Exception) {}
                    }
                },
                onMoveCursorLeft = {
                    editorInstance?.let { editor ->
                        try {
                            val cursor = editor.cursor
                            if (cursor.leftColumn > 0) {
                                editor.setSelection(cursor.leftLine, cursor.leftColumn - 1)
                            } else if (cursor.leftLine > 0) {
                                val prevLine = cursor.leftLine - 1
                                val lastCol = editor.text.getColumnCount(prevLine)
                                editor.setSelection(prevLine, lastCol)
                            }
                        } catch (_: Exception) {}
                    }
                },
                onMoveCursorRight = {
                    editorInstance?.let { editor ->
                        try {
                            val cursor = editor.cursor
                            val lineLength = editor.text.getColumnCount(cursor.leftLine)
                            if (cursor.leftColumn < lineLength) {
                                editor.setSelection(cursor.leftLine, cursor.leftColumn + 1)
                            } else if (cursor.leftLine < editor.text.lineCount - 1) {
                                editor.setSelection(cursor.leftLine + 1, 0)
                            }
                        } catch (_: Exception) {}
                    }
                }
            )

            // --- PANEL INFERIOR: CONSOLA / SALIDA / PROBLEMAS ---
            ExecutionPanel(
                isRunning = uiState.isRunning,
                result = uiState.result,
                diagnostics = uiState.diagnostics,
                errorMessage = uiState.errorMessage,
                stdin = stdinInput,
                onStdinChanged = { stdinInput = it },
                onExecuteClick = { onExecute(stdinInput) },
                onJumpToDiagnostic = { line, column ->
                    editorInstance?.let { editor ->
                        SoraDiagnosticMapper.jumpToLine(editor, line, column)
                    }
                }
            )
        }
    }
}

@Preview(name = "01 Escribir - Estado Inicial", showBackground = true, showSystemUi = true)
@Composable
private fun CodeEditorScreenInitialPreview() {
    KipuCodeTheme {
        CodeEditorContent(
            uiState = CodeEditorUiState(
                sourceCode = DEFAULT_CSHARP_TEMPLATE,
                languageKey = "csharp",
                result = null
            ),
            defaultTemplate = DEFAULT_CSHARP_TEMPLATE,
            onBack = {},
            onCodeChanged = {},
            onResetTemplate = {},
            onExecute = {}
        )
    }
}

@Preview(name = "02 Ver Resultados - Ejecución Exitosa", showBackground = true, showSystemUi = true)
@Composable
private fun CodeEditorScreenSuccessPreview() {
    KipuCodeTheme {
        CodeEditorContent(
            uiState = CodeEditorUiState(
                sourceCode = DEFAULT_CSHARP_TEMPLATE,
                languageKey = "csharp",
                result = CodeExecutionResultDomain(
                    status = "SUCCESS",
                    stdout = "12\n",
                    executionTimeSeconds = 0.028,
                    memoryKb = 5020
                )
            ),
            defaultTemplate = DEFAULT_CSHARP_TEMPLATE,
            onBack = {},
            onCodeChanged = {},
            onResetTemplate = {},
            onExecute = {}
        )
    }
}

@Preview(name = "03 Corregir Errores - Diagnósticos en Vivo", showBackground = true, showSystemUi = true)
@Composable
private fun CodeEditorScreenErrorPreview() {
    KipuCodeTheme {
        CodeEditorContent(
            uiState = CodeEditorUiState(
                sourceCode = DEFAULT_CSHARP_TEMPLATE,
                languageKey = "csharp",
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
                        message = "; expected"
                    )
                ),
                errorMessage = "Error de compilación"
            ),
            defaultTemplate = DEFAULT_CSHARP_TEMPLATE,
            onBack = {},
            onCodeChanged = {},
            onResetTemplate = {},
            onExecute = {}
        )
    }
}
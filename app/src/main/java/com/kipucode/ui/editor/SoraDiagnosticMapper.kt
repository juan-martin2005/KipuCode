package com.kipucode.ui.editor

import com.kipucode.domain.model.excecution.CodeDiagnosticDomain
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer
import io.github.rosemoe.sora.widget.CodeEditor

object SoraDiagnosticMapper {

    fun applyDiagnostics(editor: CodeEditor, diagnostics: List<CodeDiagnosticDomain>) {
        if (diagnostics.isEmpty()) {
            editor.setDiagnostics(null)
            return
        }

        val container = DiagnosticsContainer()
        val text = editor.text

        for (diag in diagnostics) {
            val line = (diag.line - 1).coerceAtLeast(0)
            val col = (diag.column - 1).coerceAtLeast(0)
            
            try {
                if (line < text.lineCount) {
                    val lineStart = text.getCharIndex(line, 0)
                    val lineLength = text.getColumnCount(line)
                    val startOffset = (lineStart + col).coerceAtMost(lineStart + lineLength)
                    val endOffset = (startOffset + 5).coerceAtMost(lineStart + lineLength).coerceAtLeast(startOffset + 1)
                    
                    // DiagnosticRegion constructors:
                    // DiagnosticRegion(startOffset, endOffset, severity, detail)
                    val severity = if (diag.severity.equals("WARNING", ignoreCase = true)) {
                        DiagnosticRegion.SEVERITY_WARNING
                    } else {
                        DiagnosticRegion.SEVERITY_ERROR
                    }
                    val region = DiagnosticRegion(startOffset, endOffset, severity)
                    container.addDiagnostic(region)
                }
            } catch (_: Exception) {}
        }

        editor.setDiagnostics(container)
    }

    fun jumpToLine(editor: CodeEditor, line: Int, column: Int) {
        val targetLine = (line - 1).coerceAtLeast(0)
        val targetCol = (column - 1).coerceAtLeast(0)
        try {
            editor.setSelection(targetLine, targetCol)
            editor.ensurePositionVisible(targetLine, targetCol)
        } catch (_: Exception) {}
    }
}
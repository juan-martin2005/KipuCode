package com.kipucode.ui.editor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.kipucode.R
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula

object SoraEditorFacttory {

    fun createEditor(context: Context): CodeEditor {
        val editor = CodeEditor(context).apply {
            // Esquema de color oscuro base Darcula / VS Code Modern
            colorScheme = SchemeDarcula().apply {
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.WHOLE_BACKGROUND, Color.parseColor("#181A20"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.LINE_NUMBER_BACKGROUND, Color.parseColor("#14161D"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.LINE_NUMBER, Color.parseColor("#60667A"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.LINE_NUMBER_CURRENT, Color.parseColor("#A0AEC0"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.CURRENT_LINE, Color.parseColor("#262933"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.SELECTION_INSERT, Color.parseColor("#00A896"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.SELECTION_HANDLE, Color.parseColor("#00A896"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.SELECTED_TEXT_BACKGROUND, Color.parseColor("#264F78"))

                // Colores de sintaxis de código
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.KEYWORD, Color.parseColor("#569CD6"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.COMMENT, Color.parseColor("#6A9955"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.LITERAL, Color.parseColor("#CE9178"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.IDENTIFIER_NAME, Color.parseColor("#4EC9B0"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.FUNCTION_NAME, Color.parseColor("#DCDCAA"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.OPERATOR, Color.parseColor("#D4D4D4"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.TEXT_NORMAL, Color.parseColor("#D4D4D4"))

                // Ventana emergente de autocompletado
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.COMPLETION_WND_BACKGROUND, Color.parseColor("#1E222B"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.COMPLETION_WND_CORNER, Color.parseColor("#2D3748"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY, Color.parseColor("#F7FAFC"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY, Color.parseColor("#A0AEC0"))
                setColor(io.github.rosemoe.sora.widget.schemes.EditorColorScheme.COMPLETION_WND_ITEM_CURRENT, Color.parseColor("#2B3545"))
            }

            // Tipografía monoespaciada de KipuCode (JetBrains Mono)
            try {
                val monoTypeface = ResourcesCompat.getFont(context, R.font.jetbrainsmono_nf)
                typefaceText = monoTypeface ?: Typeface.MONOSPACE
                typefaceLineNumber = monoTypeface ?: Typeface.MONOSPACE
            } catch (_: Exception) {
                typefaceText = Typeface.MONOSPACE
                typefaceLineNumber = Typeface.MONOSPACE
            }

            // Opciones del editor
            setTextSize(14f)
            isLineNumberEnabled = true
            setPinLineNumber(true)
            isWordwrap = false
            tabWidth = 4
            isCursorAnimationEnabled = true
            isHighlightCurrentLine = true
        }

        return editor
    }
}
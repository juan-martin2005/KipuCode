package com.kipucode.ui.screens.exercise.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kipucode.ui.components.code.CodeMarkdown
import com.kipucode.ui.components.text.FormattedText
import com.kipucode.ui.theme.KipuH4

/**
 * Representa los segmentos consecutivos de la instrucción de un ejercicio:
 * puede contener múltiples bloques de texto intercalados con bloques de código o terminal.
 */
sealed interface InstructionPart {
    data class Text(val text: String) : InstructionPart
    data class Code(val code: String, val language: String) : InstructionPart
}

@Composable
fun ExerciseInstruction(
    instruction: String,
    modifier: Modifier = Modifier
) {
    val parts = remember(instruction) {
        parseInstructionParts(instruction)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (part in parts) {
            when (part) {
                is InstructionPart.Text -> {
                    FormattedText(
                        text = part.text,
                        style = KipuH4
                    )
                }
                is InstructionPart.Code -> {
                    CodeMarkdown(
                        code = part.code,
                        language = part.language
                    )
                }
            }
        }
    }
}

fun parseInstructionParts(raw: String): List<InstructionPart> {
    val parts = mutableListOf<InstructionPart>()
    // Soporta cualquier lenguaje (incluyendo c#, c++, f#, terminal, text, etc.) y múltiples bloques de código
    val codeBlockRegex = Regex("""```([^\r\n]*)\r?\n([\s\S]*?)```""")
    var lastIndex = 0

    for (match in codeBlockRegex.findAll(raw)) {
        if (match.range.first > lastIndex) {
            val textBefore = raw.substring(lastIndex, match.range.first).trim()
            if (textBefore.isNotEmpty()) {
                parts.add(InstructionPart.Text(textBefore))
            }
        }

        val language = match.groupValues[1].trim().ifBlank { "text" }
        val code = match.groupValues[2].trim()
        parts.add(InstructionPart.Code(code = code, language = language))

        lastIndex = match.range.last + 1
    }

    if (lastIndex < raw.length) {
        val textAfter = raw.substring(lastIndex).trim()
        if (textAfter.isNotEmpty()) {
            parts.add(InstructionPart.Text(textAfter))
        }
    }

    if (parts.isEmpty() && raw.trim().isNotEmpty()) {
        parts.add(InstructionPart.Text(raw.trim()))
    }

    return parts
}

@Preview(showBackground = true, name = "Exercise Instruction Preview")
@Composable
fun ExerciseInstructionPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExerciseInstruction(
            instruction = "Tras ejecutar:\n```terminal\ndotnet new console -n HolaMundo\n```\n, reemplazas el contenido de un archivo por este código. ¿De qué archivo se trata?\n\n```c#\nConsole.WriteLine(\"¡Hola, C#!\");\n```"
        )
    }
}

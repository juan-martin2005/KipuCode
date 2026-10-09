package com.kipucode.ui.screens.exercise.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.ui.components.text.FormattedText
import com.kipucode.ui.theme.JetBrains
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuH4
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

// ============================================================================================
//  MODELOS DE DATOS PARA LÍNEAS DE CÓDIGO Y RANURAS DINÁMICAS
// ============================================================================================

data class CodeSnippetData(
    val language: String,
    val lines: List<CodeSnippetLine>,
    val totalSlots: Int
)

data class CodeSnippetLine(
    val lineNumber: Int,
    val segments: List<LineSegment>
)

sealed interface LineSegment {
    data class Text(val text: String) : LineSegment
    data class Slot(val index: Int) : LineSegment
}

// ============================================================================================
//  COMPOSABLE PRINCIPAL: BLOCK COMPLETION EXERCISE
// ============================================================================================

@Composable
fun BlockCompletion(
    current: Int,
    total: Int,
    instruction: String,
    options: List<BlockOptionDomain>,
    placedBlocks: Map<Int, BlockOptionDomain>,
    availableBlocks: List<BlockOptionDomain>,
    isEvaluated: Boolean,
    onSelectBlock: (BlockOptionDomain, totalSlots: Int) -> Unit,
    onRemoveBlock: (slotIndex: Int) -> Unit,
    onSubmitAnswer: (totalSlots: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Analizamos la instrucción para separar la descripción y el código interactivo con slots
    val (descriptionText, snippetData) = remember(instruction, options) {
        parseInstructionAndCode(instruction, options)
    }

    val totalSlots = if (snippetData.totalSlots > 0) {
        snippetData.totalSlots
    } else {
        options.count { it.isCorrect }.coerceAtLeast(1)
    }

    val allSlotsFilled = placedBlocks.size == totalSlots

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRA DE PROGRESO EXISTENTE (MANTENIDA INTACTA) ---
        ExerciseProgressBar(
            current = current,
            total = total,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --- ENUNCIADO / INSTRUCCIÓN DEL EJERCICIO ---
        if (descriptionText.isNotBlank()) {
            FormattedText(
                text = descriptionText,
                style = KipuH4,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // --- CONTENEDOR DE CÓDIGO EN TEMA OSCURO (DARK IDE) ---
        DarkCodeBox(
            snippetData = snippetData,
            placedBlocks = placedBlocks,
            isEvaluated = isEvaluated,
            onRemoveBlock = onRemoveBlock,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --- INDICADOR DE AYUDA (i) Toca un bloque ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Toca un bloque para completar el codigo",
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SECCIÓN: BLOQUES DISPONIBLES EN CUADRÍCULA DE 2 COLUMNAS ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Bloques disponibles",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = KipuDarkBlue
            )

            // Cuadrícula simétrica de 2 columnas para una apariencia espaciosa y táctil
            val pairedBlocks = remember(availableBlocks) { availableBlocks.chunked(2) }

            pairedBlocks.forEach { rowBlocks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CodeBlockCard(
                        block = rowBlocks[0],
                        onClick = {
                            if (!isEvaluated) {
                                onSelectBlock(rowBlocks[0], totalSlots)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    if (rowBlocks.size > 1) {
                        CodeBlockCard(
                            block = rowBlocks[1],
                            onClick = {
                                if (!isEvaluated) {
                                onSelectBlock(rowBlocks[1], totalSlots)
                            }
                        },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- BOTÓN COMPROBAR ESTILO MOCKUP ---
        Button(
            onClick = { onSubmitAnswer(totalSlots) },
            enabled = allSlotsFilled && !isEvaluated,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KipuTeal,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFCBD5E1),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = if (isEvaluated) "Comprobado" else "Comprobar",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

// ============================================================================================
//  CONTENEDOR DE CÓDIGO EN TEMA OSCURO (DARK THEME IDE) CON SYNTAX HIGHLIGHTING
// ============================================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DarkCodeBox(
    snippetData: CodeSnippetData,
    placedBlocks: Map<Int, BlockOptionDomain>,
    isEvaluated: Boolean,
    onRemoveBlock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF16222F),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Cabecera superior interna: Código C#
        Text(
            text = "Código ${snippetData.language}",
            fontFamily = JetBrains,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        // Líneas de código con scroll horizontal si es necesario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            snippetData.lines.forEach { line ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Número de línea en gris pizarra
                    Text(
                        text = "${line.lineNumber}".padStart(2, ' '),
                        fontFamily = JetBrains,
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )

                    // Segmentos de la línea (código coloreado intercalado con slots)
                    FlowRow(
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        line.segments.forEach { segment ->
                            when (segment) {
                                is LineSegment.Text -> {
                                    if (segment.text.isNotEmpty()) {
                                        Text(
                                            text = highlightCSharpCode(segment.text),
                                            fontFamily = JetBrains,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                                is LineSegment.Slot -> {
                                    DashedCodeSlotBox(
                                        slotIndex = segment.index,
                                        placedBlock = placedBlocks[segment.index],
                                        isEvaluated = isEvaluated,
                                        onRemoveBlock = onRemoveBlock,
                                        modifier = Modifier.align(Alignment.CenterVertically)
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

// ============================================================================================
//  RANURA CON BORDE GUIONADO EN CIAN (DASHED SLOT)
// ============================================================================================

@Composable
fun DashedCodeSlotBox(
    slotIndex: Int,
    placedBlock: BlockOptionDomain?,
    isEvaluated: Boolean,
    onRemoveBlock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = placedBlock,
        transitionSpec = {
            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
        },
        label = "slot_transition_$slotIndex",
        modifier = modifier
    ) { block ->
        if (block == null) {
            // Ranura vacía: borde punteado en cian brillante con guion central ——
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .defaultMinSize(minWidth = 84.dp)
                    .drawBehind {
                        val stroke = Stroke(
                            width = 1.8.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                        drawRoundRect(
                            color = Color(0xFF00E5FF),
                            cornerRadius = CornerRadius(10.dp.toPx()),
                            style = stroke
                        )
                    }
                    .background(
                        color = Color(0xFF16222F),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "——",
                    fontFamily = JetBrains,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF00E5FF).copy(alpha = 0.5f)
                )
            }
        } else {
            // Ranura ocupada: ficha sólida con borde suave y botón × para retirar
            val bgColor = when {
                !isEvaluated -> KipuTeal
                block.isCorrect -> Color(0xFF2E7D32) // Verde éxito
                else -> Color(0xFFD32F2F) // Rojo error
            }

            Surface(
                modifier = Modifier
                    .height(34.dp)
                    .clickable(enabled = !isEvaluated) { onRemoveBlock(slotIndex) },
                shape = RoundedCornerShape(10.dp),
                color = bgColor,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = block.content,
                        fontFamily = JetBrains,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    if (!isEvaluated) {
                        Text(
                            text = "×",
                            fontFamily = JetBrains,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================================
//  TARJETA DE BLOQUE DISPONIBLE (CUADRÍCULA EN 2 COLUMNAS)
// ============================================================================================

@Composable
fun CodeBlockCard(
    block: BlockOptionDomain,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = block.content,
                fontFamily = JetBrains,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )
        }
    }
}

// ============================================================================================
//  TOKENIZADOR Y COLOREADO DE SINTAXIS C#
// ============================================================================================

fun highlightCSharpCode(code: String): AnnotatedString {
    val builder = AnnotatedString.Builder()

    // Si es un comentario de una sola línea
    if (code.trimStart().startsWith("//")) {
        builder.pushStyle(SpanStyle(color = Color(0xFF64748B), fontStyle = FontStyle.Italic))
        builder.append(code)
        builder.pop()
        return builder.toAnnotatedString()
    }

    val tokenRegex = Regex("""(//[^\n]*)|("[^"]*")|(\b(?:string|int|double|bool|char|void|class|static|using|return|var|new|if|else|for|while)\b)|(\b(?:Console|System|Math)\b)|(\b\w+\b)|([^\w\s]+|\s+)""")

    for (match in tokenRegex.findAll(code)) {
        val (comment, str, keyword, systemType, word, _) = match.destructured
        when {
            comment.isNotEmpty() -> {
                builder.pushStyle(SpanStyle(color = Color(0xFF64748B), fontStyle = FontStyle.Italic))
                builder.append(comment)
                builder.pop()
            }
            str.isNotEmpty() -> {
                builder.pushStyle(SpanStyle(color = Color(0xFFA5D6A7))) // Verde suave para strings
                builder.append(str)
                builder.pop()
            }
            keyword.isNotEmpty() -> {
                builder.pushStyle(SpanStyle(color = Color(0xFFE879F9), fontWeight = FontWeight.Bold)) // Fucsia/Magenta
                builder.append(keyword)
                builder.pop()
            }
            systemType.isNotEmpty() -> {
                builder.pushStyle(SpanStyle(color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)) // Cian brillante
                builder.append(systemType)
                builder.pop()
            }
            word.isNotEmpty() -> {
                builder.pushStyle(SpanStyle(color = Color(0xFF38BDF8))) // Identificadores (ej: mensaje) en cian
                builder.append(word)
                builder.pop()
            }
            else -> {
                builder.pushStyle(SpanStyle(color = Color(0xFFF1F5F9))) // Puntuación y operadores en blanco
                builder.append(match.value)
                builder.pop()
            }
        }
    }

    return builder.toAnnotatedString()
}

// ============================================================================================
//  PARSER DE INSTRUCCIÓN Y DELIMITADORES DE SLOTS
// ============================================================================================

fun parseInstructionAndCode(
    rawInstruction: String,
    options: List<BlockOptionDomain>
): Pair<String, CodeSnippetData> {
    val codeBlockRegex = Regex("""```([^\r\n]*)\r?\n([\s\S]*?)```""")
    val match = codeBlockRegex.find(rawInstruction)

    val (desc, rawCode, lang) = if (match != null) {
        val before = rawInstruction.substring(0, match.range.first).trim()
        val code = match.groupValues[2].trim()
        val language = match.groupValues[1].trim().ifBlank { "C#" }
        Triple(before, code, language)
    } else {
        if (rawInstruction.contains("[[slot]]") || rawInstruction.contains("___")) {
            Triple("", rawInstruction.trim(), "C#")
        } else {
            Triple(rawInstruction.trim(), "", "C#")
        }
    }

    val lines = rawCode.lines()
    val slotRegex = Regex("""\[\[slot(?::\d+)?\]\]|___""")
    var slotCounter = 0
    val resultLines = mutableListOf<CodeSnippetLine>()

    lines.forEachIndexed { index, lineText ->
        val segments = mutableListOf<LineSegment>()
        var lastIdx = 0

        for (slotMatch in slotRegex.findAll(lineText)) {
            if (slotMatch.range.first > lastIdx) {
                val textBefore = lineText.substring(lastIdx, slotMatch.range.first)
                if (textBefore.isNotEmpty()) {
                    segments.add(LineSegment.Text(textBefore))
                }
            }
            segments.add(LineSegment.Slot(slotCounter))
            slotCounter++
            lastIdx = slotMatch.range.last + 1
        }

        if (lastIdx < lineText.length) {
            val textAfter = lineText.substring(lastIdx)
            if (textAfter.isNotEmpty()) {
                segments.add(LineSegment.Text(textAfter))
            }
        }

        if (segments.isEmpty()) {
            segments.add(LineSegment.Text(""))
        }

        resultLines.add(CodeSnippetLine(lineNumber = index + 1, segments = segments))
    }

    val finalSnippet = CodeSnippetData(
        language = lang,
        lines = resultLines,
        totalSlots = slotCounter
    )

    return desc to finalSnippet
}

// ============================================================================================
//  PREVIEW
// ============================================================================================

@Preview(showBackground = true, name = "Block Completion Styled Preview")
@Composable
fun BlockCompletionStyledPreview() {
    val options = listOf(
        BlockOptionDomain(id = "1", exerciseId = "ex1", content = "\"Hola\"", isCorrect = true, orderIndex = 0),
        BlockOptionDomain(id = "2", exerciseId = "ex1", content = "WriteLine", isCorrect = true, orderIndex = 1),
        BlockOptionDomain(id = "3", exerciseId = "ex1", content = "\"Adiós\"", isCorrect = false, orderIndex = 1),
        BlockOptionDomain(id = "4", exerciseId = "ex1", content = "ReadLine", isCorrect = false, orderIndex = 0)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BlockCompletion(
            current = 3,
            total = 5,
            instruction = "Completa el saludo:\n```c#\n// Saludo en C#\nstring mensaje = [[slot]];\n// Muestra el mensaje\nConsole.[[slot]](mensaje);\n```",
            options = options,
            placedBlocks = emptyMap(),
            availableBlocks = options,
            isEvaluated = false,
            onSelectBlock = { _, _ -> },
            onRemoveBlock = {},
            onSubmitAnswer = {}
        )
    }
}

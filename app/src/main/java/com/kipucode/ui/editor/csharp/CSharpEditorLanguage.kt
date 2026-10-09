package com.kipucode.ui.editor.csharp

import android.os.Bundle
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.analysis.SimpleAnalyzeManager
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler
import io.github.rosemoe.sora.lang.styling.CodeBlock
import io.github.rosemoe.sora.lang.styling.MappedSpans
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.lang.styling.TextStyle
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.widget.SymbolPairMatch
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import java.util.Stack

class CSharpEditorLanguage : Language {

    private val analyzeManager = CSharpAnalyzeManager()
    private val symbolPairs = SymbolPairMatch.DefaultSymbolPairs()

    override fun getAnalyzeManager(): AnalyzeManager = analyzeManager

    override fun getInterruptionLevel(): Int = Language.INTERRUPTION_LEVEL_SLIGHT

    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {
        CSharpCompletionProvider.provideCompletions(content, position, publisher)
    }

    override fun getIndentAdvance(content: ContentReference, line: Int, column: Int): Int {
        val lineText = content.getLine(line).trim()
        return if (lineText.endsWith("{")) 4 else 0
    }

    override fun useTab(): Boolean = false

    override fun getFormatter(): Formatter = EmptyLanguage.EmptyFormatter.INSTANCE

    override fun getSymbolPairs(): SymbolPairMatch = symbolPairs

    override fun getNewlineHandlers(): Array<NewlineHandler> = emptyArray()

    override fun destroy() {
        analyzeManager.destroy()
    }

    private class CSharpAnalyzeManager : SimpleAnalyzeManager<Any>() {

        private val KEYWORDS = hashSetOf(
            "abstract", "as", "async", "await", "base", "bool", "break", "byte",
            "case", "catch", "char", "checked", "class", "const", "continue",
            "decimal", "default", "delegate", "do", "double", "else", "enum",
            "event", "explicit", "extern", "false", "finally", "fixed", "float",
            "for", "foreach", "goto", "if", "implicit", "in", "int", "interface",
            "internal", "is", "lock", "long", "namespace", "new", "null", "object",
            "operator", "out", "override", "params", "private", "protected", "public",
            "readonly", "ref", "return", "sbyte", "sealed", "short", "sizeof",
            "stackalloc", "static", "string", "struct", "switch", "this", "throw",
            "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe",
            "ushort", "using", "virtual", "void", "volatile", "while", "yield",
            "var", "get", "set", "record", "init", "value"
        )

        private val STANDARD_TYPES = hashSetOf(
            "Console", "Math", "Convert", "Program", "Main", "Task", "List",
            "Dictionary", "Array", "String", "Int32", "Int64", "Boolean",
            "DateTime", "TimeSpan", "Exception", "StringBuilder", "Enumerable"
        )

        override fun analyze(text: StringBuilder, delegate: Delegate<Any>): Styles {
            val lines = text.split("\n")
            val builder = MappedSpans.Builder(lines.size)
            val codeBlocks = mutableListOf<CodeBlock>()
            val blockStack = Stack<Pair<Int, Int>>() // Pair<Line, Column>
            var inBlockComment = false

            for (lineIndex in lines.indices) {
                if (delegate.isCancelled) return Styles()

                val line = lines[lineIndex]
                val length = line.length
                var i = 0

                // Si venía un comentario multilínea desde la línea anterior
                if (inBlockComment) {
                    builder.addIfNeeded(lineIndex, 0, TextStyle.makeStyle(EditorColorScheme.COMMENT))
                    val endComment = line.indexOf("*/")
                    if (endComment != -1) {
                        inBlockComment = false
                        i = endComment + 2
                        if (i < length) {
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        }
                    } else {
                        continue
                    }
                }

                while (i < length) {
                    val c = line[i]

                    // 1. Espacios en blanco
                    if (Character.isWhitespace(c)) {
                        i++
                        continue
                    }

                    // 2. Comentarios
                    if (c == '/' && i + 1 < length) {
                        if (line[i + 1] == '/') {
                            // Comentario de una sola línea //
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.COMMENT))
                            break // Fin de la línea actual
                        } else if (line[i + 1] == '*') {
                            // Comentario multilínea /*
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.COMMENT))
                            val endIdx = line.indexOf("*/", i + 2)
                            if (endIdx != -1) {
                                i = endIdx + 2
                                if (i < length) {
                                    builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                                }
                                continue
                            } else {
                                inBlockComment = true
                                break
                            }
                        }
                    }

                    // 3. Literales de cadena ("...", @"...", $"...")
                    if (c == '"' || ((c == '@' || c == '$') && i + 1 < length && line[i + 1] == '"')) {
                        val start = i
                        builder.addIfNeeded(lineIndex, start, TextStyle.makeStyle(EditorColorScheme.LITERAL))
                        if (c == '@' || c == '$') i++
                        i++ // saltar las comillas de apertura
                        while (i < length) {
                            if (line[i] == '\\' && i + 1 < length) {
                                i += 2
                            } else if (line[i] == '"') {
                                i++
                                break
                            } else {
                                i++
                            }
                        }
                        if (i < length) {
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        }
                        continue
                    }

                    // 4. Literales de caracter ('c', '\n')
                    if (c == '\'') {
                        builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.LITERAL))
                        i++
                        while (i < length && line[i] != '\'') {
                            if (line[i] == '\\' && i + 1 < length) i++
                            i++
                        }
                        if (i < length && line[i] == '\'') i++
                        if (i < length) {
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        }
                        continue
                    }

                    // 5. Números (123, 0x12, 12.34f)
                    if (Character.isDigit(c)) {
                        val start = i
                        builder.addIfNeeded(lineIndex, start, TextStyle.makeStyle(EditorColorScheme.LITERAL))
                        while (i < length && (Character.isLetterOrDigit(line[i]) || line[i] == '.')) {
                            i++
                        }
                        if (i < length) {
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        }
                        continue
                    }

                    // 6. Identificadores, palabras clave y métodos
                    if (Character.isJavaIdentifierStart(c)) {
                        val start = i
                        while (i < length && Character.isJavaIdentifierPart(line[i])) {
                            i++
                        }
                        val word = line.substring(start, i)

                        // Comprobar si el identificador es seguido por '(' (llamada a método)
                        var isMethodCall = false
                        var peek = i
                        while (peek < length && Character.isWhitespace(line[peek])) peek++
                        if (peek < length && line[peek] == '(') {
                            isMethodCall = true
                        }

                        val style = when {
                            KEYWORDS.contains(word) -> TextStyle.makeStyle(EditorColorScheme.KEYWORD)
                            STANDARD_TYPES.contains(word) -> TextStyle.makeStyle(EditorColorScheme.IDENTIFIER_NAME)
                            isMethodCall -> TextStyle.makeStyle(EditorColorScheme.FUNCTION_NAME)
                            else -> TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL)
                        }

                        builder.addIfNeeded(lineIndex, start, style)
                        if (i < length) {
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        }
                        continue
                    }

                    // 7. Bloques de código plegables { ... }
                    if (c == '{') {
                        blockStack.push(Pair(lineIndex, i))
                        builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        i++
                        continue
                    } else if (c == '}') {
                        if (blockStack.isNotEmpty()) {
                            val startBlock = blockStack.pop()
                            val codeBlock = CodeBlock().apply {
                                startLine = startBlock.first
                                startColumn = startBlock.second
                                endLine = lineIndex
                                endColumn = i
                            }
                            codeBlocks.add(codeBlock)
                        }
                        builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        i++
                        continue
                    }

                    // 8. Operadores
                    if ("+-*/%=<>!&|^~?:".indexOf(c) != -1) {
                        builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.OPERATOR))
                        i++
                        if (i < length) {
                            builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                        }
                        continue
                    }

                    // Caracter normal
                    builder.addIfNeeded(lineIndex, i, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))
                    i++
                }
            }

            if (lines.isNotEmpty()) {
                builder.determine(lines.size - 1)
            } else {
                builder.addNormalIfNull()
            }

            val spans = builder.build()
            val styles = Styles(spans)
            for (block in codeBlocks) {
                styles.addCodeBlock(block)
            }
            styles.finishBuilding()

            return styles
        }
    }
}
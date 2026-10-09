package com.kipucode.ui.editor.csharp

import io.github.rosemoe.sora.lang.completion.CompletionItem
import io.github.rosemoe.sora.lang.completion.CompletionItemKind
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import java.util.Locale

object CSharpCompletionProvider {

    private val KEYWORDS = listOf(
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
        "ushort", "using", "virtual", "void", "volatile", "while", "var", "get", "set"
    )

    private val COMMON_TYPES = listOf(
        "Console", "Math", "Convert", "Program", "Main", "Task", "List",
        "Dictionary", "Array", "String", "Int32", "Int64", "Boolean",
        "DateTime", "TimeSpan", "Exception", "StringBuilder", "Enumerable"
    )

    private val COMMON_METHODS = listOf(
        "WriteLine", "ReadLine", "Write", "Parse", "ToString", "Add",
        "Remove", "Contains", "Clear", "Equals", "Substring", "IndexOf",
        "Split", "Join", "Length", "Count", "Max", "Min", "Sqrt", "Pow", "Abs"
    )

    private data class SnippetDefinition(
        val prefix: String,
        val label: String,
        val commitText: String,
        val description: String
    )

    private val SNIPPETS = listOf(
        SnippetDefinition(
            prefix = "cw",
            label = "cw",
            commitText = "Console.WriteLine();",
            description = "Console.WriteLine();"
        ),
        SnippetDefinition(
            prefix = "cr",
            label = "cr",
            commitText = "Console.ReadLine();",
            description = "Console.ReadLine();"
        ),
        SnippetDefinition(
            prefix = "for",
            label = "for",
            commitText = "for (int i = 0; i < length; i++)\n{\n    \n}",
            description = "Bucle for indexado"
        ),
        SnippetDefinition(
            prefix = "foreach",
            label = "foreach",
            commitText = "foreach (var item in collection)\n{\n    \n}",
            description = "Bucle foreach"
        ),
        SnippetDefinition(
            prefix = "while",
            label = "while",
            commitText = "while (condition)\n{\n    \n}",
            description = "Bucle while"
        ),
        SnippetDefinition(
            prefix = "ifelse",
            label = "ifelse",
            commitText = "if (condition)\n{\n    \n}\nelse\n{\n    \n}",
            description = "Estructura if-else"
        ),
        SnippetDefinition(
            prefix = "prop",
            label = "prop",
            commitText = "public int MyProperty { get; set; }",
            description = "Propiedad auto-implementada"
        )
    )

    fun provideCompletions(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher
    ) {
        val line = content.getLine(position.line)
        var startCol = position.column
        while (startCol > 0 && (Character.isJavaIdentifierPart(line[startCol - 1]) || line[startCol - 1] == '.')) {
            startCol--
        }

        val prefix = line.substring(startCol, position.column)
        if (prefix.isBlank()) return

        val prefixLength = prefix.length
        val normalizedPrefix = prefix.lowercase(Locale.ROOT)
        val items = mutableListOf<CompletionItem>()

        // 1. Snippets
        for (snippet in SNIPPETS) {
            if (snippet.prefix.startsWith(normalizedPrefix)) {
                items.add(
                    SimpleCompletionItem(snippet.label, snippet.description, prefixLength, snippet.commitText)
                        .kind(CompletionItemKind.Snippet)
                )
            }
        }

        // 2. Keywords
        for (kw in KEYWORDS) {
            if (kw.startsWith(normalizedPrefix)) {
                items.add(
                    SimpleCompletionItem(kw, "keyword", prefixLength, kw)
                        .kind(CompletionItemKind.Keyword)
                )
            }
        }

        // 3. Types / Classes
        for (type in COMMON_TYPES) {
            if (type.lowercase(Locale.ROOT).startsWith(normalizedPrefix)) {
                items.add(
                    SimpleCompletionItem(type, "class", prefixLength, type)
                        .kind(CompletionItemKind.Class)
                )
            }
        }

        // 4. Common Methods
        for (method in COMMON_METHODS) {
            if (method.lowercase(Locale.ROOT).startsWith(normalizedPrefix)) {
                items.add(
                    SimpleCompletionItem("$method()", "method", prefixLength, "$method()")
                        .kind(CompletionItemKind.Method)
                )
            }
        }

        if (items.isNotEmpty()) {
            publisher.setComparator { a, b -> a.label.toString().compareTo(b.label.toString(), ignoreCase = true) }
            publisher.addItems(items)
            publisher.updateList()
        }
    }
}
package com.kipucode.viewmodel

import com.kipucode.domain.model.excecution.CodeDiagnosticDomain
import com.kipucode.domain.model.excecution.CodeExecutionResultDomain

const val DEFAULT_CSHARP_TEMPLATE = """using System;

class Program
{
    static void Main()
    {
        Console.WriteLine("Bienvenido a KipuCode!!");
    }
}
"""

data class CodeEditorUiState(
    val isRunning: Boolean = false,
    val sourceCode: String = DEFAULT_CSHARP_TEMPLATE,
    val languageKey: String = "csharp",
    val result: CodeExecutionResultDomain? = null,
    val diagnostics: List<CodeDiagnosticDomain> = emptyList(),
    val errorMessage: String? = null
)
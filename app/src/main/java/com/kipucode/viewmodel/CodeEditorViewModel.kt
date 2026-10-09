package com.kipucode.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.excecution.CodeExecutionEvent
import com.kipucode.domain.model.excecution.CodeExecutionRequestDomain
import com.kipucode.domain.usecase.execution.CodeExecutionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CodeEditorViewModel @Inject constructor(
    private val codeExecutionUseCase: CodeExecutionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CodeEditorUiState())
    val uiState: StateFlow<CodeEditorUiState> = _uiState.asStateFlow()

    fun setInitialCode(code: String, languageKey: String = "csharp") {
        _uiState.update {
            it.copy(
                sourceCode = code,
                languageKey = languageKey
            )
        }
    }

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(sourceCode = newCode) }
    }

    fun executeCode(stdin: String? = null) {
        val currentState = _uiState.value
        if (currentState.isRunning || currentState.sourceCode.isBlank()) return

        viewModelScope.launch {
            val request = CodeExecutionRequestDomain(
                languageKey = currentState.languageKey,
                sourceCode = currentState.sourceCode,
                stdin = stdin ?: ""
            )

            codeExecutionUseCase(request).collect { event ->
                when (event) {
                    is CodeExecutionEvent.Idle -> {
                        _uiState.update { it.copy(isRunning = false) }
                    }
                    is CodeExecutionEvent.Running -> {
                        _uiState.update {
                            it.copy(
                                isRunning = true,
                                errorMessage = null
                            )
                        }
                    }
                    is CodeExecutionEvent.Success -> {
                        _uiState.update {
                            it.copy(
                                isRunning = false,
                                result = event.result,
                                diagnostics = event.result.diagnostics,
                                errorMessage = null
                            )
                        }
                    }
                    is CodeExecutionEvent.Error -> {
                        _uiState.update {
                            it.copy(
                                isRunning = false,
                                diagnostics = event.diagnostics,
                                errorMessage = event.message
                            )
                        }
                    }
                }
            }
        }
    }
}
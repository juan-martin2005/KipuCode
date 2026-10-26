package com.kipucode.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.auth.LoginUseCase
import com.kipucode.domain.usecase.auth.ValidateEmailUseCase
import com.kipucode.domain.usecase.auth.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun validateLoginForm(email: String, pass: String): Boolean {
        val emailResult = validateEmailUseCase(email)
        val passwordResult = validatePasswordUseCase(pass, requireComplexity = false)

        _uiState.update { current ->
            current.copy(
                formErrors = current.formErrors.copy(
                    emailError = emailResult.errorType,
                    passwordError = passwordResult.errorType
                )
            )
        }

        return emailResult.successful && passwordResult.successful
    }

    fun clearEmailError() {
        if (_uiState.value.formErrors.emailError != null) {
            _uiState.update { it.copy(formErrors = it.formErrors.copy(emailError = null)) }
        }
    }

    fun clearPasswordError() {
        if (_uiState.value.formErrors.passwordError != null) {
            _uiState.update { it.copy(formErrors = it.formErrors.copy(passwordError = null)) }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(loginResponse = null) }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loginResponse = Response.Loading) }
            val result = loginUseCase(email, password)
            _uiState.update { it.copy(loginResponse = result) }
        }
    }
}

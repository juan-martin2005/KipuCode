package com.kipucode.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ValidationResult
import com.kipucode.domain.usecase.auth.ResetPasswordUseCase
import com.kipucode.domain.usecase.auth.ValidateEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun validateEmail(email: String): ValidationResult {
        return validateEmailUseCase(email)
    }

    fun resetState() {
        _uiState.update { it.copy(resetResponse = null) }
    }

    fun setEmailError(error: String?) {
        _uiState.update { it.copy(emailError = error) }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(resetResponse = Response.Loading) }
            val result = resetPasswordUseCase(email)
            _uiState.update { it.copy(resetResponse = result) }
        }
    }
}

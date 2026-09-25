package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.user.ReauthenticateUseCase
import com.kipucode.domain.usecase.user.UpdatePasswordUseCase
import com.kipucode.domain.usecase.user.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.user.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PasswordUiState(
    val currentStep: Int = 1, // 1: Verificar contraseña actual, 2: Nueva contraseña
    val currentPasswordError: ValidationErrorType? = null,
    val newPasswordError: ValidationErrorType? = null,
    val confirmPasswordError: ValidationErrorType? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val serverError: String? = null
)

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val reauthenticateUseCase: ReauthenticateUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()

    // --- PASO 1: Verificar contraseña actual ---
    fun verifyCurrentPassword(currentPass: String) {
        val validation = validatePasswordUseCase(currentPass)
        if (!validation.successful) {
            _uiState.update { it.copy(currentPasswordError = validation.errorType) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, serverError = null, currentPasswordError = null) }
            when (val result = reauthenticateUseCase(currentPass)) {
                is Response.Success -> {
                    _uiState.update { it.copy(isLoading = false, currentStep = 2, serverError = null) }
                }
                is Response.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            serverError = "La contraseña actual es incorrecta"
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    // --- PASO 2: Validar y actualizar nueva contraseña ---
    fun submitNewPassword(newPass: String, confirmPass: String) {
        val passValidation = validatePasswordUseCase(newPass)
        val confirmValidation = validateConfirmPasswordUseCase(newPass, confirmPass)

        _uiState.update {
            it.copy(
                newPasswordError = passValidation.errorType,
                confirmPasswordError = confirmValidation.errorType,
                serverError = null
            )
        }

        if (!passValidation.successful || !confirmValidation.successful) {
            return
        }

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, serverError = null) }
            when (val result = updatePasswordUseCase(newPass)) {
                is Response.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Response.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            serverError = result.message ?: "Ocurrió un error al actualizar la contraseña"
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    fun clearCurrentPasswordError() {
        if (_uiState.value.currentPasswordError != null || _uiState.value.serverError != null) {
            _uiState.update { it.copy(currentPasswordError = null, serverError = null) }
        }
    }

    fun clearNewPasswordError() {
        if (_uiState.value.newPasswordError != null) {
            _uiState.update { it.copy(newPasswordError = null) }
        }
    }

    fun clearConfirmPasswordError() {
        if (_uiState.value.confirmPasswordError != null) {
            _uiState.update { it.copy(confirmPasswordError = null) }
        }
    }

    fun resetState() {
        _uiState.value = PasswordUiState()
    }
}
package com.kipucode.ui.screens.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.auth.ReauthenticateUseCase
import com.kipucode.domain.usecase.auth.UpdatePasswordUseCase
import com.kipucode.domain.usecase.auth.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.auth.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val reauthenticateUseCase: ReauthenticateUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    // --- PASO 1: Verificar contraseña actual ---
    fun verifyCurrentPassword(currentPass: String) {
        val validation = validatePasswordUseCase(currentPass, requireComplexity = false)
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
        if (_uiState.value.newPasswordError != null || _uiState.value.serverError != null) {
            _uiState.update { it.copy(newPasswordError = null, serverError = null) }
        }
    }

    fun clearConfirmPasswordError() {
        if (_uiState.value.confirmPasswordError != null || _uiState.value.serverError != null) {
            _uiState.update { it.copy(confirmPasswordError = null, serverError = null) }
        }
    }

    fun resetState() {
        _uiState.value = ChangePasswordUiState()
    }
}

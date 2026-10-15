package com.kipucode.ui.screens.changepassword

import com.kipucode.domain.model.ValidationErrorType

data class ChangePasswordUiState(
    val currentStep: Int = 1, // 1: Verificar contraseña actual, 2: Nueva contraseña
    val currentPasswordError: ValidationErrorType? = null,
    val newPasswordError: ValidationErrorType? = null,
    val confirmPasswordError: ValidationErrorType? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val serverError: String? = null
)

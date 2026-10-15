package com.kipucode.ui.screens.register

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.ValidationErrorType

data class RegisterFormErrors(
    val nameError: ValidationErrorType? = null,
    val emailError: ValidationErrorType? = null,
    val passwordError: ValidationErrorType? = null,
    val confirmPasswordError: ValidationErrorType? = null
)

data class RegisterUiState(
    val registerResponse: Response<UserDomain>? = null,
    val formErrors: RegisterFormErrors = RegisterFormErrors()
)

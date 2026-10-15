package com.kipucode.ui.screens.login

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.ValidationErrorType

data class LoginFormErrors(
    val emailError: ValidationErrorType? = null,
    val passwordError: ValidationErrorType? = null
)

data class LoginUiState(
    val loginResponse: Response<UserDomain>? = null,
    val formErrors: LoginFormErrors = LoginFormErrors()
)

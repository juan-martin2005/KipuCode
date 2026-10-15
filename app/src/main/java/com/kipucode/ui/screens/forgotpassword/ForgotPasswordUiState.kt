package com.kipucode.ui.screens.forgotpassword

import com.kipucode.domain.model.Response

data class ForgotPasswordUiState(
    val resetResponse: Response<Unit>? = null,
    val emailError: String? = null
)

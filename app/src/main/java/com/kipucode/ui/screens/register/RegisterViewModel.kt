package com.kipucode.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.usecase.auth.RegisterUseCase
import com.kipucode.domain.usecase.auth.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.auth.ValidateEmailUseCase
import com.kipucode.domain.usecase.auth.ValidateFullNameUseCase
import com.kipucode.domain.usecase.auth.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateNameUseCase: ValidateFullNameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun validateRegisterForm(
        fullName: String,
        email: String,
        pass: String,
        confirmPass: String
    ): Boolean {
        val nameResult = validateNameUseCase(fullName)
        val emailResult = validateEmailUseCase(email)
        val passwordResult = validatePasswordUseCase(pass)
        val confirmPasswordResult = validateConfirmPasswordUseCase(pass, confirmPass)

        _uiState.update { current ->
            current.copy(
                formErrors = current.formErrors.copy(
                    nameError = nameResult.errorType,
                    emailError = emailResult.errorType,
                    passwordError = passwordResult.errorType,
                    confirmPasswordError = confirmPasswordResult.errorType
                )
            )
        }

        return nameResult.successful &&
                emailResult.successful &&
                passwordResult.successful &&
                confirmPasswordResult.successful
    }

    fun clearNameError() {
        if (_uiState.value.formErrors.nameError != null) {
            _uiState.update { it.copy(formErrors = it.formErrors.copy(nameError = null)) }
        }
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

    fun clearConfirmPasswordError() {
        if (_uiState.value.formErrors.confirmPasswordError != null) {
            _uiState.update { it.copy(formErrors = it.formErrors.copy(confirmPasswordError = null)) }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(registerResponse = null) }
    }

    fun register(userDomain: UserDomain, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(registerResponse = Response.Loading) }
            val result = registerUseCase(userDomain, password)
            _uiState.update { it.copy(registerResponse = result) }
        }
    }
}

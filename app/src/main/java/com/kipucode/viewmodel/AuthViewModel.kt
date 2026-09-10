package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.user.IsUserLoggedInUseCase
import com.kipucode.domain.usecase.user.LoginUseCases
import com.kipucode.domain.usecase.user.LogoutUseCase
import com.kipucode.domain.usecase.user.RegisterUseCase
import com.kipucode.domain.usecase.user.ResetPasswordUseCase
import com.kipucode.domain.usecase.user.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.user.ValidateEmailUseCase
import com.kipucode.domain.usecase.user.ValidateFullNameUseCase
import com.kipucode.domain.usecase.user.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterFormErrors(
    val nameError: ValidationErrorType? = null,
    val emailError: ValidationErrorType? = null,
    val passwordError: ValidationErrorType? = null,
    val confirmPasswordError: ValidationErrorType? = null
)

data class LoginFormErrors(
    val emailError: ValidationErrorType? = null,
    val passwordError: ValidationErrorType? = null
)

// ============================================================================================
//  VIEWMODEL DE AUTENTICACIÓN
// ============================================================================================
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val validateNameUseCase: ValidateFullNameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase,

    private val loginUseCases: LoginUseCases,
    private val registerUseCase: RegisterUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    // Estados privados -> El estado solo puede cambiar en la clase AuthViewModel
    private val _authState = MutableStateFlow<Response<UserDomain>?>(null)
    private val _resetPasswordState = MutableStateFlow<Response<Unit>?>(null)
    private val _registerFormErrorsState = MutableStateFlow(RegisterFormErrors())
    private val _loginFormErrorsState = MutableStateFlow(LoginFormErrors())


    // Estados públicos -> Permite reaccionar a los cambios sin modificarlos
    val authState: StateFlow<Response<UserDomain>?> = _authState
    val resetPasswordState: StateFlow<Response<Unit>?> = _resetPasswordState
    val registerFormErrorsState: StateFlow<RegisterFormErrors> = _registerFormErrorsState.asStateFlow()
    val loginFormErrorsState: StateFlow<LoginFormErrors> = _loginFormErrorsState.asStateFlow()


    // ============================================================================================
    // VALIDACIONES DEL FORMULARIO
    // ============================================================================================
    fun validateRegisterForm(
        fullName: String,
        email: String,
        pass: String,
        confirmPass: String
    ): Boolean {
        val nameResult = validateNameUseCase.invoke(fullName)
        val emailResult = validateEmailUseCase.invoke(email)
        val passwordResult = validatePasswordUseCase.invoke(pass)
        val confirmPasswordResult = validateConfirmPasswordUseCase.invoke(pass, confirmPass)

        _registerFormErrorsState.update {
            it.copy(
                nameError = nameResult.errorType,
                emailError = emailResult.errorType,
                passwordError = passwordResult.errorType,
                confirmPasswordError = confirmPasswordResult.errorType
            )
        }

        return nameResult.successful &&
                emailResult.successful &&
                passwordResult.successful &&
                confirmPasswordResult.successful
    }

    fun validateLoginForm(
        email: String,
        pass: String,
    ): Boolean {
        val emailResult = validateEmailUseCase.invoke(email)
        val passwordResult = validatePasswordUseCase.invoke(pass)

        _loginFormErrorsState.update {
            it.copy(
                emailError = emailResult.errorType,
                passwordError = passwordResult.errorType,
            )
        }

        return emailResult.successful && passwordResult.successful
    }

    fun clearNameError() {
        if (_registerFormErrorsState.value.nameError != null) {
            _registerFormErrorsState.update { it.copy(nameError = null) }
        }
    }

    fun clearEmailError() {
        if (_loginFormErrorsState.value.emailError != null) {
            _loginFormErrorsState.update { it.copy(emailError = null) }
        }

        if (_registerFormErrorsState.value.emailError != null) {
            _registerFormErrorsState.update { it.copy(emailError = null) }
        }
    }

    fun clearPasswordError() {
        if (_loginFormErrorsState.value.passwordError != null) {
            _loginFormErrorsState.update { it.copy(passwordError = null) }
        }

        if (_registerFormErrorsState.value.passwordError != null) {
            _registerFormErrorsState.update { it.copy(passwordError = null) }
        }
    }

    fun clearConfirmPasswordError() {
        if (_registerFormErrorsState.value.confirmPasswordError != null) {
            _registerFormErrorsState.update { it.copy(confirmPasswordError = null) }
        }
    }

    // ============================================================================================
    // ACCIONES DE AUTENTICACIÓN
    // ============================================================================================

    fun resetState() {
        _authState.value = null
    }
    fun login (email: String, password: String){
        viewModelScope.launch {
            _authState.value = Response.Loading

            val result = loginUseCases.invoke(email,password)
            _authState.value = result
        }
    }
    fun register(userDomain: UserDomain, password: String) {
        viewModelScope.launch {
            _authState.value = Response.Loading

            val result = registerUseCase.invoke(userDomain,password)
            _authState.value = result
        }
    }

    fun isUserLoggedIn(): Boolean {
        return isUserLoggedInUseCase.invoke()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke()
        }
    }

    fun resetForgotPasswordState() {
        _resetPasswordState.value = null
    }
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = Response.Loading

            val result = resetPasswordUseCase.invoke(email)
            _resetPasswordState.value = result
        }
    }
}
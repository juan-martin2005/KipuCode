package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserDomain
import com.kipucode.domain.usecase.user.IsUserLoggedInUseCase
import com.kipucode.domain.usecase.user.LoginUseCases
import com.kipucode.domain.usecase.user.LogoutUseCase
import com.kipucode.domain.usecase.user.RegisterUseCase
import com.kipucode.domain.usecase.user.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============================================================================================
//  VIEWMODEL DE AUTENTICACIÓN
// ============================================================================================
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val registerUseCase: RegisterUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    // Estados privados -> El estado solo puede cambiar en la clase AuthViewModel
    private val _authState = MutableStateFlow<Response<UserDomain>?>(null)
    private val _resetPasswordState = MutableStateFlow<Response<Unit>?>(null)

    // Estados públicos -> Permite reaccionar a los cambios sin modificarlos
    val authState : StateFlow<Response<UserDomain>?> = _authState
    val resetPasswordState: StateFlow<Response<Unit>?> = _resetPasswordState

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
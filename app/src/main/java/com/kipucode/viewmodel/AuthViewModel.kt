package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.usecase.user.IsUserLoggedInUseCase
import com.kipucode.domain.usecase.user.LoginUseCases
import com.kipucode.domain.usecase.user.LogoutUseCase
import com.kipucode.domain.usecase.user.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val registerUseCase: RegisterUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<Response<User>?>(null)
    val loginState : StateFlow<Response<User>?> = _loginState

    fun resetState() {
        _loginState.value = null
    }

    fun login (email: String, password: String){

        viewModelScope.launch {
            val result = loginUseCases.invoke(email,password)
            _loginState.value = result
        }
    }

    fun register(user: User, password: String) {
        viewModelScope.launch {
            val result = registerUseCase.invoke(user,password)
            _loginState.value = result
        }
    }

    fun isUserLoggedIn(): Boolean {
        return isUserLoggedInUseCase.invoke()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke()
            resetState()
        }
    }
}
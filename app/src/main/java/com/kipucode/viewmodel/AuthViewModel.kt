package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.usecase.user.LoginUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCases: LoginUseCases
) : ViewModel() {

    private val _loginState = MutableStateFlow<Response<User>?>(null)
    val loginState : StateFlow<Response<User>?> = _loginState

    fun login (email: String, password: String){

        viewModelScope.launch {
            val result = loginUseCases.invoke(email,password)
            _loginState.value = result
        }
    }
    fun resetState() {
        _loginState.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                val firebaseDataSource = com.kipucode.data.remote.firebase.service.FirebaseRemoteDataSource(auth)
                val authRepository = com.kipucode.data.repository.AuthRepositoryImpl(firebaseDataSource)
                val loginUseCases = LoginUseCases(authRepository)

                AuthViewModel(
                    loginUseCases = loginUseCases
                )
            }
        }
    }
}
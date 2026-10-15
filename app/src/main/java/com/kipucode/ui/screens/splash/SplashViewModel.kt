package com.kipucode.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.kipucode.domain.usecase.auth.IsUserLoggedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return isUserLoggedInUseCase()
    }
}

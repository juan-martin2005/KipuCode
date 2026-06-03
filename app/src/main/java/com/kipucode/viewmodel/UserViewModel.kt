package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import com.kipucode.domain.model.UserProgress
import com.kipucode.domain.usecase.user.GetUserProfileUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserProgressUseCase : GetUserProgressUseCase,
    private val refreshUserProfileUseCase : RefreshUserProfileUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    private val _userProgress = MutableStateFlow<Response<UserProgress>?>(null)
    val userProfile: StateFlow<User?> = _userProfile
    val userProgress: StateFlow<Response<UserProgress>?> = _userProgress

    private val _refreshState = MutableStateFlow<Response<Unit>?>(null)
    val refreshState: StateFlow<Response<Unit>?> = _refreshState

    init {
        startObservingUser()
        startObservingUserProgress()
        triggerRefresh()
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            _refreshState.value = Response.Loading
            val result = refreshUserProfileUseCase.invoke()
            _refreshState.value = result
        }
    }

    fun startObservingUser() {
        viewModelScope.launch {
            getUserProfileUseCase.invoke().collect { userLocal ->
                _userProfile.value = userLocal
            }
        }

    }

    fun startObservingUserProgress(){
        viewModelScope.launch {
            _userProgress.value = Response.Loading
            val result = getUserProgressUseCase.invoke()
            _userProgress.value = result
        }
    }

    fun swipeToRefresh() {
        triggerRefresh()
    }

    fun resetRefreshState() {
        _refreshState.value = null
    }
}
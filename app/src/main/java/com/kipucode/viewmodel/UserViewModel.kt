package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kipucode.data.repository.UserRepositoryImpl
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _refreshState = MutableStateFlow<Response<Unit>?>(null)
    val refreshState: StateFlow<Response<Unit>?> = _refreshState

    init {
        startObservingUser()
    }

    private fun triggerRefresh(uid: String) {
        viewModelScope.launch {
            _refreshState.value = Response.Loading
            val result = userRepository.refreshUserProfile(uid)
            _refreshState.value = result
        }
    }

    fun startObservingUser() {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUid != null) {
            viewModelScope.launch {
                userRepository.getUserProfile(currentUid).collect { userLocal ->
                    _userProfile.value = userLocal
                }
            }
            triggerRefresh(currentUid)
        } else {
            _userProfile.value = null
        }
    }

    fun swipeToRefresh() {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUid != null) {
            triggerRefresh(currentUid) // Reutiliza la lógica
        }
    }

    fun resetRefreshState() {
        _refreshState.value = null
    }
}
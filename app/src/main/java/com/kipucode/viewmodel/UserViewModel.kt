package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kipucode.data.remote.firebase.service.UserFirestoreSource
import com.kipucode.domain.model.ErrorType
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userFirestoreSource: UserFirestoreSource
) : ViewModel() {

    private val _userState = MutableStateFlow<Response<User>?>(null)
    val userState: StateFlow<Response<User>?> = _userState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _userState.value = Response.Loading

            val currentUid = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUid != null) {
                try {
                    val profile = userFirestoreSource.getUserProfile(currentUid)
                    if (profile != null) {
                        _userState.value = Response.Success(profile)
                    } else {
                        _userState.value = Response.Error(
                            "User profile wasn't found",
                            ErrorType.FIRESTORE_ERROR
                        )
                    }
                } catch (e: Exception) {
                    _userState.value = Response.Error(
                        "An unexpected error occurred while loading user data",
                        ErrorType.FIRESTORE_ERROR
                    )
                }
            } else {
                _userState.value = Response.Error(
                    "The session has expired",
                    ErrorType.FIRESTORE_ERROR
                )
            }
        }
    }
}
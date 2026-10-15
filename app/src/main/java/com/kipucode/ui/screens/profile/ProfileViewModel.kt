package com.kipucode.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.activity.CalculateProfileHeatmapUseCase
import com.kipucode.domain.usecase.activity.GetActivityCalendarUseCase
import com.kipucode.domain.usecase.activity.RefreshActivityCalendarUseCase
import com.kipucode.domain.usecase.auth.LogoutUseCase
import com.kipucode.domain.usecase.user.GetUserProfileUseCase
import com.kipucode.domain.usecase.user.RefreshUserProfileUseCase
import com.kipucode.domain.usecase.user.UpdateUserAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    getActivityCalendarUseCase: GetActivityCalendarUseCase,
    private val calculateProfileHeatmapUseCase: CalculateProfileHeatmapUseCase,
    private val updateUserAvatarUseCase: UpdateUserAvatarUseCase,
    private val refreshUserProfileUseCase: RefreshUserProfileUseCase,
    private val refreshActivityCalendarUseCase: RefreshActivityCalendarUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    private val _isUpdatingAvatar = MutableStateFlow(false)
    private val _updateAvatarSuccess = MutableStateFlow(false)

    val uiState: StateFlow<ProfileUiState> = combine(
        getUserProfileUseCase(),
        getActivityCalendarUseCase(),
        _isRefreshing,
        _isUpdatingAvatar,
        _updateAvatarSuccess
    ) { userProfile, activities, isRefreshing, isUpdatingAvatar, updateAvatarSuccess ->
        val heatmap = calculateProfileHeatmapUseCase(activities)
        val heatmapUiData = HeatmapUiData(
            year = heatmap.year,
            totalActiveDays = heatmap.totalActiveDays,
            totalExercises = heatmap.totalExercises,
            totalXp = heatmap.totalXp,
            activityMap = heatmap.activityMap
        )

        ProfileUiState(
            isLoading = userProfile == null,
            isRefreshing = isRefreshing,
            name = userProfile?.name ?: "",
            email = userProfile?.email ?: "",
            avatarId = userProfile?.avatarId ?: "",
            heatmapData = heatmapUiData,
            isUpdatingAvatar = isUpdatingAvatar,
            updateAvatarSuccess = updateAvatarSuccess
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshUserProfileUseCase()
                refreshActivityCalendarUseCase()
            } catch (_: Exception) {}
            _isRefreshing.value = false
        }
    }

    fun updateUserAvatar(avatarId: String) {
        viewModelScope.launch {
            _isUpdatingAvatar.value = true
            updateUserAvatarUseCase(avatarId)
            _isUpdatingAvatar.value = false
            _updateAvatarSuccess.value = true
        }
    }

    fun resetAvatarSuccess() {
        _updateAvatarSuccess.value = false
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }
}

package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ActivityCalendarRepository
import com.kipucode.domain.usecase.GetActivityCalendarUseCase
import com.kipucode.domain.usecase.user.GetUserProfileUseCase
import com.kipucode.domain.usecase.user.RefreshUserProfileUseCase
import com.kipucode.domain.usecase.user.UpdateUserAvatarUseCase
import com.kipucode.ui.screens.profile.HeatmapUiData
import com.kipucode.ui.screens.profile.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    getActivityCalendarUseCase: GetActivityCalendarUseCase,
    private val updateUserAvatarUseCase: UpdateUserAvatarUseCase,
    private val refreshUserProfileUseCase: RefreshUserProfileUseCase,
    private val activityCalendarRepository: ActivityCalendarRepository
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
        val currentYear = LocalDate.now().year
        val heatmapData = HeatmapUiData(
            year = currentYear,
            totalActiveDays = activities.count { it.exercisesCount > 0 },
            totalExercises = activities.sumOf { it.exercisesCount },
            totalXp = activities.sumOf { it.xpEarned },
            activityMap = activities.associateBy { it.date }
        )

        ProfileUiState(
            isLoading = userProfile == null,
            isRefreshing = isRefreshing,
            name = userProfile?.name ?: "",
            email = userProfile?.email ?: "",
            avatarId = userProfile?.avatarId ?: "",
            heatmapData = heatmapData,
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
                activityCalendarRepository.refreshActivityCalendar()
            } catch (_: Exception) {}
            _isRefreshing.value = false
        }
    }

    fun updateUserAvatar(avatarId: String) {
        viewModelScope.launch {
            _isUpdatingAvatar.value = true
            val result = updateUserAvatarUseCase(avatarId)
            _isUpdatingAvatar.value = false
            if (result is Response.Success) {
                _updateAvatarSuccess.value = true
            }
        }
    }

    fun resetAvatarSuccess() {
        _updateAvatarSuccess.value = false
    }
}

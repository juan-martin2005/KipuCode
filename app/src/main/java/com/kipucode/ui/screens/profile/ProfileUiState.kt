package com.kipucode.ui.screens.profile

import com.kipucode.ui.screens.profile.components.DayStreakUI

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val name: String = "",
    val email: String = "",
    val avatarId: String = "",
    val isUpdatingAvatar: Boolean = false,
    val updateAvatarSuccess: Boolean = false,

    val currentStreakDays: Int = 0,
    val weekDays: List<DayStreakUI> = emptyList(),
    val nextStreakText: String = ""
)

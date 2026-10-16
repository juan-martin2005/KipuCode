package com.kipucode.ui.screens.profile

import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.ui.screens.profile.components.DayStreakUI
import java.time.LocalDate

data class HeatmapUiData(
    val year: Int = LocalDate.now().year,
    val totalActiveDays: Int = 0,
    val totalExercises: Int = 0,
    val totalXp: Int = 0,
    val activityMap: Map<String, DailyActivityDomain> = emptyMap()
)

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val name: String = "",
    val email: String = "",
    val avatarId: String = "",
    val heatmapData: HeatmapUiData = HeatmapUiData(),
    val isUpdatingAvatar: Boolean = false,
    val updateAvatarSuccess: Boolean = false,

    val currentStreakDays: Int = 0,
    val weekDays: List<DayStreakUI> = emptyList(),
    val nextStreakText: String = ""
)

package com.kipucode.ui.screens.code

import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.domain.model.PracticeMethod
import java.time.LocalDate

data class HeatmapUiData(
    val year: Int = LocalDate.now().year,
    val totalActiveDays: Int = 0,
    val totalExercises: Int = 0,
    val totalXp: Int = 0,
    val activityMap: Map<String, DailyActivityDomain> = emptyMap()
)

data class CodePracticeAvailability(
    val completeCodeCount: Int = 0,
    val whatsOutputCount: Int = 0
)

data class CodeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val availability: CodePracticeAvailability = CodePracticeAvailability(),
    val selectedMethod: PracticeMethod = PracticeMethod.COMPLETE_CODE,
    val heatmapData: HeatmapUiData = HeatmapUiData()
)

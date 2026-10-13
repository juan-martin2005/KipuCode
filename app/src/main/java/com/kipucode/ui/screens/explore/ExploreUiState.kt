package com.kipucode.ui.screens.explore

import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.usecase.CognitiveMasteryOverview

// ================================================================================================
//  ESTADO DE UI PARA EXPLORE SCREEN
// ================================================================================================
data class ExploreUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val coursesWithLessons: List<CourseWithLessonsDomain> = emptyList(),
    val masteryOverview: CognitiveMasteryOverview = CognitiveMasteryOverview(),
    val completedLessons: List<String> = emptyList()
)

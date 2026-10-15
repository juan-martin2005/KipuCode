package com.kipucode.ui.screens.lesson

import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.PracticeMethod

data class LessonUiState(
    val isLoading: Boolean = true,
    val lesson: LessonDomain? = null,
    val availableMethods: List<PracticeMethod> = emptyList(),
    val selectedMethod: PracticeMethod? = null,
    val errorMessage: String? = null
)

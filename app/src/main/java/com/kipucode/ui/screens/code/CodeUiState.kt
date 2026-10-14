package com.kipucode.ui.screens.code

import com.kipucode.domain.model.PracticeMethod

data class CodePracticeAvailability(
    val completeCodeCount: Int = 0,
    val whatsOutputCount: Int = 0
)

data class CodeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val availability: CodePracticeAvailability = CodePracticeAvailability(),
    val selectedMethod: PracticeMethod = PracticeMethod.COMPLETE_CODE
)

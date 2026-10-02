package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.usecase.GetDueExercisesWithDetailsUseCase
import com.kipucode.domain.usecase.RefreshLearningProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CodeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val dueExercises: List<DueExerciseDomain> = emptyList()
)

@HiltViewModel
class CodeViewModel @Inject constructor(
    getDueExercisesWithDetailsUseCase: GetDueExercisesWithDetailsUseCase,
    private val refreshLearningProgressUseCase: RefreshLearningProgressUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    init {
        syncDataInBackground()
    }

    val uiState: StateFlow<CodeUiState> = combine(
        getDueExercisesWithDetailsUseCase(),
        _isRefreshing
    ) { dueList, isRefreshing ->
        CodeUiState(
            isLoading = false,
            isRefreshing = isRefreshing,
            dueExercises = dueList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CodeUiState(isLoading = true)
    )

    private fun syncDataInBackground() {
        viewModelScope.launch {
            refreshLearningProgressUseCase()
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshLearningProgressUseCase()
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

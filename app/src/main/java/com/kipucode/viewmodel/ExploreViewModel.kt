package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.usecase.GetCognitiveMasteryOverviewUseCase
import com.kipucode.domain.usecase.GetCourseWithLessonsUseCase
import com.kipucode.domain.usecase.RefreshCoursesUseCase
import com.kipucode.domain.usecase.UpdateLastViewedLessonUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import com.kipucode.ui.screens.explore.ExploreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ================================================================================================
//  VIEWMODEL DE EXPLORAR MÓDULOS (1 SCREEN = 1 VIEWMODEL = 1 UISTATE)
// ================================================================================================
@HiltViewModel
class ExploreViewModel @Inject constructor(
    getCourseWithLessonsUseCase: GetCourseWithLessonsUseCase,
    getCognitiveMasteryOverviewUseCase: GetCognitiveMasteryOverviewUseCase,
    getUserProgressUseCase: GetUserProgressUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase,
    private val refreshUserProgressUseCase: RefreshUserProgressUseCase,
    private val updateLastViewedLessonUseCase: UpdateLastViewedLessonUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    // ============================================================================================
    //  COMBINE: Transforma cursos, progreso del usuario y maestría en un solo ExploreUiState
    // ============================================================================================
    val uiState: StateFlow<ExploreUiState> = combine(
        getCourseWithLessonsUseCase(),
        getCognitiveMasteryOverviewUseCase(),
        getUserProgressUseCase(),
        _isRefreshing
    ) { coursesWithLessons, masteryOverview, userProgress, isRefreshing ->
        if (coursesWithLessons.isEmpty()) {
            return@combine ExploreUiState(
                isLoading = true,
                isRefreshing = isRefreshing
            )
        }

        // 1. Determinar el curso activo según la última lección vista o el track activo
        val activeLessonId = userProgress?.lastVisitedLessonId
        val activeCourseWithLessons = coursesWithLessons.find { courseItem ->
            courseItem.lessons.any { it.id == activeLessonId }
        } ?: coursesWithLessons.find { it.course.track == userProgress?.activeTrack }

        val activeTrack = userProgress?.activeTrack?.ifEmpty { null }
            ?: activeCourseWithLessons?.course?.track

        // 2. Filtrar y ordenar los cursos
        val filteredCourses = if (activeTrack != null) {
            coursesWithLessons.filter { it.course.track == activeTrack }
        } else {
            coursesWithLessons
        }.sortedBy { it.course.orderIndex }

        ExploreUiState(
            isLoading = false,
            isRefreshing = isRefreshing,
            coursesWithLessons = filteredCourses,
            masteryOverview = masteryOverview,
            completedLessons = userProgress?.completedLessons ?: emptyList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExploreUiState(isLoading = true)
    )

    init {
        syncDataInBackground()
    }

    private fun syncDataInBackground() {
        viewModelScope.launch {
            refreshCoursesUseCase()
            refreshUserProgressUseCase()
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshCoursesUseCase()
                refreshUserProgressUseCase()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSelectLesson(lessonId: String) {
        viewModelScope.launch {
            updateLastViewedLessonUseCase(lessonId)
        }
    }
}

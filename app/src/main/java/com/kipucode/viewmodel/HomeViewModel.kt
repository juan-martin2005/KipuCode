package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.usecase.GetCourseWithLessonsUseCase
import com.kipucode.domain.usecase.RefreshCoursesUseCase
import com.kipucode.domain.usecase.RefreshLearningProgressUseCase
import com.kipucode.domain.usecase.user.GetUserProfileUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProfileUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import com.kipucode.ui.screens.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    getUserProgressUseCase: GetUserProgressUseCase,
    getCourseWithLessonsUseCase: GetCourseWithLessonsUseCase,
    private val refreshUserProfileUseCase: RefreshUserProfileUseCase,
    private val refreshUserProgressUseCase: RefreshUserProgressUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase,
    private val refreshLearningProgressUseCase: RefreshLearningProgressUseCase
) : ViewModel() {

    // Controla la animación de deslizar para refrescar (Pull-to-refresh)
    private val _isRefreshing = MutableStateFlow(false)

    init {
        syncDataInBackground()
    }

    // ============================================================================================
    //  COMBINE: 4 flujos se convierten en un solo HomeUiState inmutable
    // ============================================================================================
    val uiState: StateFlow<HomeUiState> = combine(
        getUserProfileUseCase(),
        getUserProgressUseCase(),
        getCourseWithLessonsUseCase(),
        _isRefreshing
    ) { userProfile, userProgress, coursesWithLessons, isRefreshing ->

        // Si aún no tenemos cursos cargados, indicamos Loading
        if (coursesWithLessons.isEmpty()) {
            return@combine HomeUiState(isLoading = true, isRefreshing = isRefreshing)
        }

        // 1. Encontrar el curso activo en el que está el alumno (con fallback al primer curso disponible)
        val activeCourseWithLessons = coursesWithLessons.find { courseItem ->
            courseItem.lessons.any { it.id == userProgress?.currentLessonId }
        } ?: coursesWithLessons.firstOrNull()

        val courseData = activeCourseWithLessons?.course
        val lessonsData = activeCourseWithLessons?.lessons?.sortedBy { it.orderIndex } ?: emptyList()

        // 2. Orden de la lección actual
        val currentLessonOrderIndex = lessonsData.find {
            it.id == userProgress?.currentLessonId
        }?.orderIndex ?: 0

        // 3. Progreso de lecciones
        val isCompletedCourse = userProgress?.completedCourses?.contains(courseData?.id) ?: false
        val currentLessonProgress = if (isCompletedCourse) {
            lessonsData.size
        } else {
            (currentLessonOrderIndex - 1).coerceAtLeast(0)
        }

        // 4. Calcular la racha activa
        val streak = calculateActiveStreak(
            lastCompletedMillis = userProgress?.completedAt,
            databaseStreak = userProgress?.streakDay ?: 0
        )

        // 5. Retornar los datos a la UI
        HomeUiState(
            isLoading = false,
            isRefreshing = isRefreshing,

            userName = userProfile?.name ?: "Usuario",
            totalXp = userProgress?.totalXp ?: 0,
            streakDay = streak,

            courseTitle = courseData?.title ?: "Curso",
            courseNumber = courseData?.orderIndex ?: 1,
            currentLessonsProgress = currentLessonProgress,
            totalLessons = lessonsData.size,

            lessons = lessonsData,
            completedLessonIds = userProgress?.completedLessons ?: emptyList(),
            currentLessonOrderIndex = currentLessonOrderIndex
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    // ============================================================================================
    //  SINCRONIZACIÓN Y REFRESH
    // ============================================================================================
    private fun syncDataInBackground() {
        viewModelScope.launch {
            refreshCoursesUseCase()
            refreshUserProfileUseCase()
            refreshUserProgressUseCase()
            refreshLearningProgressUseCase()
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Sincronización secuencial ordenada para evitar condiciones de carrera entre tablas
                refreshCoursesUseCase()
                refreshUserProfileUseCase()
                refreshUserProgressUseCase()
                refreshLearningProgressUseCase()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // ============================================================================================
    //  Cálculo de racha diaria
    // ============================================================================================
    private fun calculateActiveStreak(lastCompletedMillis: Long?, databaseStreak: Int): Int {
        if (lastCompletedMillis == null) return 0

        val lastDate = Instant.ofEpochMilli(lastCompletedMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val todayDate = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)

        return if (daysBetween > 1L) 0 else databaseStreak
    }
}
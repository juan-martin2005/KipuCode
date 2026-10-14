package com.kipucode.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.CognitiveMasteryDomain
import com.kipucode.domain.usecase.course.GetCognitiveMasteryOverviewUseCase
import com.kipucode.domain.usecase.course.GetCourseWithLessonsUseCase
import com.kipucode.domain.usecase.course.RefreshCoursesUseCase
import com.kipucode.domain.usecase.course.UpdateLastViewedLessonUseCase
import com.kipucode.domain.usecase.learning.RefreshLearningProgressUseCase
import com.kipucode.domain.usecase.user.CalculateStreakUseCase
import com.kipucode.domain.usecase.user.GetUserProfileUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProfileUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    getUserProgressUseCase: GetUserProgressUseCase,
    getCourseWithLessonsUseCase: GetCourseWithLessonsUseCase,
    getCognitiveMasteryOverviewUseCase: GetCognitiveMasteryOverviewUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val updateLastViewedLessonUseCase: UpdateLastViewedLessonUseCase,
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
    //  COMBINE: 5 flujos se convierten en un solo HomeUiState inmutable con Dominio FSRS-6
    // ============================================================================================
    val uiState: StateFlow<HomeUiState> = combine(
        getUserProfileUseCase(),
        getUserProgressUseCase(),
        getCourseWithLessonsUseCase(),
        getCognitiveMasteryOverviewUseCase(),
        _isRefreshing
    ) { userProfile, userProgress, coursesWithLessons, masteryOverview, isRefreshing ->

        // Si aún no tenemos cursos cargados, indicamos Loading
        if (coursesWithLessons.isEmpty()) {
            return@combine HomeUiState(isLoading = true, isRefreshing = isRefreshing)
        }

        // 1. Encontrar el curso activo en el que está el alumno (con fallback al track activo o primer curso disponible)
        val activeLessonId = userProgress?.lastVisitedLessonId
        val activeCourseWithLessons = coursesWithLessons.find { courseItem ->
            courseItem.lessons.any { it.id == activeLessonId }
        } ?: coursesWithLessons.find { it.course.track == userProgress?.activeTrack }
          ?: coursesWithLessons.firstOrNull()

        val courseData = activeCourseWithLessons?.course
        val lessonsData = activeCourseWithLessons?.lessons?.sortedBy { it.orderIndex } ?: emptyList()

        // 2. Orden de la lección actual
        val currentLessonOrderIndex = lessonsData.find {
            it.id == activeLessonId
        }?.orderIndex ?: 0

        // 3. Progreso de lecciones y dominio cognitivo FSRS
        val isCompletedCourse = userProgress?.completedCourses?.contains(courseData?.id) ?: false
        val currentLessonProgress = if (isCompletedCourse) {
            lessonsData.size
        } else {
            (currentLessonOrderIndex - 1).coerceAtLeast(0)
        }

        val activeCourseId = courseData?.id.orEmpty()
        val courseMastery = masteryOverview.courseMastery[activeCourseId]
            ?: CognitiveMasteryDomain()

        // 4. Calcular la racha activa delegando al caso de uso del dominio
        val streak = calculateStreakUseCase.calculateActiveStreak(
            lastCompletedMillis = userProgress?.completedAt,
            databaseStreak = userProgress?.streakDay ?: 0
        )

        // 5. Retornar los datos a la UI
        HomeUiState(
            isLoading = false,
            isRefreshing = isRefreshing,

            userName = userProfile?.name ?: "Usuario",
            avatarId = userProfile?.avatarId ?: "avatar_000",
            totalXp = userProgress?.totalXp ?: 0,
            streakDay = streak,

            activeCourseId = activeCourseId,
            courseTitle = courseData?.title ?: "Curso",
            courseNumber = courseData?.orderIndex ?: 1,
            currentLessonsProgress = currentLessonProgress,
            totalLessons = lessonsData.size,
            courseMastery = courseMastery,

            lessons = lessonsData,
            lessonMasteryMap = masteryOverview.lessonMastery,
            completedLessonIds = userProgress?.completedLessons ?: emptyList(),
            currentLessonOrderIndex = currentLessonOrderIndex,
            currentLessonId = activeLessonId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSelectLesson(lessonId: String) {
        viewModelScope.launch {
            updateLastViewedLessonUseCase(lessonId)
        }
    }

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
}

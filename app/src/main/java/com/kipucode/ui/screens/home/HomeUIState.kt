package com.kipucode.ui.screens.home

import com.kipucode.domain.model.CognitiveMasteryDomain
import com.kipucode.domain.model.LessonDomain

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,

    // Datos del Estudiante
    val userName: String = "",
    val avatarId : String = "",
    val totalXp: Int = 0,
    val streakDay: Int = 0,

    // Datos del Curso Activo
    val activeCourseId: String = "",
    val courseTitle: String = "",
    val courseNumber: Int = 1,
    val currentLessonsProgress: Int = 0,
    val totalLessons: Int = 0,
    val courseMastery: CognitiveMasteryDomain = CognitiveMasteryDomain(),

    // Lecciones y Estado de Dominio
    val lessons: List<LessonDomain> = emptyList(),
    val lessonMasteryMap: Map<String, CognitiveMasteryDomain> = emptyMap(),
    val completedLessonIds: List<String> = emptyList(),
    val currentLessonOrderIndex: Int = 0,
    val currentLessonId: String? = null,

    // Repaso Urgente (FSRS-6)
    val hasDueReviews: Boolean = false,
    val isUrgentReviewDismissed: Boolean = false
)
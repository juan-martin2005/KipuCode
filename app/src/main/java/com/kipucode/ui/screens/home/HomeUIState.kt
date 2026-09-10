package com.kipucode.ui.screens.home

import com.kipucode.domain.model.LessonDomain

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,

    // Datos del Estudiante
    val userName: String = "Usuario",
    val totalXp: Int = 0,
    val streakDay: Int = 0,

    // Datos del Curso Activo
    val courseTitle: String = "Curso",
    val courseNumber: Int = 1,
    val currentLessonsProgress: Int = 0,
    val totalLessons: Int = 0,

    // Lecciones y Estado
    val lessons: List<LessonDomain> = emptyList(),
    val completedLessonIds: List<String> = emptyList(),
    val currentLessonOrderIndex: Int = 0,

    // Mensajes o errores
    val errorMessage: String? = null
)
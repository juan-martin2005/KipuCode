package com.kipucode.ui.navigation

import kotlinx.serialization.Serializable

// ============================================================================================
//  RUTAS DE NAVEGACIÓN (Type-Safe Navigation)
// ============================================================================================

// --- Rutas sin argumentos ---
@Serializable
data object SplashRoute

@Serializable
data object OnboardingRoute

@Serializable
data object RegisterRoute

@Serializable
data object LoginRoute

@Serializable
data object ForgotPasswordRoute

@Serializable
data object HomeRoute

@Serializable
data object ExploreRoute

@Serializable
data object CodeRoute

@Serializable
data object ProfileRoute

@Serializable
data object ChangePasswordRoute

// --- Rutas con argumentos ---
@Serializable
data class LessonRoute(
    val lessonId: String? = null
)

@Serializable
data class ExerciseRoute(
    val lessonId: String,
    val type: String? = null,
    val onlyDue: Boolean = false
)

@Serializable
data class SummaryRoute(
    val xp: Int,
    val correct: Int,
    val total: Int,
    val timeSeconds: Long
)

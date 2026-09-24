package com.kipucode.ui.navigation

import kotlinx.serialization.Serializable

// ============================================================================================
//  RUTAS DE NAVEGACIÓN (Type-Safe Navigation)
// ============================================================================================

// --- Rutas sin argumentos ---
@Serializable
object SplashRoute

@Serializable
object OnboardingRoute

@Serializable
object RegisterRoute

@Serializable
object LoginRoute

@Serializable
object ForgotPasswordRoute

@Serializable
object HomeRoute

@Serializable
object ExploreRoute

@Serializable
object CodeRoute

@Serializable
object ProfileRoute

// --- Rutas con argumentos ---
@Serializable
data class LessonRoute(
    val lessonId: String? = null
)

@Serializable
data class ExerciseRoute(
    val lessonId: String
)

@Serializable
data class SummaryRoute(
    val xp: Int,
    val correct: Int,
    val total: Int,
    val timeSeconds: Long
)

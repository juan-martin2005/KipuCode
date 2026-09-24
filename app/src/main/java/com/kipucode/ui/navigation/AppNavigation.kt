package com.kipucode.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kipucode.ui.screens.auth.ForgotPasswordScreen
import com.kipucode.ui.screens.auth.LoginScreen
import com.kipucode.ui.screens.auth.RegisterScreen
import com.kipucode.ui.screens.code.CodeScreen
import com.kipucode.ui.screens.explore.ExploreScreen
import com.kipucode.ui.screens.exercise.ExerciseScreen
import com.kipucode.ui.screens.home.HomeScreen
import com.kipucode.ui.screens.lesson.LessonScreen
import com.kipucode.ui.screens.onboarding.OnboardingScreen
import com.kipucode.ui.screens.profile.ProfileScreen
import com.kipucode.ui.screens.splash.SplashScreen
import com.kipucode.ui.screens.summary.SummaryScreen
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<SplashRoute> {
            val authViewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                authViewModel = authViewModel,
                onSplashFinished = { isUserLogged ->
                    if (isUserLogged) {
                        userViewModel.startObservingUser()
                        userViewModel.startObservingUserProgress()

                        navController.navigate(HomeRoute) {
                            popUpTo<SplashRoute> { inclusive = true }
                        }
                    } else {
                        navController.navigate(OnboardingRoute) {
                            popUpTo<SplashRoute> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<OnboardingRoute> {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate(LoginRoute) },
                onNavigateToRegister = { navController.navigate(RegisterRoute) }
            )
        }

        composable<RegisterRoute> {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo<RegisterRoute> { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo<RegisterRoute> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable<LoginRoute> {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = {
                    userViewModel.startObservingUser()
                    userViewModel.startObservingUserProgress()

                    navController.navigate(HomeRoute) { popUpTo(0) }
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ForgotPasswordRoute)
                },
                onBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable<ForgotPasswordRoute> {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                navController = navController,
                onNavigateToCode = { lessonId ->
                    navController.navigate(LessonRoute(lessonId = lessonId))
                }
            )
        }

        composable<ExploreRoute> {
            ExploreScreen(
                userViewModel = userViewModel,
                navController = navController,
                onNavigateToCode = { lessonId ->
                    navController.navigate(LessonRoute(lessonId = lessonId))
                }
            )
        }

        composable<CodeRoute> {
            CodeScreen(
                navController = navController
            )
        }

        composable<ProfileRoute> {
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                userViewModel = userViewModel,
                navController = navController,
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(OnboardingRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<LessonRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LessonRoute>()
            LessonScreen(
                lessonId = route.lessonId,
                onBack = {
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToExercises = { id ->
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(ExerciseRoute(lessonId = id))
                    }
                }
            )
        }

        composable<ExerciseRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ExerciseRoute>()
            ExerciseScreen(
                lessonId = route.lessonId,
                type = route.type,
                onBack = {
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onFinished = { session ->
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(
                            SummaryRoute(
                                xp = session.xpEarned,
                                correct = session.correctCount,
                                total = session.totalCount,
                                timeSeconds = session.timeSeconds
                            )
                        ) {
                            popUpTo<ExerciseRoute> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<SummaryRoute> { backStackEntry ->
            SummaryScreen(
                onContinue = {
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(HomeRoute) {
                            popUpTo<HomeRoute> { inclusive = false }
                        }
                    }
                }
            )
        }
    }
}
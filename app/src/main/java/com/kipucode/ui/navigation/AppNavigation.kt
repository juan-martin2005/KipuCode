package com.kipucode.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
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
import com.kipucode.ui.screens.profile.ChangePasswordScreen
import com.kipucode.ui.screens.profile.ProfileScreen
import com.kipucode.ui.screens.splash.SplashScreen
import com.kipucode.ui.screens.summary.SummaryScreen
import com.kipucode.ui.screens.editor.CodeEditorScreen
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) }
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

        composable<HomeRoute>(
            exitTransition = {
                if (targetState.destination.hasRoute(LessonRoute::class)) {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(300))
                } else {
                    fadeOut(animationSpec = tween(200))
                }
            },
            popEnterTransition = {
                if (initialState.destination.hasRoute(LessonRoute::class)) {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(250))
                } else {
                    fadeIn(animationSpec = tween(200))
                }
            }
        ) {
            HomeScreen(
                navController = navController,
                onNavigateToCode = { lessonId ->
                    navController.navigate(LessonRoute(lessonId = lessonId))
                }
            )
        }

        composable<ExploreRoute>(
            exitTransition = {
                if (targetState.destination.hasRoute(LessonRoute::class)) {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(300))
                } else {
                    fadeOut(animationSpec = tween(200))
                }
            },
            popEnterTransition = {
                if (initialState.destination.hasRoute(LessonRoute::class)) {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(250))
                } else {
                    fadeIn(animationSpec = tween(200))
                }
            }
        ) {
            ExploreScreen(
                navController = navController
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

        composable<ChangePasswordRoute> {
            ChangePasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<LessonRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(250, easing = FastOutLinearInEasing)
                )
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<LessonRoute>()
            LessonScreen(
                lessonId = route.lessonId,
                onBack = {
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToExercises = { id, type ->
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(ExerciseRoute(lessonId = id, type = type))
                    }
                }
            )
        }

        composable<ExerciseRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250, easing = FastOutLinearInEasing)
                )
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<ExerciseRoute>()
            ExerciseScreen(
                lessonId = route.lessonId,
                type = route.type,
                onlyDue = route.onlyDue,
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

        composable<CodeEditorRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CodeEditorRoute>()
            CodeEditorScreen(
                languageKey = route.languageKey,
                onBack = {
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
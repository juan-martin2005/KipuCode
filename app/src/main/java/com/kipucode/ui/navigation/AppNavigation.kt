package com.kipucode.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.kipucode.ui.screens.auth.ForgotPasswordScreen
import com.kipucode.ui.screens.auth.LoginScreen
import com.kipucode.ui.screens.auth.RegisterScreen
import com.kipucode.ui.screens.code.CodeScreen
import com.kipucode.ui.screens.explore.ExploreScreen
import com.kipucode.ui.screens.home.HomeScreen
import com.kipucode.ui.screens.lesson.ExerciseScreen
import com.kipucode.ui.screens.lesson.LessonScreen
import com.kipucode.ui.screens.onboarding.OnboardingScreen
import com.kipucode.ui.screens.profile.ProfileScreen
import com.kipucode.ui.screens.splash.SplashScreen
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("splash") {
            val authViewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                authViewModel = authViewModel,
                onSplashFinished = { isUserLogged ->
                    if (isUserLogged) {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register"){
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = {
                    userViewModel.startObservingUser()
                    userViewModel.startObservingUserProgress()
                    navController.navigate("home") { popUpTo(0) }
                },
                onNavigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                },
                onBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }


        composable("home"){
            HomeScreen(
                userViewModel = userViewModel,
                navController = navController,
                onNavigateToCode = { lessonId ->
                    navController.navigate("lesson?lessonId=${lessonId}")
                }
            )
        }

        composable("explore"){
            ExploreScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable("code"){
            CodeScreen(
                navController = navController
            )
        }

        composable("profile"){
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                userViewModel = userViewModel,
                navController = navController,
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable(
            route = "lesson?lessonId={lessonId}",
            arguments = listOf(navArgument("lessonId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")
            LessonScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() },
                onNavigateToExercises = {lessonId ->
                    navController.navigate("exercise?lessonId=${lessonId}")
                }
            )
        }

        composable(
            route = "exercise?lessonId={lessonId}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            ExerciseScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() },
                onFinished = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
    }
}
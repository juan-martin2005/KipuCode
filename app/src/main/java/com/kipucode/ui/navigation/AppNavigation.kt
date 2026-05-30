package com.kipucode.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.kipucode.ui.component.card.KipuBottomBar
import com.kipucode.ui.screens.auth.ForgotPasswordScreen
import com.kipucode.ui.screens.auth.LoginScreen
import com.kipucode.ui.screens.auth.RegisterScreen
import com.kipucode.ui.screens.explore.ExploreScreen
import com.kipucode.ui.screens.home.HomeScreen
import com.kipucode.ui.screens.onboarding.OnboardingScreen
import com.kipucode.ui.screens.profile.ProfileScreen
import com.kipucode.ui.screens.splash.SplashScreen
import com.kipucode.viewmodel.AuthViewModel
import com.kipucode.viewmodel.CoursesViewModel
import com.kipucode.viewmodel.UserViewModel

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    val userViewModel: UserViewModel = hiltViewModel()

    Scaffold(
        containerColor = Color(0xFF292929),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute in listOf("home", "explore", "code", "profile")) {
                KipuBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues).fillMaxSize()
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
                            popUpTo(0)
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
                        userViewModel.loadUserProfile()
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
                HomeScreen(userViewModel = userViewModel)
            }

            composable("explore"){
                ExploreScreen(
                )
            }

            composable("code"){
                Text(text = "Pantalla de Código - TEST")
            }

            composable("profile"){
                val authViewModel: AuthViewModel = hiltViewModel()

                ProfileScreen(
                    userViewModel = userViewModel,
                    onLogoutClick = {
                        authViewModel.logout()

                        navController.navigate("onboarding") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
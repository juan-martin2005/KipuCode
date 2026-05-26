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
import com.kipucode.ui.components.cards.KipuBottomBar
import com.kipucode.ui.screens.auth.LoginScreen
import com.kipucode.ui.screens.auth.RegisterScreen
import com.kipucode.ui.screens.home.HomeScreen
import com.kipucode.ui.screens.onboarding.OnboardingScreen
import com.kipucode.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

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
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
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
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    onNavigateToLogin = { navController.navigate("login") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("login"){
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo(0)
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("home"){
                HomeScreen()
            }

            composable("explore"){
                Text(text = "Pantalla de Explorar - TEST")
            }

            composable("code"){
                Text(text = "Pantalla de Código - TEST")
            }

            composable("profile"){
                Text(text = "Pantalla de Perfil - TEST")
            }

        }
    }
}
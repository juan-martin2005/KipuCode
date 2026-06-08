package com.kipucode.ui.component.card

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kipucode.ui.theme.Nunito
import com.kipucode.R

sealed class NavBarItem(val route: String, val title: String, val icon: Int) {
    object Home : NavBarItem("home", "Home", R.drawable.ic_home)
    object Explore : NavBarItem("explore", "Explore", R.drawable.ic_explore)
    object Code : NavBarItem("code", "Code", R.drawable.ic_code)
    object Profile : NavBarItem("profile", "Profile", R.drawable.ic_user)
}

@Composable
fun KipuBottomBar(navController: NavController) {
    val items = listOf(
        NavBarItem.Home,
        NavBarItem.Explore,
        NavBarItem.Code,
        NavBarItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF0293a8)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0293a8),
                    selectedTextColor = Color(0xFF0293a8),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Vista del Navbar")
@Composable
fun NavbarPreview() {
    val navController = rememberNavController()
    KipuBottomBar(navController = navController)
}
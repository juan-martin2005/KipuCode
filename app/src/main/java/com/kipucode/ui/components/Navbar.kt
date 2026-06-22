package com.kipucode.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kipucode.ui.theme.Nunito
import com.kipucode.R
import com.kipucode.ui.theme.KipuDarkBlue

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
        NavBarItem.Profile,
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
                selected = currentRoute?.substringBefore('?') == item.route,

                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home")
                            launchSingleTop = true
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

@Composable
fun KipuTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Botón de Volver ---
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Volver",
                    tint = KipuDarkBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // --- Textos e Información ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    color = KipuDarkBlue,
                    lineHeight = 24.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Vista del TopBar")
@Composable
fun TopBarPreview() {
    KipuTopBar(
        title = "Pensamiento Computacional y Entornos Base",
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Vista del Navbar")
@Composable
fun NavbarPreview() {
    val navController = rememberNavController()
    KipuBottomBar(navController = navController)
}


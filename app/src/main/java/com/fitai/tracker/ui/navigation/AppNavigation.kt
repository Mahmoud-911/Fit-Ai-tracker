package com.fitai.tracker.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.fitai.tracker.ui.screens.*
import com.fitai.tracker.ui.theme.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Scan : Screen("scan")
    object Tracking : Screen("tracking")
    object Goals : Screen("goals")
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val selectedIcon: ImageVector = icon
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home"),
    BottomNavItem(Screen.Scan.route, Icons.Default.CameraAlt, "Scan"),
    BottomNavItem(Screen.Tracking.route, Icons.Default.BarChart, "Diary"),
    BottomNavItem(Screen.Goals.route, Icons.Default.EmojiEvents, "Goals")
)

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
        },
        exitTransition = {
            fadeOut(tween(200))
        },
        popEnterTransition = {
            fadeIn(tween(300))
        },
        popExitTransition = {
            fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 4 }
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(onScanClick = { navController.navigate(Screen.Scan.route) })
        }

        composable(Screen.Scan.route) {
            ScanScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Tracking.route) {
            TrackingScreen()
        }

        composable(Screen.Goals.route) {
            GoalsScreen()
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute == Screen.Splash.route) return

    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GojoViolet,
                    selectedTextColor = GojoViolet,
                    indicatorColor = GojoViolet.copy(alpha = 0.15f),
                    unselectedIconColor = TextTertiary,
                    unselectedTextColor = TextTertiary
                )
            )
        }
    }
}

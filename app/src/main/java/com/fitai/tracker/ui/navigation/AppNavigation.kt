package com.fitai.tracker.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fitai.tracker.ui.screens.GoalsScreen
import com.fitai.tracker.ui.screens.HomeScreen
import com.fitai.tracker.ui.screens.ScanScreen
import com.fitai.tracker.ui.screens.SplashScreen
import com.fitai.tracker.ui.screens.TrackingScreen
import com.fitai.tracker.ui.theme.DarkCardElevated
import com.fitai.tracker.ui.theme.IosBlue
import com.fitai.tracker.ui.theme.IosSeparator
import com.fitai.tracker.ui.theme.TextPrimary
import com.fitai.tracker.ui.theme.TextTertiary

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
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home"),
    BottomNavItem(Screen.Scan.route, Icons.Default.CameraAlt, "Scan"),
    BottomNavItem(Screen.Tracking.route, Icons.Default.BarChart, "Diary"),
    BottomNavItem(Screen.Goals.route, Icons.Default.EmojiEvents, "Goals")
)

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 6 }
        },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(280)) },
        popExitTransition = {
            fadeOut(tween(180)) + slideOutHorizontally(tween(280)) { it / 6 }
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
        composable(Screen.Tracking.route) { TrackingScreen() }
        composable(Screen.Goals.route) { GoalsScreen() }
    }
}

/**
 * Floating pill tab bar — Dribbble style.
 * Sits 16dp above the bottom edge, rounded 28dp, dark card with a hairline
 * border + a soft shadow. Active tab gets an iOS-blue pill background and
 * shows its label inline next to the icon; inactive tabs show only the icon.
 */
@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute == Screen.Splash.route) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 18.dp, shape = RoundedCornerShape(32.dp), clip = false),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
            border = BorderStroke(0.5.dp, IosSeparator),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    PillTabItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = if (isSelected) Modifier.weight(1.4f) else Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PillTabItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillColor by animateColorAsState(
        targetValue = if (isSelected) IosBlue else Color.Transparent,
        animationSpec = tween(220),
        label = "pillColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextTertiary,
        animationSpec = tween(220),
        label = "iconColor"
    )
    val pillHeight by animateDpAsState(
        targetValue = if (isSelected) 48.dp else 44.dp,
        animationSpec = tween(220),
        label = "pillHeight"
    )

    Box(
        modifier = modifier
            .height(pillHeight)
            .clip(RoundedCornerShape(28.dp))
            .background(pillColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            if (isSelected) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.label,
                    color = iconColor,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = iconColor
                    )
                )
            }
        }

        // unselected items: small label below icon for accessibility
        if (!isSelected) {
            // optional: hidden label, only icon visible — keep clean Dribbble look
        }
    }
}

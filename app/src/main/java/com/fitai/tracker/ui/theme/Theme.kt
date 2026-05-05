package com.fitai.tracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JJKDarkColorScheme = darkColorScheme(
    primary = GojoViolet,
    onPrimary = TextPrimary,
    primaryContainer = GojoVioletDark,
    onPrimaryContainer = GojoVioletLight,
    secondary = DomainBlue,
    onSecondary = DarkBackground,
    secondaryContainer = DomainBlueDark,
    onSecondaryContainer = DomainBlue,
    tertiary = YujiPink,
    onTertiary = DarkBackground,
    tertiaryContainer = CursedRedDark,
    onTertiaryContainer = YujiPinkLight,
    error = CursedRed,
    onError = TextPrimary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = DarkCardElevated,
    scrim = DarkBackground,
    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = GojoVioletDark,
    surfaceTint = GojoViolet
)

@Composable
fun FitAiTrackerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = JJKDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}

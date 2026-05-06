package com.fitai.tracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// iOS-style dark color scheme keyed off systemBlue.
private val IosDarkColorScheme = darkColorScheme(
    primary = IosBlue,
    onPrimary = Color.White,
    primaryContainer = IosBlueDark,
    onPrimaryContainer = IosBlueLight,
    secondary = IosCyan,
    onSecondary = DarkBackground,
    secondaryContainer = DomainBlueDark,
    onSecondaryContainer = IosCyan,
    tertiary = IosIndigo,
    onTertiary = Color.White,
    tertiaryContainer = MegumiDark,
    onTertiaryContainer = IosBlueLight,
    error = CursedRed,
    onError = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkCardElevated,
    surfaceContainerHighest = DarkCardElevated,
    outline = IosSeparator,
    outlineVariant = DarkCardElevated,
    scrim = Color.Black,
    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = IosBlueDark,
    surfaceTint = IosBlue
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
        colorScheme = IosDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}

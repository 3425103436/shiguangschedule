package com.xingheyuzhuan.shiguangschedule.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = WakeupBlue,
    onPrimary = Color.White,
    primaryContainer = WakeupBlueContainer.copy(alpha = 0.28f),
    secondary = WakeupPink,
    secondaryContainer = WakeupPinkContainer.copy(alpha = 0.22f),
    tertiary = WakeupGreen,
    tertiaryContainer = WakeupGreenContainer.copy(alpha = 0.2f),
    background = WakeupBackgroundDark,
    surface = WakeupSurfaceDark,
    onBackground = WakeupOnSurfaceDark,
    onSurface = WakeupOnSurfaceDark,
    onSurfaceVariant = WakeupOnSurfaceVariantDark,
)

private val LightColorScheme = lightColorScheme(
    primary = WakeupBlue,
    onPrimary = Color.White,
    primaryContainer = WakeupBlueContainer,
    secondary = WakeupPink,
    secondaryContainer = WakeupPinkContainer,
    tertiary = WakeupGreen,
    tertiaryContainer = WakeupGreenContainer,
    background = WakeupBackgroundLight,
    surface = WakeupSurfaceLight,
    onBackground = WakeupOnSurfaceLight,
    onSurface = WakeupOnSurfaceLight,
    onSurfaceVariant = WakeupOnSurfaceVariantLight,
)

@Composable
fun ShiguangScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val backgroundColor = colorScheme.background.toArgb()

            window.setBackgroundDrawable(backgroundColor.toDrawable())

            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()

            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

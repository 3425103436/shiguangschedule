package com.xingheyuzhuan.shiguangschedule.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import com.xingheyuzhuan.shiguangschedule.data.model.AppSettingsModel
import com.xingheyuzhuan.shiguangschedule.data.model.AppThemeMode

/**
 * 定义一个用于全局同步深色模式状态的 Local 变量
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

private val LiquidGlassShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * 把 Material 3 的全部表面角色转换为半透明玻璃层级。
 * 所有 Scaffold、TopAppBar、Card、Dialog、BottomSheet、TextField 和菜单都会
 * 透出应用根层的彩色柔光，而不用每个业务页面重复维护一套颜色。
 */
private fun androidx.compose.material3.ColorScheme.withLiquidGlassSurfaces(
    darkTheme: Boolean
): androidx.compose.material3.ColorScheme {
    val surfaceBase = if (darkTheme) Color(0xFF181C27) else Color.White
    val variantBase = if (darkTheme) Color(0xFF252A38) else Color(0xFFF8F9FD)

    return copy(
        background = Color.Transparent,
        surface = surfaceBase.copy(alpha = if (darkTheme) 0.70f else 0.68f),
        surfaceVariant = variantBase.copy(alpha = if (darkTheme) 0.62f else 0.48f),
        surfaceDim = surfaceBase.copy(alpha = if (darkTheme) 0.52f else 0.40f),
        surfaceBright = surfaceBase.copy(alpha = if (darkTheme) 0.82f else 0.82f),
        surfaceContainerLowest = surfaceBase.copy(alpha = if (darkTheme) 0.34f else 0.24f),
        surfaceContainerLow = surfaceBase.copy(alpha = if (darkTheme) 0.46f else 0.36f),
        surfaceContainer = surfaceBase.copy(alpha = if (darkTheme) 0.56f else 0.48f),
        surfaceContainerHigh = surfaceBase.copy(alpha = if (darkTheme) 0.68f else 0.60f),
        surfaceContainerHighest = surfaceBase.copy(alpha = if (darkTheme) 0.78f else 0.72f),
        primaryContainer = primaryContainer.copy(alpha = if (darkTheme) 0.72f else 0.54f),
        outline = (if (darkTheme) WakeUpDarkOutline else WakeUpLightOutline).copy(alpha = 0.74f),
        outlineVariant = Color.White.copy(alpha = if (darkTheme) 0.12f else 0.50f),
        secondaryContainer = (if (darkTheme) WakeUpDarkSecondaryContainer else WakeUpLightSecondaryContainer)
            .copy(alpha = if (darkTheme) 0.72f else 0.58f),
        tertiaryContainer = tertiaryContainer.copy(alpha = if (darkTheme) 0.72f else 0.54f),
        errorContainer = errorContainer.copy(alpha = if (darkTheme) 0.78f else 0.64f)
    )
}

/**
 * 外部调用的快捷主题函数
 * 自动根据 AppSettingsModel 处理所有主题逻辑
 */
@Composable
fun ShiguangScheduleTheme(
    settings: AppSettingsModel,
    content: @Composable () -> Unit
) {
    val darkTheme = when (settings.themeMode) {
        AppThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        ShiguangScheduleTheme(
            darkTheme = darkTheme,
            dynamicColor = settings.useDynamicColor,
            customLightPrimary = Color(settings.customLightPrimary),
            customDarkPrimary = Color(settings.customDarkPrimary),
            content = content
        )
    }
}

/**
 * 核心主题实现函数。
 *
 * 保留动态颜色开关，同时固定背景、表面和文字层级，让课表主体保持
 * iOS Liquid Glass 风格的柔和层次；用户仍然可以通过系统动态颜色改变强调色。
 */
@Composable
fun ShiguangScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    customLightPrimary: Color = Purple40,
    customDarkPrimary: Color = Purple80,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val systemScheme = if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }

            systemScheme.copy(
                background = if (darkTheme) WakeUpDarkBackground else WakeUpLightBackground,
                onBackground = if (darkTheme) WakeUpDarkOnBackground else WakeUpLightOnBackground,
                surface = if (darkTheme) WakeUpDarkSurface else WakeUpLightSurface,
                onSurface = if (darkTheme) WakeUpDarkOnSurface else WakeUpLightOnSurface,
                surfaceVariant = if (darkTheme) WakeUpDarkSurfaceVariant else WakeUpLightSurfaceVariant,
                onSurfaceVariant = if (darkTheme) WakeUpDarkOnSurfaceVariant else WakeUpLightOnSurfaceVariant,
                outline = if (darkTheme) WakeUpDarkOutline else WakeUpLightOutline
            ).withLiquidGlassSurfaces(darkTheme)
        }

        darkTheme -> {
            darkColorScheme(
                primary = customDarkPrimary,
                onPrimary = WakeUpDarkBackground,
                background = WakeUpDarkBackground,
                onBackground = WakeUpDarkOnBackground,
                surface = WakeUpDarkSurface,
                onSurface = WakeUpDarkOnSurface,
                surfaceVariant = WakeUpDarkSurfaceVariant,
                onSurfaceVariant = WakeUpDarkOnSurfaceVariant,
                secondaryContainer = WakeUpDarkSecondaryContainer,
                onSecondaryContainer = WakeUpDarkOnSecondaryContainer,
                outline = WakeUpDarkOutline
            ).withLiquidGlassSurfaces(darkTheme = true)
        }

        else -> {
            lightColorScheme(
                primary = customLightPrimary,
                onPrimary = Color.White,
                background = WakeUpLightBackground,
                onBackground = WakeUpLightOnBackground,
                surface = WakeUpLightSurface,
                onSurface = WakeUpLightOnSurface,
                surfaceVariant = WakeUpLightSurfaceVariant,
                onSurfaceVariant = WakeUpLightOnSurfaceVariant,
                secondaryContainer = WakeUpLightSecondaryContainer,
                onSecondaryContainer = WakeUpLightOnSecondaryContainer,
                outline = WakeUpLightOutline
            ).withLiquidGlassSurfaces(darkTheme = false)
        }
    }

    // 处理系统状态栏和导航栏外观
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            val backgroundColor = (
                if (darkTheme) WakeUpDarkBackground else WakeUpLightBackground
            ).toArgb()
            window.setBackgroundDrawable(backgroundColor.toDrawable())

            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = LiquidGlassShapes,
        content = content
    )
}

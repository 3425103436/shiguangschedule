package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalIsDarkTheme

/**
 * iOS Liquid Glass 风格的通用表面。
 *
 * Android Compose 没有跨组件的 backdrop-filter；这里通过极浅的半透明渐变、
 * 高亮边缘和下层彩色光斑组合出稳定的玻璃折射效果，并兼容低版本设备。
 */
@Composable
fun Modifier.liquidGlass(
    tint: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(20.dp),
    emphasized: Boolean = false,
    shadowElevation: androidx.compose.ui.unit.Dp = if (emphasized) 8.dp else 3.dp
): Modifier {
    val isDark = LocalIsDarkTheme.current
    val topColor = if (isDark) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.76f)
    val middleColor = if (isDark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.32f)
    } else {
        Color.White.copy(alpha = 0.42f)
    }
    val tintColor = tint.copy(alpha = (if (isDark) 0.15f else 0.075f) * tint.alpha)
    val edgeColor = Color.White.copy(alpha = if (isDark) 0.22f else 0.78f)

    return this
        .shadow(elevation = shadowElevation, shape = shape, clip = false)
        .clip(shape)
        .background(
            brush = Brush.linearGradient(colors = listOf(topColor, middleColor, tintColor))
        )
        .border(width = 1.dp, color = edgeColor, shape = shape)
        .drawWithContent {
            drawContent()
            drawLine(
                color = Color.White.copy(alpha = if (isDark) 0.20f else 0.82f),
                start = Offset(size.width * 0.12f, 1.dp.toPx()),
                end = Offset(size.width * 0.88f, 1.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
        }
}

/** 浅色 iOS 玻璃所需的低饱和彩色光斑背景。 */
@Composable
fun LiquidGlassBackdrop(modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val baseColor = if (isDark) Color(0xFF10131C) else Color(0xFFF4F6FA)

    Canvas(modifier = modifier) {
        drawRect(baseColor)

        val minSize = size.minDimension
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    (if (isDark) Color(0xFF386A91) else Color(0xFFBFE3FA)).copy(alpha = if (isDark) 0.28f else 0.38f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.04f, size.height * 0.20f),
                radius = minSize * 0.70f
            ),
            radius = minSize * 0.70f,
            center = Offset(size.width * 0.04f, size.height * 0.20f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    (if (isDark) Color(0xFF694A83) else Color(0xFFEAD8F8)).copy(alpha = if (isDark) 0.25f else 0.34f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.96f, size.height * 0.42f),
                radius = minSize * 0.72f
            ),
            radius = minSize * 0.72f,
            center = Offset(size.width * 0.96f, size.height * 0.42f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    (if (isDark) Color(0xFF276457) else Color(0xFFD1F0E8)).copy(alpha = if (isDark) 0.22f else 0.30f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.38f, size.height * 0.92f),
                radius = minSize * 0.74f
            ),
            radius = minSize * 0.74f,
            center = Offset(size.width * 0.38f, size.height * 0.92f)
        )
    }
}

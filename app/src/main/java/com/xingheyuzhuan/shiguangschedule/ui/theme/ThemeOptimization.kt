package com.xingheyuzhuan.shiguangschedule.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * UI 主题优化
 * 提升整体视觉质感和丝滑度
 */

/**
 * 优化的形状系统
 * 使用更现代的圆角设计
 */
object OptimizedShapes {
    val shapes = Shapes(
        // 小圆角：用于小型组件（按钮、输入框等）
        small = RoundedCornerShape(12.dp),
        
        // 中等圆角：用于中型组件（卡片、对话框等）
        medium = RoundedCornerShape(16.dp),
        
        // 大圆角：用于大型组件（容器、底部表单等）
        large = RoundedCornerShape(20.dp)
    )
}

/**
 * 优化的间距系统
 * 提供一致的间距规范
 */
object OptimizedSpacing {
    val xs = 2.dp      // 极小间距
    val sm = 4.dp      // 小间距
    val md = 8.dp      // 中等间距
    val lg = 12.dp     // 大间距
    val xl = 16.dp     // 超大间距
    val xxl = 24.dp    // 极大间距
}

/**
 * 优化的阴影系统
 * 提供多层次的阴影效果
 */
object OptimizedElevations {
    val none = 0.dp        // 无阴影
    val xs = 1.dp          // 极小阴影
    val sm = 2.dp          // 小阴影
    val md = 4.dp          // 中等阴影
    val lg = 6.dp          // 大阴影
    val xl = 8.dp          // 超大阴影
    val xxl = 12.dp        // 极大阴影
}

/**
 * 优化的透明度系统
 * 提供一致的透明度规范
 */
object OptimizedAlpha {
    val disabled = 0.38f       // 禁用状态
    val hover = 0.08f         // 悬停状态
    val focus = 0.12f         // 焦点状态
    val pressed = 0.16f       // 按下状态
    val surface = 0.92f       // 表面颜色
    val surfaceVariant = 0.88f // 表面变体
}

/**
 * 优化的动画时长系统
 * 提供一致的动画时长规范
 */
object OptimizedDurations {
    val instant = 0          // 无动画
    val short = 150          // 短动画（毫秒）
    val medium = 300         // 中等动画
    val long = 500           // 长动画
    val veryLong = 800       // 很长的动画
}

/**
 * 优化的文本样式
 * 提供更现代的排版设计
 */
object OptimizedTypography {
    // 标题样式：更大的字体，更粗的字重
    val displayLarge = androidx.compose.material3.Typography().displayLarge.copy(
        fontSize = androidx.compose.ui.unit.sp(32),
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        lineHeight = androidx.compose.ui.unit.sp(40)
    )
    
    val displayMedium = androidx.compose.material3.Typography().displayMedium.copy(
        fontSize = androidx.compose.ui.unit.sp(28),
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        lineHeight = androidx.compose.ui.unit.sp(36)
    )
    
    val displaySmall = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(24),
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        lineHeight = androidx.compose.ui.unit.sp(32)
    )
    
    // 标题样式：中等大小
    val headlineLarge = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(20),
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        lineHeight = androidx.compose.ui.unit.sp(28)
    )
    
    val headlineMedium = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(18),
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        lineHeight = androidx.compose.ui.unit.sp(26)
    )
    
    val headlineSmall = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(16),
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        lineHeight = androidx.compose.ui.unit.sp(24)
    )
    
    // 正文样式：标准大小
    val bodyLarge = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(16),
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        lineHeight = androidx.compose.ui.unit.sp(24)
    )
    
    val bodyMedium = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(14),
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        lineHeight = androidx.compose.ui.unit.sp(20)
    )
    
    val bodySmall = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(12),
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        lineHeight = androidx.compose.ui.unit.sp(16)
    )
    
    // 标签样式：小号文本
    val labelLarge = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(14),
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        lineHeight = androidx.compose.ui.unit.sp(20)
    )
    
    val labelMedium = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(12),
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        lineHeight = androidx.compose.ui.unit.sp(16)
    )
    
    val labelSmall = androidx.compose.ui.text.TextStyle(
        fontSize = androidx.compose.ui.unit.sp(11),
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        lineHeight = androidx.compose.ui.unit.sp(16)
    )
}

/**
 * 优化的颜色系统
 * 提供更精细的颜色控制
 */
object OptimizedColors {
    // 中性颜色
    val white = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
    val black = androidx.compose.ui.graphics.Color(0xFF000000)
    
    // 灰色系
    val gray50 = androidx.compose.ui.graphics.Color(0xFFFAFAFA)
    val gray100 = androidx.compose.ui.graphics.Color(0xFFF5F5F5)
    val gray200 = androidx.compose.ui.graphics.Color(0xFFEEEEEE)
    val gray300 = androidx.compose.ui.graphics.Color(0xFFE0E0E0)
    val gray400 = androidx.compose.ui.graphics.Color(0xFFBDBDBD)
    val gray500 = androidx.compose.ui.graphics.Color(0xFF9E9E9E)
    val gray600 = androidx.compose.ui.graphics.Color(0xFF757575)
    val gray700 = androidx.compose.ui.graphics.Color(0xFF616161)
    val gray800 = androidx.compose.ui.graphics.Color(0xFF424242)
    val gray900 = androidx.compose.ui.graphics.Color(0xFF212121)
    
    // 语义颜色
    val success = androidx.compose.ui.graphics.Color(0xFF4CAF50)
    val warning = androidx.compose.ui.graphics.Color(0xFFFFC107)
    val error = androidx.compose.ui.graphics.Color(0xFFF44336)
    val info = androidx.compose.ui.graphics.Color(0xFF2196F3)
}

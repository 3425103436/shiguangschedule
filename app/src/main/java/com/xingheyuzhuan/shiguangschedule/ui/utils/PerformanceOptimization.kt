package com.xingheyuzhuan.shiguangschedule.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * 性能优化工具类
 * 提供列表渲染优化、重组缓存、动画优化等功能
 */

/**
 * Spring 动画预设：Q 弹效果
 * 用于替代 Tween 动画，实现更自然的物理特性
 */
object SpringAnimationPresets {
    // 标准弹簧：适合大多数 UI 动画
    val standard = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    // 轻弹簧：适合轻量级动画（如按钮反馈）
    val light = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessVeryLow
    )

    // 重弹簧：适合页面转换
    val heavy = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // 快速弹簧：适合快速反馈
    val quick = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
}

/**
 * 优化的列表项 Key 生成器
 * 确保 LazyColumn/LazyRow 中的项目正确缓存
 */
fun <T> generateListItemKey(item: T, index: Int, keyExtractor: (T) -> String): String {
    return "${keyExtractor(item)}_$index"
}

/**
 * 优化的修饰符：减少阴影计算
 * 通过缓存阴影参数，避免每次重组都重新计算
 */
fun Modifier.optimizedShadow(
    elevation: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.dp(4f),
    shape: androidx.compose.foundation.shape.Shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    ambientColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.08f),
    spotColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.04f)
): Modifier = composed {
    val cachedShape = remember { shape }
    val cachedAmbientColor = remember { ambientColor }
    val cachedSpotColor = remember { spotColor }
    
    this.then(
        Modifier.shadow(
            elevation = elevation,
            shape = cachedShape,
            ambientColor = cachedAmbientColor,
            spotColor = cachedSpotColor
        )
    )
}

/**
 * 优化的图形层修饰符：用于性能敏感的动画
 * 使用 graphicsLayer 而非频繁重组
 */
fun Modifier.optimizedGraphicsLayer(
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    translationX: Float = 0f,
    translationY: Float = 0f,
    alpha: Float = 1f,
    rotationZ: Float = 0f
): Modifier = composed {
    this.then(
        Modifier.graphicsLayer(
            scaleX = scaleX,
            scaleY = scaleY,
            translationX = translationX,
            translationY = translationY,
            alpha = alpha,
            rotationZ = rotationZ
        )
    )
}

/**
 * 防止过度重组的 Composable 包装器
 * 用于包装性能敏感的子树
 */
@Composable
fun <T> MemoizedComposable(
    input: T,
    content: @Composable (T) -> Unit
) {
    val memoized = remember(input) { input }
    content(memoized)
}

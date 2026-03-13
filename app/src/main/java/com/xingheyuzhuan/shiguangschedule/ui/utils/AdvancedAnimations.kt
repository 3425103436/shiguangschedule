package com.xingheyuzhuan.shiguangschedule.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.flow.collectLatest

/**
 * 高级动效工具类
 * 提供丝滑的交互动画和过渡效果
 */

/**
 * 按钮按压动效
 * 实现类似 iOS 的按钮反馈
 */
fun Modifier.pressEffect(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    scaleDown: Float = 0.95f
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    scale.animateTo(
                        scaleDown,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                }
                is PressInteraction.Release -> {
                    scale.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
                is PressInteraction.Cancel -> {
                    scale.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }
        }
    }
    
    this.then(
        Modifier.graphicsLayer(
            scaleX = scale.value,
            scaleY = scale.value
        )
    )
}

/**
 * 悬停动效
 * 实现类似 Web 的悬停效果
 */
fun Modifier.hoverEffect(
    scale: Float = 1.02f,
    elevation: Float = 4f
): Modifier = composed {
    val scaleAnim = remember { Animatable(1f) }
    val elevationAnim = remember { Animatable(0f) }
    
    this.then(
        Modifier.graphicsLayer(
            scaleX = scaleAnim.value,
            scaleY = scaleAnim.value,
            translationY = -elevationAnim.value
        )
    )
}

/**
 * 淡入淡出动效
 * 实现平滑的透明度过渡
 */
@Composable
fun fadeInOut(
    visible: Boolean,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
): Float {
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    
    LaunchedEffect(visible) {
        alpha.animateTo(if (visible) 1f else 0f, animationSpec = animationSpec)
    }
    
    return alpha.value
}

/**
 * 滑动动效
 * 实现平滑的位移过渡
 */
@Composable
fun slideInOut(
    visible: Boolean,
    direction: SlideDirection = SlideDirection.UP,
    distance: Float = 100f,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
): Float {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(visible) {
        offset.animateTo(if (visible) 0f else distance, animationSpec = animationSpec)
    }
    
    return offset.value
}

/**
 * 滑动方向
 */
enum class SlideDirection {
    UP, DOWN, LEFT, RIGHT
}

/**
 * 缩放动效
 * 实现平滑的缩放过渡
 */
@Composable
fun scaleInOut(
    visible: Boolean,
    startScale: Float = 0.8f,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
): Float {
    val scale = remember { Animatable(if (visible) 1f else startScale) }
    
    LaunchedEffect(visible) {
        scale.animateTo(if (visible) 1f else startScale, animationSpec = animationSpec)
    }
    
    return scale.value
}

/**
 * 旋转动效
 * 实现平滑的旋转过渡
 */
@Composable
fun rotateInOut(
    visible: Boolean,
    startRotation: Float = -90f,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
): Float {
    val rotation = remember { Animatable(if (visible) 0f else startRotation) }
    
    LaunchedEffect(visible) {
        rotation.animateTo(if (visible) 0f else startRotation, animationSpec = animationSpec)
    }
    
    return rotation.value
}

/**
 * 弹簧动效修饰符
 * 用于任何需要弹簧效果的组件
 */
fun Modifier.springEffect(
    scale: Float = 1f,
    alpha: Float = 1f,
    translationX: Float = 0f,
    translationY: Float = 0f,
    rotation: Float = 0f
): Modifier = this.then(
    Modifier.graphicsLayer(
        scaleX = scale,
        scaleY = scale,
        alpha = alpha,
        translationX = translationX,
        translationY = translationY,
        rotationZ = rotation
    )
)

/**
 * 脉冲动效
 * 实现持续的脉冲效果（如加载指示器）
 */
@Composable
fun pulseEffect(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    duration: Int = 1000
): Float {
    val scale = remember { Animatable(minScale) }
    
    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(maxScale, animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            ))
            scale.animateTo(minScale, animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            ))
        }
    }
    
    return scale.value
}

/**
 * 弹跳动效
 * 实现弹跳效果（如列表项进入）
 */
@Composable
fun bounceEffect(
    trigger: Boolean = true,
    bounceHeight: Float = 20f,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
): Float {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            offset.animateTo(-bounceHeight, animationSpec = animationSpec)
            offset.animateTo(0f, animationSpec = animationSpec)
        }
    }
    
    return offset.value
}

/**
 * 摇晃动效
 * 实现摇晃效果（如错误提示）
 */
@Composable
fun shakeEffect(
    trigger: Boolean = false,
    distance: Float = 10f,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
): Float {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            repeat(4) {
                offset.animateTo(if (it % 2 == 0) distance else -distance, animationSpec = animationSpec)
            }
            offset.animateTo(0f, animationSpec = animationSpec)
        }
    }
    
    return offset.value
}

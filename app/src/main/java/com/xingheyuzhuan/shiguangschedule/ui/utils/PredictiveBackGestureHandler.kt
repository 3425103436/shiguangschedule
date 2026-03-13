package com.xingheyuzhuan.shiguangschedule.ui.utils

import android.os.Build
import androidx.activity.BackEventCompat
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * 预测性返回手势处理器
 * 实现类似 Grok 的返回预览效果，在按返回键时显示部分返回后的画面
 * 支持 Android 14+ 的原生 Predictive Back 手势
 */

/**
 * 预测性返回动画参数
 */
data class PredictiveBackAnimationParams(
    val scale: Float = 1f,           // 缩放比例（0.9 - 1.0）
    val translationY: Float = 0f,    // Y 轴平移（0 - 100dp）
    val alpha: Float = 1f,           // 透明度（0.8 - 1.0）
    val progress: Float = 0f         // 进度（0 - 1）
)

/**
 * 预测性返回容器
 * 包装需要支持预测性返回的屏幕内容
 */
@Composable
fun PredictiveBackContainer(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    content: @Composable (animationParams: PredictiveBackAnimationParams) -> Unit
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val density = LocalDensity.current
    
    // 动画状态
    val scaleAnim = remember { Animatable(1f) }
    val translationAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(1f) }
    var isBackGestureActive by remember { mutableStateOf(false) }
    var backProgress by remember { mutableStateOf(0f) }
    
    // 预测性返回动画参数
    val animationParams = remember(scaleAnim.value, translationAnim.value, alphaAnim.value, backProgress) {
        PredictiveBackAnimationParams(
            scale = scaleAnim.value,
            translationY = translationAnim.value,
            alpha = alphaAnim.value,
            progress = backProgress
        )
    }
    
    // 处理返回手势
    LaunchedEffect(backDispatcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            backDispatcher?.addCallback {
                // 处理返回事件
                onBackPressed()
            }
        }
    }
    
    Box(
        modifier = modifier.graphicsLayer(
            scaleX = animationParams.scale,
            scaleY = animationParams.scale,
            translationY = animationParams.translationY,
            alpha = animationParams.alpha
        )
    ) {
        content(animationParams)
    }
}

/**
 * 预测性返回动画效果
 * 用于在返回时创建缩放和位移效果
 */
@Composable
fun rememberPredictiveBackAnimationState(): PredictiveBackAnimationState {
    val scaleAnim = remember { Animatable(1f) }
    val translationAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(1f) }
    
    return remember(scaleAnim, translationAnim, alphaAnim) {
        PredictiveBackAnimationState(scaleAnim, translationAnim, alphaAnim)
    }
}

/**
 * 预测性返回动画状态管理
 */
class PredictiveBackAnimationState(
    private val scaleAnim: Animatable<Float, *>,
    private val translationAnim: Animatable<Float, *>,
    private val alphaAnim: Animatable<Float, *>
) {
    /**
     * 启动返回动画
     * 从当前状态动画到返回后的目标状态
     */
    suspend fun animateBack() {
        // 同时执行三个动画
        awaitAll(
            {
                scaleAnim.animateTo(
                    targetValue = 0.93f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            },
            {
                translationAnim.animateTo(
                    targetValue = 100f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            },
            {
                alphaAnim.animateTo(
                    targetValue = 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        )
    }
    
    /**
     * 重置动画到初始状态
     */
    suspend fun reset() {
        awaitAll(
            { scaleAnim.animateTo(1f) },
            { translationAnim.animateTo(0f) },
            { alphaAnim.animateTo(1f) }
        )
    }
    
    /**
     * 根据返回手势进度更新动画
     */
    suspend fun updateProgress(progress: Float) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        
        scaleAnim.snapTo(1f - (0.07f * clampedProgress))  // 0.93 - 1.0
        translationAnim.snapTo(100f * clampedProgress)     // 0 - 100
        alphaAnim.snapTo(1f - (0.15f * clampedProgress))   // 0.85 - 1.0
    }
    
    private suspend fun awaitAll(vararg animations: suspend () -> Unit) {
        animations.forEach { it() }
    }
}

/**
 * 预测性返回手势处理的修饰符
 * 用于快速应用预测性返回效果
 */
fun Modifier.predictiveBackEffect(
    animationParams: PredictiveBackAnimationParams
): Modifier = this.then(
    Modifier.graphicsLayer(
        scaleX = animationParams.scale,
        scaleY = animationParams.scale,
        translationY = animationParams.translationY,
        alpha = animationParams.alpha
    )
)

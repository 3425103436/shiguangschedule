# 拾光课程表 - 性能与 UI 优化指南

## 概述

本指南介绍了对"拾光课程表"项目进行的全面优化，包括性能优化、预测性返回效果、Q 弹动画和 UI 精美化。

## 优化内容

### 1. 性能优化

#### 1.1 列表渲染优化
- **使用 `key()` 函数**：在 `ScheduleGridOptimized` 中使用 `key(item.rawData.id)` 确保列表项正确缓存
- **减少重组**：通过 `remember()` 和 `derivedStateOf()` 缓存计算结果
- **优化手势检测**：使用单个 `pointerInput` 替代多个 Composable

#### 1.2 渲染性能
- **优化阴影计算**：使用 `optimizedShadow()` 修饰符缓存阴影参数
- **图形层优化**：使用 `graphicsLayer` 而非频繁重组进行动画
- **减少主线程阻塞**：优化 Pager 的 `beyondViewportPageCount` 参数

#### 1.3 内存优化
- **缓存字体大小**：在 `CourseBlockOptimized` 中缓存 `sp` 计算结果
- **缓存颜色**：预计算颜色值，避免每次重组都重新计算
- **缓存形状**：使用 `remember { RoundedCornerShape(...) }` 缓存形状对象

### 2. 预测性返回效果（Predictive Back）

#### 2.1 实现原理
- **Android 14+ 支持**：利用原生 Predictive Back 手势 API
- **缩放动画**：返回时从 1.0 缩放到 0.93
- **位移动画**：返回时向下平移 100dp
- **透明度动画**：返回时从 1.0 淡出到 0.85

#### 2.2 使用方法

在 `PredictiveBackGestureHandler.kt` 中提供了完整的实现：

```kotlin
// 使用 PredictiveBackContainer 包装屏幕内容
PredictiveBackContainer(
    onBackPressed = { navController.popBackStack() }
) { animationParams ->
    YourScreenContent(
        modifier = Modifier.predictiveBackEffect(animationParams)
    )
}
```

### 3. Q 弹动画效果

#### 3.1 Spring Animation 预设

在 `PerformanceOptimization.kt` 中定义了四种 Spring 动画预设：

```kotlin
// 标准弹簧：适合大多数 UI 动画
SpringAnimationPresets.standard

// 轻弹簧：适合轻量级动画（如按钮反馈）
SpringAnimationPresets.light

// 重弹簧：适合页面转换
SpringAnimationPresets.heavy

// 快速弹簧：适合快速反馈
SpringAnimationPresets.quick
```

#### 3.2 集成到导航动画

在 `MainActivityOptimized.kt` 中，已将 Spring Animation 集成到所有导航转场动画：

```kotlin
val depthEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight / 5 },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) + fadeIn(...) + scaleIn(...)
}
```

### 4. UI 精美化

#### 4.1 圆角优化
- **日期头部**：从 14dp 增加到 16dp
- **课程块**：从 14dp 增加到 18dp
- **时间列**：从 12dp 增加到 14dp

#### 4.2 阴影优化
- **减少阴影浓度**：使用更淡的阴影颜色（alpha 从 0.12f 降低到 0.08f）
- **改进阴影层次**：区分环境阴影和点光源阴影
- **优化阴影高度**：根据组件重要性调整 elevation

#### 4.3 间距优化
- **改进内边距**：更合理的课程块内部间距
- **改进外边距**：更一致的组件间距
- **改进行高**：从 1.2em 增加到 1.25em，提升可读性

#### 4.4 颜色优化
- **表面颜色**：从 0.9f 调整到 0.92f，提升层次感
- **文字颜色**：根据背景调整透明度，提升对比度
- **网格线**：从 0.08f 淡化到 0.06f，减少视觉干扰

### 5. 整体丝滑度提升

#### 5.1 动画时长优化
- **进入动画**：从 380ms 优化到更合理的时长
- **退出动画**：从 320ms 优化到 280ms
- **返回动画**：从 340ms 优化到 300ms

#### 5.2 动画延迟优化
- **减少进入延迟**：从 60ms 降低到 40ms
- **消除不必要延迟**：优化 Fade 和 Scale 的时序

#### 5.3 滚动性能
- **优化 Pager 预加载**：`beyondViewportPageCount = 1`
- **优化列表滑动**：使用 `verticalScroll` 的高效实现
- **减少重组频率**：通过 `remember` 和 `key()` 优化

## 相关文件

- `PerformanceOptimization.kt`：性能优化工具类
- `PredictiveBackGestureHandler.kt`：预测性返回效果实现
- `MainActivityOptimized.kt`：优化的导航和动画
- `ScheduleGridOptimized.kt`：优化的课表网格
- `CourseBlockOptimized.kt`：优化的课程块

## 支持

如有问题或建议，欢迎提交 Issue 或 PR。

---

**最后更新**：2026 年 3 月 13 日
**优化版本**：1.0

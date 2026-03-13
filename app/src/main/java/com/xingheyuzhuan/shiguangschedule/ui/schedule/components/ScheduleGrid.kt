package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.xingheyuzhuan.shiguangschedule.R
import com.xingheyuzhuan.shiguangschedule.data.db.main.TimeSlot
import com.xingheyuzhuan.shiguangschedule.ui.schedule.MergedCourseBlock

/**
 * 绘图模型接口
 * startSection/endSection 基于逻辑节次坐标（0.0 代表第一节课顶部）
 */
interface ISchedulable {
    val columnIndex: Int
    val startSection: Float
    val endSection: Float
    val rawData: MergedCourseBlock
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun ScheduleGrid(
    style: ScheduleGridStyleComposed,
    dates: List<String>,
    timeSlots: List<TimeSlot>,
    mergedCourses: List<MergedCourseBlock>,
    showWeekends: Boolean,
    todayIndex: Int,
    firstDayOfWeek: Int,
    onCourseBlockClicked: (MergedCourseBlock) -> Unit,
    onGridCellClicked: (Int, Int) -> Unit,
    onTimeSlotClicked: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = maxWidth

        // 1. 处理日期与星期排序
        val weekDays = stringArrayResource(R.array.week_days_short_names).toList()
        val reorderedWeekDays = rearrangeDays(weekDays, firstDayOfWeek)
        val displayDays = if (showWeekends) reorderedWeekDays else reorderedWeekDays.take(5)

        // 2. 计算尺寸
        val cellWidth = (screenWidth - style.timeColumnWidth) / displayDays.size
        val totalGridHeight = style.sectionHeight * timeSlots.size
        // 缓存网格线颜色，避免每次重组重新计算 copy(alpha=)
        val gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)

        // 3. 转换绘图数据 - 使用 remember 缓存
        val schedulables = remember(mergedCourses, firstDayOfWeek, showWeekends) {
            mergedCourses.mapNotNull { block ->
                val displayIdx = mapDayToDisplayIndex(block.day, firstDayOfWeek, showWeekends)
                if (displayIdx == -1) return@mapNotNull null
                object : ISchedulable {
                    override val columnIndex = displayIdx
                    override val startSection = block.startSection
                    override val endSection = block.endSection
                    override val rawData = block
                }
            }
        }

        // 4. 预计算 TimeSlot 查找表，避免在每个课程块内部做 list.find()
        val timeSlotMap = remember(timeSlots) {
            timeSlots.associateBy { it.number }
        }

        Column(Modifier.fillMaxSize()) {
            DayHeader(style, displayDays, dates, cellWidth, todayIndex, gridLineColor)

            Row(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                TimeColumn(style, timeSlots, onTimeSlotClicked, Modifier.height(totalGridHeight), gridLineColor)

                Box(Modifier.height(totalGridHeight).weight(1f)) {
                    // 优化：使用单个手势检测器替代 91 个 Composable
                    ClickableGrid(displayDays.size, timeSlots.size, cellWidth, style.sectionHeight) { dayIdx, sec ->
                        onGridCellClicked(mapDisplayIndexToDay(dayIdx, firstDayOfWeek), sec)
                    }

                    schedulables.forEach { item ->
                        // key() 保证 Compose 按课程 ID 追踪每个块，
                        // 单个课程变化时只重组该块，而非整个网格。
                        key(item.rawData.courses.firstOrNull()?.course?.id ?: item.hashCode()) {
                            val topOffset = item.startSection * style.sectionHeight
                            val blockHeight = (item.endSection - item.startSection) * style.sectionHeight

                            // 点击缩放动画
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.96f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ),
                                label = "courseBlockPressScale"
                            )

                            Box(
                                modifier = Modifier
                                    .offset(x = item.columnIndex * cellWidth, y = topOffset)
                                    .size(width = cellWidth, height = blockHeight)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) { onCourseBlockClicked(item.rawData) }
                            ) {
                                CourseBlock(
                                    mergedBlock = item.rawData,
                                    style = style,
                                    startTime = item.rawData.courses.firstOrNull()?.course?.let {
                                        if (it.isCustomTime) it.customStartTime
                                        else timeSlotMap[it.startSection]?.startTime
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 子组件
@Composable
private fun DayHeader(style: ScheduleGridStyleComposed, displayDays: List<String>, dates: List<String>, cellWidth: Dp, todayIndex: Int, lineColor: Color) {
    // 壁纸模式下降低不透明度实现毛玻璃感
    val surfaceColor = if (style.hasBackgroundImage) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    }
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.48f)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        Modifier
            .fillMaxWidth()
            .height(style.dayHeaderHeight)
            .padding(horizontal = 8.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp), ambientColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(14.dp))
            .background(surfaceColor)
    ) {
        Spacer(Modifier.width(style.timeColumnWidth).fillMaxHeight())

        displayDays.forEachIndexed { index, day ->
            Box(Modifier.width(cellWidth).fillMaxHeight()
                .background(if (index == todayIndex) primaryContainerColor else Color.Transparent)
                .drawBehind {
                    if (!style.hideGridLines) {
                        drawLine(lineColor, Offset(size.width, 0f), Offset(size.width, size.height), 1f)
                    }
                },
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurfaceColor
                    )
                    if (!style.hideDateUnderDay && dates.size > index) {
                        Text(
                            text = dates[index],
                            fontSize = 10.sp,
                            color = onSurfaceVariantColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeColumn(style: ScheduleGridStyleComposed, timeSlots: List<TimeSlot>, onTimeSlotClicked: () -> Unit, modifier: Modifier, lineColor: Color) {
    // 壁纸模式下降低不透明度实现毛玻璃感
    val surfaceColor = if (style.hasBackgroundImage) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
    }
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .width(style.timeColumnWidth)
            .padding(start = 6.dp, end = 6.dp, top = 8.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp), ambientColor = Color.Black.copy(alpha = 0.06f))
            .clip(RoundedCornerShape(12.dp)),
        color = surfaceColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            timeSlots.forEach { slot ->
                Column(Modifier.fillMaxWidth().height(style.sectionHeight).clickable { onTimeSlotClicked() }.drawBehind {
                    if (!style.hideGridLines) {
                        drawLine(lineColor, Offset(0f, size.height), Offset(size.width, size.height), 1f)
                    }
                },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Text(
                        text = slot.number.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurfaceColor
                    )
                    if (!style.hideSectionTime) {
                        Text(
                            text = slot.startTime,
                            fontSize = 10.sp,
                            color = onSurfaceVariantColor
                        )
                        Text(
                            text = slot.endTime,
                            fontSize = 10.sp,
                            color = onSurfaceVariantColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridLines(dayCount: Int, slotCount: Int, cellWidth: Dp, totalHeight: Dp, sectionHeight: Dp, lineColor: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val h = totalHeight.toPx()
        repeat(dayCount) { i ->
            val x = i * cellWidth.toPx()
            drawLine(lineColor, Offset(x, 0f), Offset(x, h), 1f)
        }
        repeat(slotCount) { i ->
            val y = i * sectionHeight.toPx()
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1f)
        }
    }
}

/**
 * 优化：使用单个 pointerInput 手势检测替代 Column + Row + Spacer 创建的 91 个 Composable。
 * 从 O(dayCount * slotCount) 个 Composable 减少到 1 个，大幅降低重组开销。
 */
@Composable
private fun ClickableGrid(dayCount: Int, slotCount: Int, cellWidth: Dp, sectionHeight: Dp, onClick: (Int, Int) -> Unit) {
    val density = LocalDensity.current
    val cellWidthPx = with(density) { cellWidth.toPx() }
    val sectionHeightPx = with(density) { sectionHeight.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(dayCount, slotCount, cellWidthPx, sectionHeightPx) {
                detectTapGestures { offset ->
                    val dayIdx = (offset.x / cellWidthPx).toInt().coerceIn(0, dayCount - 1)
                    val sec = (offset.y / sectionHeightPx).toInt().coerceIn(0, slotCount - 1) + 1
                    onClick(dayIdx, sec)
                }
            }
    )
}

// 辅助逻辑
private fun rearrangeDays(originalDays: List<String>, firstDayOfWeek: Int): List<String> {
    val startIndex = (firstDayOfWeek - 1).coerceIn(0, 6)
    return originalDays.subList(startIndex, originalDays.size) + originalDays.subList(0, startIndex)
}

private fun mapDayToDisplayIndex(courseDay: Int, firstDayOfWeek: Int, showWeekends: Boolean): Int {
    val idx = (courseDay - firstDayOfWeek + 7) % 7
    return if (idx >= if (showWeekends) 7 else 5) -1 else idx
}

private fun mapDisplayIndexToDay(idx: Int, firstDayOfWeek: Int): Int = (firstDayOfWeek - 1 + idx) % 7 + 1

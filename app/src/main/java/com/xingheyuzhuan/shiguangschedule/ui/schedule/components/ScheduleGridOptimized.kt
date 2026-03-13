package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.xingheyuzhuan.shiguangschedule.ui.utils.optimizedShadow

/**
 * 优化版 ScheduleGrid 组件
 * 
 * 优化内容：
 * 1. 使用 key() 优化列表渲染
 * 2. 减少不必要的重组
 * 3. 优化阴影计算
 * 4. 改进 UI 视觉效果（更精美的圆角、阴影、间距）
 * 5. 提升整体丝滑度
 */

interface ISchedulable {
    val columnIndex: Int
    val startSection: Float
    val endSection: Float
    val rawData: MergedCourseBlock
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun ScheduleGridOptimized(
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
        
        // 优化：使用更精细的网格线颜色
        val gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.06f)
        val gridLineColorDarker = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)

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

        Column(Modifier.fillMaxSize()) {
            DayHeaderOptimized(style, displayDays, dates, cellWidth, todayIndex, gridLineColor, gridLineColorDarker)

            Row(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                TimeColumnOptimized(style, timeSlots, onTimeSlotClicked, Modifier.height(totalGridHeight), gridLineColor, gridLineColorDarker)

                Box(Modifier.height(totalGridHeight).weight(1f)) {
                    // 优化：使用单个手势检测器
                    ClickableGrid(displayDays.size, timeSlots.size, cellWidth, style.sectionHeight) { dayIdx, sec ->
                        onGridCellClicked(mapDisplayIndexToDay(dayIdx, firstDayOfWeek), sec)
                    }

                    // 使用 key() 优化列表渲染
                    schedulables.forEach { item ->
                        key(item.rawData.id) {
                            val topOffset = item.startSection * style.sectionHeight
                            val blockHeight = (item.endSection - item.startSection) * style.sectionHeight

                            Box(
                                modifier = Modifier
                                    .offset(x = item.columnIndex * cellWidth, y = topOffset)
                                    .size(width = cellWidth, height = blockHeight)
                                    .padding(style.courseBlockOuterPadding)
                                    .clickable { onCourseBlockClicked(item.rawData) }
                            ) {
                                CourseBlockOptimized(
                                    mergedBlock = item.rawData,
                                    style = style,
                                    startTime = item.rawData.courses.firstOrNull()?.course?.let {
                                        if (it.isCustomTime) it.customStartTime
                                        else timeSlots.find { ts -> ts.number == it.startSection }?.startTime
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

/**
 * 优化的日期头部组件
 * 改进阴影、圆角和间距
 */
@Composable
private fun DayHeaderOptimized(
    style: ScheduleGridStyleComposed,
    displayDays: List<String>,
    dates: List<String>,
    cellWidth: Dp,
    todayIndex: Int,
    lineColor: Color,
    lineColorDarker: Color
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.52f)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        Modifier
            .fillMaxWidth()
            .height(style.dayHeaderHeight)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .optimizedShadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
    ) {
        Spacer(Modifier.width(style.timeColumnWidth).fillMaxHeight())

        displayDays.forEachIndexed { index, day ->
            Box(
                Modifier
                    .width(cellWidth)
                    .fillMaxHeight()
                    .background(if (index == todayIndex) primaryContainerColor else Color.Transparent)
                    .drawBehind {
                        if (!style.hideGridLines && index < displayDays.size - 1) {
                            drawLine(lineColor, Offset(size.width, 0f), Offset(size.width, size.height), 1f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
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

/**
 * 优化的时间列组件
 * 改进阴影、圆角和视觉效果
 */
@Composable
private fun TimeColumnOptimized(
    style: ScheduleGridStyleComposed,
    timeSlots: List<TimeSlot>,
    onTimeSlotClicked: () -> Unit,
    modifier: Modifier,
    lineColor: Color,
    lineColorDarker: Color
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .width(style.timeColumnWidth)
            .padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 8.dp)
            .optimizedShadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.02f)
            )
            .clip(RoundedCornerShape(14.dp)),
        color = surfaceColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column {
            timeSlots.forEachIndexed { index, slot ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(style.sectionHeight)
                        .clickable { onTimeSlotClicked() }
                        .drawBehind {
                            if (!style.hideGridLines && index < timeSlots.size - 1) {
                                drawLine(lineColor, Offset(0f, size.height), Offset(size.width, size.height), 1f)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = slot.number.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
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
private fun ClickableGrid(
    dayCount: Int,
    slotCount: Int,
    cellWidth: Dp,
    sectionHeight: Dp,
    onClick: (Int, Int) -> Unit
) {
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

private fun rearrangeDays(originalDays: List<String>, firstDayOfWeek: Int): List<String> {
    val startIndex = (firstDayOfWeek - 1).coerceIn(0, 6)
    return originalDays.subList(startIndex, originalDays.size) + originalDays.subList(0, startIndex)
}

private fun mapDayToDisplayIndex(courseDay: Int, firstDayOfWeek: Int, showWeekends: Boolean): Int {
    val idx = (courseDay - firstDayOfWeek + 7) % 7
    return if (idx >= if (showWeekends) 7 else 5) -1 else idx
}

private fun mapDisplayIndexToDay(idx: Int, firstDayOfWeek: Int): Int = (firstDayOfWeek - 1 + idx) % 7 + 1

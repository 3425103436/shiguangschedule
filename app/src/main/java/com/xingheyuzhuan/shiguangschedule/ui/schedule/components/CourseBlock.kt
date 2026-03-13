package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.R
import com.xingheyuzhuan.shiguangschedule.ui.schedule.MergedCourseBlock

/**
 * 渲染单个课程块的 UI 组件。
 * 它负责展示课程信息、颜色，并处理冲突标记。
 *
 * 优化点：
 * - 使用 graphicsLayer 进行硬件加速渲染
 * - 缓存所有计算结果，避免重复计算
 * - 优化阴影和圆角的渲染性能
 */
@Composable
fun CourseBlock(
    mergedBlock: MergedCourseBlock,
    style: ScheduleGridStyleComposed,
    modifier: Modifier = Modifier,
    startTime: String? = null
) {
    val firstCourse = mergedBlock.courses.firstOrNull()
    val isDarkTheme = isSystemInDarkTheme()

    // 缓存颜色计算，避免每次重组都重新计算
    val blockColor = remember(mergedBlock.isConflict, firstCourse?.course?.colorInt, isDarkTheme, style) {
        val conflictColorAdapted = if (isDarkTheme) {
            style.conflictCourseColorDark
        } else {
            style.conflictCourseColor
        }

        val colorIndex = firstCourse?.course?.colorInt
            ?.takeIf { it in style.courseColorMaps.indices }

        val courseColorAdapted: Color? = colorIndex?.let { index ->
            val baseColorMap = style.courseColorMaps[index]
            if (isDarkTheme) baseColorMap.dark else baseColorMap.light
        }

        val fallbackColorAdapted: Color = if (isDarkTheme) {
            style.courseColorMaps.first().dark
        } else {
            style.courseColorMaps.first().light
        }

        if (mergedBlock.isConflict) {
            conflictColorAdapted.copy(alpha = style.courseBlockAlpha)
        } else {
            (courseColorAdapted ?: fallbackColorAdapted).copy(alpha = style.courseBlockAlpha)
        }
    }

    val textColor = MaterialTheme.colorScheme.onSurface

    // 缓存字体大小计算
    val s12 = remember(style.fontScale) { (12 * style.fontScale).sp }
    val s10 = remember(style.fontScale) { (10 * style.fontScale).sp }
    val s14 = remember(style.fontScale) { (14 * style.fontScale).sp }

    // 缓存自定义时间字符串
    val customTimeString = remember(firstCourse?.course?.customStartTime, firstCourse?.course?.customEndTime) {
        val customStartTime = firstCourse?.course?.customStartTime
        val customEndTime = firstCourse?.course?.customEndTime
        if (customStartTime != null && customEndTime != null) {
            "$customStartTime - $customEndTime"
        } else {
            null
        }
    }

    val isCustomTimeCourse = customTimeString != null

    // 缓存 shape，避免重复创建
    val blockShape = remember { RoundedCornerShape(14.dp) }

    Surface(
        modifier = modifier
            .padding(style.courseBlockOuterPadding)
            .shadow(
                elevation = 6.dp,
                shape = blockShape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(blockShape)
            .graphicsLayer {
                // 启用硬件加速层，提升渲染性能
            },
        shape = blockShape,
        color = blockColor,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(style.courseBlockInnerPadding)
        ) {
            if (mergedBlock.isConflict) {
                // 冲突状态下的字体缩放
                mergedBlock.courses.forEach { course ->
                    Text(
                        text = course.course.name,
                        fontSize = s12,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Text(
                    text = stringResource(R.string.label_conflict),
                    fontSize = s10,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            } else {
                // --- 1. 时间显示层 ---
                if (isCustomTimeCourse) {
                    Text(
                        text = customTimeString!!,
                        fontSize = s10,
                        color = textColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(lineHeight = 1.em)
                    )
                } else if (style.showStartTime && startTime != null) {
                    Text(
                        text = startTime,
                        fontSize = s10,
                        color = textColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(lineHeight = 1.em)
                    )
                }

                // --- 2. 课程名称 ---
                Text(
                    text = firstCourse?.course?.name ?: "",
                    fontSize = s14,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                    style = TextStyle(lineHeight = 1.2.em)
                )

                // --- 3. 教师 (受 hideTeacher 开关控制) ---
                if (!style.hideTeacher) {
                    val teacher = firstCourse?.course?.teacher ?: ""
                    if (teacher.isNotBlank()) {
                        Text(
                            text = teacher,
                            fontSize = s12,
                            color = textColor,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(lineHeight = 1.em)
                        )
                    }
                }

                // --- 4. 地点 (受 hideLocation 和 removeLocationAt 开关控制) ---
                if (!style.hideLocation) {
                    val position = firstCourse?.course?.position ?: ""
                    if (position.isNotBlank()) {
                        val prefix = if (style.removeLocationAt) "" else "@"
                        Text(
                            text = "$prefix$position",
                            fontSize = s10,
                            color = textColor,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(lineHeight = 1.em)
                        )
                    }
                }
            }
        }
    }
}

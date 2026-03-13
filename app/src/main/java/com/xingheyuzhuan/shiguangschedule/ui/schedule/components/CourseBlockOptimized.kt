package com.xingheyuzhuan.shiguangschedule.ui.schedule.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.R
import com.xingheyuzhuan.shiguangschedule.ui.schedule.MergedCourseBlock
import com.xingheyuzhuan.shiguangschedule.ui.utils.optimizedShadow

/**
 * 优化版 CourseBlock 组件
 * 
 * 优化内容：
 * 1. 改进阴影效果（更精细、更自然）
 * 2. 优化圆角设计（更现代）
 * 3. 改进间距和排版
 * 4. 提升整体视觉质感
 * 5. 性能优化（缓存计算）
 */

@Composable
fun CourseBlockOptimized(
    mergedBlock: MergedCourseBlock,
    style: ScheduleGridStyleComposed,
    modifier: Modifier = Modifier,
    startTime: String? = null
) {
    val firstCourse = mergedBlock.courses.firstOrNull()
    val isDarkTheme = isSystemInDarkTheme()

    // 缓存颜色计算
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

    // 优化：使用更精美的圆角（18dp 而非 14dp）
    val blockShape = remember { RoundedCornerShape(18.dp) }

    Surface(
        modifier = modifier
            .padding(style.courseBlockOuterPadding)
            .optimizedShadow(
                elevation = 4.dp,
                shape = blockShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(blockShape),
        shape = blockShape,
        color = blockColor,
        tonalElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(style.courseBlockInnerPadding)
        ) {
            if (mergedBlock.isConflict) {
                // 冲突状态下的显示
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
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                // --- 1. 时间显示层 ---
                if (isCustomTimeCourse) {
                    Text(
                        text = customTimeString!!,
                        fontSize = s10,
                        color = textColor.copy(alpha = 0.75f),
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(lineHeight = 1.em)
                    )
                } else if (style.showStartTime && startTime != null) {
                    Text(
                        text = startTime,
                        fontSize = s10,
                        color = textColor.copy(alpha = 0.75f),
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
                    style = TextStyle(lineHeight = 1.25.em)
                )

                // --- 3. 教师 ---
                if (!style.hideTeacher) {
                    val teacher = firstCourse?.course?.teacher ?: ""
                    if (teacher.isNotBlank()) {
                        Text(
                            text = teacher,
                            fontSize = s12,
                            color = textColor.copy(alpha = 0.85f),
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(lineHeight = 1.em)
                        )
                    }
                }

                // --- 4. 地点 ---
                if (!style.hideLocation) {
                    val position = firstCourse?.course?.position ?: ""
                    if (position.isNotBlank()) {
                        val prefix = if (style.removeLocationAt) "" else "@"
                        Text(
                            text = "$prefix$position",
                            fontSize = s10,
                            color = textColor.copy(alpha = 0.7f),
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(lineHeight = 1.em)
                        )
                    }
                }
            }
        }
    }
}

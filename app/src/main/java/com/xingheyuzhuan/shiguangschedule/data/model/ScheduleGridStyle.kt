package com.xingheyuzhuan.shiguangschedule.data.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.DualColorProto
import com.xingheyuzhuan.shiguangschedule.data.model.schedule_style.ScheduleGridStyleProto

// 1. Compose 业务模型

/**
 * 浅色和深色模式下的颜色对。
 * @Immutable 标记：纯值对象，避免 Compose 不必要的重组检查。
 */
@Immutable
data class DualColor(val light: Color, val dark: Color)

/**
 * 课表网格样式配置的业务模型
 * 所有尺寸（Dp）属性使用 Float，颜色（Color）属性使用 Long。
 */
data class ScheduleGridStyle(
    // Grid 尺寸 (单位: Float/Dp)
    val timeColumnWidthDp: Float = DEFAULT_TIME_COLUMN_WIDTH,
    val dayHeaderHeightDp: Float = DEFAULT_DAY_HEADER_HEIGHT,
    val sectionHeightDp: Float = DEFAULT_SECTION_HEIGHT,

    // CourseBlock 外观 (单位: Float/Dp & Float)
    val courseBlockCornerRadiusDp: Float = DEFAULT_BLOCK_CORNER_RADIUS,
    val courseBlockOuterPaddingDp: Float = DEFAULT_BLOCK_OUTER_PADDING,
    val courseBlockInnerPaddingDp: Float = DEFAULT_BLOCK_INNER_PADDING,
    val courseBlockAlphaFloat: Float = DEFAULT_BLOCK_ALPHA,

    // 颜色 (单位: Long/ARGB)
    val conflictCourseColorLong: Long = DEFAULT_CONFLICT_COLOR,
    val conflictCourseColorDarkLong: Long = DEFAULT_CONFLICT_COLOR_DARK,

    // 颜色列表
    val courseColorMaps: List<DualColor> = DEFAULT_COLOR_MAPS,

    val courseBlockFontScale: Float = DEFAULT_FONT_SCALE,

    val hideGridLines: Boolean = false,
    val hideSectionTime: Boolean = false,
    val hideDateUnderDay: Boolean = false,
    val showStartTime: Boolean = false,
    val hideLocation: Boolean = false,
    val hideTeacher: Boolean = false,
    val removeLocationAt: Boolean = false,

    // 背景壁纸路径 (存储在私有目录下的绝对路径)
    val backgroundImagePath: String? = null
) {

    fun generateRandomColorIndex(): Int {
        if (courseColorMaps.isEmpty()) return 0
        return kotlin.random.Random.nextInt(courseColorMaps.size)
    }

    companion object {
        // --- 默认常量 ---
        internal val DEFAULT_TIME_COLUMN_WIDTH = 40f
        internal val DEFAULT_DAY_HEADER_HEIGHT = 45f
        internal val DEFAULT_SECTION_HEIGHT = 70f
        internal val DEFAULT_BLOCK_CORNER_RADIUS = 14f
        internal val DEFAULT_BLOCK_OUTER_PADDING = 2.5f
        internal val DEFAULT_BLOCK_INNER_PADDING = 6f
        internal val DEFAULT_BLOCK_ALPHA = 0.88f
        internal val DEFAULT_FONT_SCALE = 1f
        internal val DEFAULT_CONFLICT_COLOR = 0xFFFFD4D4L
        internal val DEFAULT_CONFLICT_COLOR_DARK = 0xFF4D1A1AL

        internal val DEFAULT_COLOR_MAPS = listOf(
            // 1. Soft Peach
            DualColor(light = Color(0xFFFDE8D8), dark = Color(0xFF5C3A28)),
            // 2. Cream Yellow
            DualColor(light = Color(0xFFFFF4D6), dark = Color(0xFF5C4E28)),
            // 3. Mint Green
            DualColor(light = Color(0xFFDCF5E7), dark = Color(0xFF28503A)),
            // 4. Soft Sage
            DualColor(light = Color(0xFFE4F0D8), dark = Color(0xFF3A5028)),
            // 5. Baby Blue
            DualColor(light = Color(0xFFDDE9FF), dark = Color(0xFF283C5C)),
            // 6. Lavender
            DualColor(light = Color(0xFFE8DFFF), dark = Color(0xFF3A2860)),
            // 7. Rose Quartz
            DualColor(light = Color(0xFFFFE3EE), dark = Color(0xFF5C2840)),
            // 8. Sky Mist
            DualColor(light = Color(0xFFD6F0F5), dark = Color(0xFF284850)),
            // 9. Warm Gray
            DualColor(light = Color(0xFFF0EDE8), dark = Color(0xFF48443C)),
            // 10. Soft Coral
            DualColor(light = Color(0xFFFEE0DB), dark = Color(0xFF5C3430)),
            // 11. Lilac
            DualColor(light = Color(0xFFF0E0F5), dark = Color(0xFF48305C)),
            // 12. Pale Teal
            DualColor(light = Color(0xFFD8F0EC), dark = Color(0xFF2D4844)),
        )

        /**
         * 默认样式对象，用于首次启动或重置样式。
         * 注意：backgroundImagePath 默认为 null，但在 ViewModel 的重置逻辑中会特殊处理以保留壁纸。
         */
        val DEFAULT = ScheduleGridStyle(
            timeColumnWidthDp = DEFAULT_TIME_COLUMN_WIDTH,
            dayHeaderHeightDp = DEFAULT_DAY_HEADER_HEIGHT,
            sectionHeightDp = DEFAULT_SECTION_HEIGHT,
            courseBlockCornerRadiusDp = DEFAULT_BLOCK_CORNER_RADIUS,
            courseBlockOuterPaddingDp = DEFAULT_BLOCK_OUTER_PADDING,
            courseBlockInnerPaddingDp = DEFAULT_BLOCK_INNER_PADDING,
            courseBlockAlphaFloat = DEFAULT_BLOCK_ALPHA,
            conflictCourseColorLong = DEFAULT_CONFLICT_COLOR,
            conflictCourseColorDarkLong = DEFAULT_CONFLICT_COLOR_DARK,
            courseColorMaps = DEFAULT_COLOR_MAPS,
            courseBlockFontScale = DEFAULT_FONT_SCALE,
            hideGridLines = false,
            hideSectionTime = false,
            hideDateUnderDay = false,
            showStartTime = false,
            hideLocation = false,
            hideTeacher = false,
            removeLocationAt = false,
            backgroundImagePath = null
        )
    }
}


// 2. Proto ⇔ Compose 转换扩展函数

fun DualColorProto.toCompose(): DualColor {
    return DualColor(
        light = Color(this.lightColor.toInt()),
        dark = Color(this.darkColor.toInt())
    )
}

fun DualColor.toProto(): DualColorProto {
    return DualColorProto.newBuilder()
        .setLightColor(this.light.toArgb().toLong())
        .setDarkColor(this.dark.toArgb().toLong())
        .build()
}

/**
 * Protobuf -> ScheduleGridStyle 转换函数
 */
fun ScheduleGridStyleProto.toCompose(): ScheduleGridStyle {
    val d = ScheduleGridStyle.DEFAULT

    return ScheduleGridStyle(
        // 1. 基础布局尺寸
        timeColumnWidthDp = if (hasTimeColumnWidthDp()) timeColumnWidthDp else d.timeColumnWidthDp,
        dayHeaderHeightDp = if (hasDayHeaderHeightDp()) dayHeaderHeightDp else d.dayHeaderHeightDp,
        sectionHeightDp = if (hasSectionHeightDp()) sectionHeightDp else d.sectionHeightDp,

        // 2. 课程块外观
        courseBlockCornerRadiusDp = if (hasCourseBlockCornerRadiusDp()) courseBlockCornerRadiusDp else d.courseBlockCornerRadiusDp,
        courseBlockOuterPaddingDp = if (hasCourseBlockOuterPaddingDp()) courseBlockOuterPaddingDp else d.courseBlockOuterPaddingDp,
        courseBlockInnerPaddingDp = if (hasCourseBlockInnerPaddingDp()) courseBlockInnerPaddingDp else d.courseBlockInnerPaddingDp,

        // 3. 透明度与缩放
        courseBlockAlphaFloat = if (hasCourseBlockAlphaFloat()) courseBlockAlphaFloat else d.courseBlockAlphaFloat,
        courseBlockFontScale = if (hasCourseBlockFontScale()) courseBlockFontScale else d.courseBlockFontScale,

        // 4. 颜色配置
        conflictCourseColorLong = if (hasConflictCourseColorLong()) conflictCourseColorLong else d.conflictCourseColorLong,
        conflictCourseColorDarkLong = if (hasConflictCourseColorDarkLong()) conflictCourseColorDarkLong else d.conflictCourseColorDarkLong,

        // 5. 其他列表和布尔值
        courseColorMaps = if (this.courseColorMapsList.isEmpty()) d.courseColorMaps else this.courseColorMapsList.map { it.toCompose() },
        hideGridLines = this.hideGridLines,
        hideSectionTime = this.hideSectionTime,
        hideDateUnderDay = this.hideDateUnderDay,
        showStartTime = this.showStartTime,
        hideLocation = this.hideLocation,
        hideTeacher = this.hideTeacher,
        removeLocationAt = this.removeLocationAt,

        // 6. 背景图路径映射 (空字符串转 null)
        backgroundImagePath = if (this.backgroundImagePath.isNullOrEmpty()) null else this.backgroundImagePath
    )
}

/**
 * ScheduleGridStyle -> Protobuf 转换 (用于写入)
 */
fun ScheduleGridStyle.toProto(): ScheduleGridStyleProto {
    return ScheduleGridStyleProto.newBuilder().apply {
        timeColumnWidthDp = this@toProto.timeColumnWidthDp
        dayHeaderHeightDp = this@toProto.dayHeaderHeightDp
        sectionHeightDp = this@toProto.sectionHeightDp
        courseBlockCornerRadiusDp = this@toProto.courseBlockCornerRadiusDp
        courseBlockOuterPaddingDp = this@toProto.courseBlockOuterPaddingDp
        courseBlockInnerPaddingDp = this@toProto.courseBlockInnerPaddingDp
        courseBlockAlphaFloat = this@toProto.courseBlockAlphaFloat
        conflictCourseColorLong = this@toProto.conflictCourseColorLong
        conflictCourseColorDarkLong = this@toProto.conflictCourseColorDarkLong
        courseBlockFontScale = this@toProto.courseBlockFontScale

        addAllCourseColorMaps(this@toProto.courseColorMaps.map { it.toProto() })

        hideGridLines = this@toProto.hideGridLines
        hideSectionTime = this@toProto.hideSectionTime
        hideDateUnderDay = this@toProto.hideDateUnderDay
        showStartTime = this@toProto.showStartTime
        hideLocation = this@toProto.hideLocation
        hideTeacher = this@toProto.hideTeacher
        removeLocationAt = this@toProto.removeLocationAt

        // 将 null 映射回空字符串写入 Proto
        backgroundImagePath = this@toProto.backgroundImagePath ?: ""
    }.build()
}
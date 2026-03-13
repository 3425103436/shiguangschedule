package com.xingheyuzhuan.shiguangschedule.widget.countdown

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.xingheyuzhuan.shiguangschedule.MainActivity
import com.xingheyuzhuan.shiguangschedule.R
import com.xingheyuzhuan.shiguangschedule.widget.WidgetSnapshot
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 下一节课倒计时小组件渲染器
 *
 * 功能特性：
 * 1. 显示下一节课的倒计时（小时:分钟 格式）
 * 2. 上课中状态：显示"正在上课"和剩余时间
 * 3. 课间状态：显示距离下一节课的倒计时
 * 4. 今日无课/课程结束：显示友好提示
 * 5. 显示课程名称、教室位置和教师信息
 */
object CountdownNativeRenderer {

    fun render(context: Context, snapshot: WidgetSnapshot): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_countdown_native)

        // 1. 设置点击跳转
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 100, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        rv.setOnClickPendingIntent(R.id.widget_countdown_root, pendingIntent)

        // 2. 数据准备
        val now = LocalTime.now()
        val today = LocalDate.now()
        val todayStr = today.toString()
        val allCourses = snapshot.coursesList
        val currentWeek = if (snapshot.currentWeek <= 0) null else snapshot.currentWeek

        // 3. 头部日期信息
        val dateFormatter = DateTimeFormatter.ofPattern("M月d日 E", Locale.getDefault())
        rv.setTextViewText(R.id.tv_countdown_date, today.format(dateFormatter))
        currentWeek?.let {
            rv.setTextViewText(R.id.tv_countdown_week, context.getString(R.string.status_current_week_format, it))
        }

        // 假期处理
        if (currentWeek == null) {
            showCountdownStatus(rv, "假期模式", "享受假期吧！", "--:--")
            return rv
        }

        // 4. 筛选今日课程
        val todayCourses = allCourses.filter {
            (it.date == todayStr || it.date.isBlank()) && !it.isSkipped
        }.sortedBy { it.startTime }

        if (todayCourses.isEmpty()) {
            showCountdownStatus(rv, "今日无课", "好好休息一下", "--:--")
            return rv
        }

        // 5. 核心倒计时逻辑
        // 查找正在上的课
        val currentCourse = todayCourses.find { course ->
            try {
                val start = LocalTime.parse(course.startTime)
                val end = LocalTime.parse(course.endTime)
                now in start..end
            } catch (e: Exception) { false }
        }

        if (currentCourse != null) {
            // 正在上课
            try {
                val endTime = LocalTime.parse(currentCourse.endTime)
                val remaining = Duration.between(now, endTime)
                val hours = remaining.toHours()
                val minutes = remaining.toMinutes() % 60

                val countdownText = if (hours > 0) {
                    String.format("%d:%02d", hours, minutes)
                } else {
                    String.format("%d分钟", minutes)
                }

                rv.setViewVisibility(R.id.container_countdown_content, View.VISIBLE)
                rv.setViewVisibility(R.id.container_countdown_status, View.GONE)

                rv.setTextViewText(R.id.tv_countdown_label, "正在上课 · 还剩")
                rv.setTextViewText(R.id.tv_countdown_time, countdownText)
                rv.setTextViewText(R.id.tv_countdown_course_name, currentCourse.name)
                rv.setTextViewText(R.id.tv_countdown_course_position, currentCourse.position)
                rv.setTextViewText(
                    R.id.tv_countdown_course_time_range,
                    "${currentCourse.startTime.take(5)} - ${currentCourse.endTime.take(5)}"
                )

                if (currentCourse.teacher.isNotBlank()) {
                    rv.setViewVisibility(R.id.tv_countdown_course_teacher, View.VISIBLE)
                    rv.setTextViewText(R.id.tv_countdown_course_teacher, currentCourse.teacher)
                } else {
                    rv.setViewVisibility(R.id.tv_countdown_course_teacher, View.GONE)
                }

                // 设置颜色指示器
                val style = snapshot.style
                if (currentCourse.colorInt < style.courseColorMapsCount) {
                    val colorPair = style.getCourseColorMaps(currentCourse.colorInt)
                    rv.setInt(R.id.countdown_course_indicator, "setBackgroundColor", colorPair.lightColor.toInt())
                    rv.setInt(R.id.countdown_course_indicator_dark, "setBackgroundColor", colorPair.darkColor.toInt())
                }

            } catch (e: Exception) {
                showCountdownStatus(rv, currentCourse.name, "上课中", "...")
            }
            return rv
        }

        // 查找下一节课
        val nextCourse = todayCourses.find { course ->
            try {
                LocalTime.parse(course.startTime) > now
            } catch (e: Exception) { false }
        }

        if (nextCourse != null) {
            // 即将上课
            try {
                val startTime = LocalTime.parse(nextCourse.startTime)
                val remaining = Duration.between(now, startTime)
                val hours = remaining.toHours()
                val minutes = remaining.toMinutes() % 60

                val countdownText = if (hours > 0) {
                    String.format("%d:%02d", hours, minutes)
                } else {
                    String.format("%d分钟", minutes)
                }

                rv.setViewVisibility(R.id.container_countdown_content, View.VISIBLE)
                rv.setViewVisibility(R.id.container_countdown_status, View.GONE)

                rv.setTextViewText(R.id.tv_countdown_label, "下一节课 · 还有")
                rv.setTextViewText(R.id.tv_countdown_time, countdownText)
                rv.setTextViewText(R.id.tv_countdown_course_name, nextCourse.name)
                rv.setTextViewText(R.id.tv_countdown_course_position, nextCourse.position)
                rv.setTextViewText(
                    R.id.tv_countdown_course_time_range,
                    "${nextCourse.startTime.take(5)} - ${nextCourse.endTime.take(5)}"
                )

                if (nextCourse.teacher.isNotBlank()) {
                    rv.setViewVisibility(R.id.tv_countdown_course_teacher, View.VISIBLE)
                    rv.setTextViewText(R.id.tv_countdown_course_teacher, nextCourse.teacher)
                } else {
                    rv.setViewVisibility(R.id.tv_countdown_course_teacher, View.GONE)
                }

                // 设置颜色指示器
                val style = snapshot.style
                if (nextCourse.colorInt < style.courseColorMapsCount) {
                    val colorPair = style.getCourseColorMaps(nextCourse.colorInt)
                    rv.setInt(R.id.countdown_course_indicator, "setBackgroundColor", colorPair.lightColor.toInt())
                    rv.setInt(R.id.countdown_course_indicator_dark, "setBackgroundColor", colorPair.darkColor.toInt())
                }

            } catch (e: Exception) {
                showCountdownStatus(rv, nextCourse.name, "即将开始", "...")
            }
            return rv
        }

        // 今日课程已全部结束
        showCountdownStatus(rv, "今日课程已结束", "好好休息吧", "✓")
        return rv
    }

    private fun showCountdownStatus(rv: RemoteViews, title: String, subtitle: String, timeText: String) {
        rv.setViewVisibility(R.id.container_countdown_content, View.GONE)
        rv.setViewVisibility(R.id.container_countdown_status, View.VISIBLE)
        rv.setTextViewText(R.id.tv_countdown_status_title, title)
        rv.setTextViewText(R.id.tv_countdown_status_subtitle, subtitle)
        rv.setTextViewText(R.id.tv_countdown_status_time, timeText)
    }
}

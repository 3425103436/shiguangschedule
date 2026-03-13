package com.xingheyuzhuan.shiguangschedule.data.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * 周次计算工具，统一各处重复的日期格式化与周次对齐逻辑。
 */
object WeekCalculator {

    val DATE_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    /**
     * 将日期对齐到设置的一周起始日。
     */
    fun getStartDayOfWeek(date: LocalDate, firstDayOfWeekInt: Int): LocalDate {
        val firstDayOfWeek = DayOfWeek.of(firstDayOfWeekInt)
        return date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }

    /**
     * 根据学期开始日期和总周数，计算当前周数。
     * @param semesterStartDateStr 学期开始日期字符串，格式为 yyyy-MM-dd
     * @param totalWeeks 学期总周数
     * @param firstDayOfWeekInt 一周起始日 (1=MONDAY, 7=SUNDAY)
     * @return 当前周数 (从1开始)，如果不在学期内则返回 null
     */
    fun calculateCurrentWeek(
        semesterStartDateStr: String?,
        totalWeeks: Int,
        firstDayOfWeekInt: Int
    ): Int? {
        if (semesterStartDateStr.isNullOrEmpty() || totalWeeks <= 0) return null

        return try {
            val semesterStartDate = LocalDate.parse(semesterStartDateStr, DATE_FORMATTER)
            val alignedStartDate = getStartDayOfWeek(semesterStartDate, firstDayOfWeekInt)
            val alignedToday = getStartDayOfWeek(LocalDate.now(), firstDayOfWeekInt)

            if (alignedToday.isBefore(alignedStartDate)) return null

            val diffWeeks = ChronoUnit.WEEKS.between(alignedStartDate, alignedToday).toInt()
            val calculatedWeek = diffWeeks + 1

            if (calculatedWeek in 1..totalWeeks) calculatedWeek else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

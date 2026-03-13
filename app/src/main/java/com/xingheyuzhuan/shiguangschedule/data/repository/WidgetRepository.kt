package com.xingheyuzhuan.shiguangschedule.data.repository

import android.content.Context
import com.xingheyuzhuan.shiguangschedule.data.db.widget.WidgetCourse
import com.xingheyuzhuan.shiguangschedule.data.db.widget.WidgetCourseDao
import com.xingheyuzhuan.shiguangschedule.data.db.widget.WidgetAppSettingsDao
import com.xingheyuzhuan.shiguangschedule.data.db.widget.WidgetAppSettings
import com.xingheyuzhuan.shiguangschedule.data.util.WeekCalculator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.DayOfWeek

/**
 * Widget 数据仓库，负责处理与 Widget 数据库相关的所有数据操作。
 */
class WidgetRepository(
    private val widgetCourseDao: WidgetCourseDao,
    private val widgetAppSettingsDao: WidgetAppSettingsDao,
    private val context: Context
) {
    // 创建一个 Channel，用于发送数据更新事件。
    private val _dataUpdatedChannel = Channel<Unit>(Channel.CONFLATED)
    val dataUpdatedFlow: Flow<Unit> = _dataUpdatedChannel.receiveAsFlow()

    /**
     * 获取指定日期范围内的 Widget 课程。
     */
    fun getWidgetCoursesByDateRange(startDate: String, endDate: String): Flow<List<WidgetCourse>> {
        return widgetCourseDao.getWidgetCoursesByDateRange(startDate, endDate)
    }

    /**
     * 批量插入或更新 Widget 课程。
     */
    suspend fun insertAll(courses: List<WidgetCourse>) {
        widgetCourseDao.insertAll(courses)
        _dataUpdatedChannel.trySend(Unit)
    }

    /**
     * 删除所有课程。
     */
    suspend fun deleteAll() {
        widgetCourseDao.deleteAll()
        _dataUpdatedChannel.trySend(Unit)
    }

    /**
     * 插入或更新小组件设置（WidgetAppSettings）。
     */
    suspend fun insertOrUpdateAppSettings(settings: WidgetAppSettings) {
        widgetAppSettingsDao.insertOrUpdate(settings) // 调用 Dao 的 insertOrUpdate 方法
        _dataUpdatedChannel.trySend(Unit) // 通知监听者数据已更新
    }

    /**
     * 获取小组件设置的数据流。
     * 返回类型已修正为 WidgetAppSettings，匹配 widgetAppSettingsDao 的实际返回。
     */
    fun getAppSettingsFlow(): Flow<WidgetAppSettings?> {
        // 由于 widgetAppSettingsDao 的定义是 getAppSettings(): Flow<WidgetAppSettings?>
        // 这里可以直接返回，不再需要类型转换或 Suppress
        return widgetAppSettingsDao.getAppSettings()
    }

    /**
     * 计算并发出当前周数，它是一个数据流。
     */
    fun getCurrentWeekFlow(): Flow<Int?> {
        return widgetAppSettingsDao.getAppSettings()
            .map { settings ->
                val totalWeeks = settings?.semesterTotalWeeks ?: 0
                val startDate = settings?.semesterStartDate
                val firstDayOfWeek = settings?.firstDayOfWeek ?: DayOfWeek.MONDAY.value

                WeekCalculator.calculateCurrentWeek(startDate, totalWeeks, firstDayOfWeek)
            }
    }
}
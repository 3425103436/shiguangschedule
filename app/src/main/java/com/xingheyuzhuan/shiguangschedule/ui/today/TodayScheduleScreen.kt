package com.xingheyuzhuan.shiguangschedule.ui.today

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.xingheyuzhuan.shiguangschedule.ui.components.BottomNavigationBar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.xingheyuzhuan.shiguangschedule.R
import com.xingheyuzhuan.shiguangschedule.data.model.ScheduleGridStyle
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScheduleScreen(
    navController: NavHostController,
    viewModel: TodayScheduleViewModel = viewModel(
        factory = TodayScheduleViewModel.TodayScheduleViewModelFactory(
            application = LocalContext.current.applicationContext as Application
        )
    )
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val semesterStatus by viewModel.semesterStatus.collectAsState()
    val todayCourses by viewModel.todayCourses.collectAsState()
    val gridStyle by viewModel.gridStyle.collectAsState()
    val isDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.title_today_schedule),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = semesterStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            val today = LocalDate.now()
            val todayDateString = remember(today) {
                today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
            }
            val todayDayOfWeekString = remember(today) {
                today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            }

            // 日期头部卡片
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = todayDateString,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = todayDayOfWeekString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${todayCourses.size} 节课",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (todayCourses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎉",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.text_no_courses_today),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "好好休息，享受今天吧",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                val currentTime = LocalTime.now()

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(
                        items = todayCourses,
                        key = { _, course -> "${course.name}_${course.startTime}" }
                    ) { index, course ->
                        // 逐个入场动画
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(index * 50L)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            val isCourseFinished = remember(currentTime, course) {
                                try {
                                    val courseEndTime = LocalTime.parse(course.endTime)
                                    currentTime.isAfter(courseEndTime)
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            val isCurrentlyInClass = remember(currentTime, course) {
                                try {
                                    val courseStartTime = LocalTime.parse(course.startTime)
                                    val courseEndTime = LocalTime.parse(course.endTime)
                                    currentTime in courseStartTime..courseEndTime
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            val colorPair = gridStyle.courseColorMaps.getOrElse(course.colorInt) {
                                ScheduleGridStyle.DEFAULT_COLOR_MAPS[0]
                            }
                            val baseColor = if (isDark) colorPair.dark else colorPair.light
                            val cardColor = when {
                                isCourseFinished -> baseColor.copy(alpha = 0.35f)
                                isCurrentlyInClass -> baseColor
                                else -> baseColor.copy(alpha = 0.85f)
                            }
                            val contentColor = if (isDark) Color.White else Color.Black

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        // 已结束的课程稍微缩小，增加层次感
                                        if (isCourseFinished) {
                                            scaleX = 0.98f
                                            scaleY = 0.98f
                                            alpha = 0.75f
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = cardColor,
                                    contentColor = contentColor
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = when {
                                        isCurrentlyInClass -> 6.dp
                                        isCourseFinished -> 0.dp
                                        else -> 3.dp
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    // 状态标签
                                    if (isCurrentlyInClass) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = contentColor.copy(alpha = 0.15f),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = "正在上课",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = contentColor
                                            )
                                        }
                                    }

                                    // 课程名称
                                    Text(
                                        text = course.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            textDecoration = if (isCourseFinished) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // 时间行
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = contentColor.copy(alpha = 0.7f)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${course.startTime} - ${course.endTime}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = contentColor.copy(alpha = 0.8f)
                                        )

                                        // 倒计时提示
                                        if (isCurrentlyInClass) {
                                            try {
                                                val endTime = LocalTime.parse(course.endTime)
                                                val remaining = Duration.between(currentTime, endTime)
                                                val mins = remaining.toMinutes()
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "还剩 ${mins} 分钟",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = contentColor.copy(alpha = 0.6f)
                                                )
                                            } catch (_: Exception) {}
                                        }
                                    }

                                    // 地点行
                                    course.position.takeIf { it.isNotBlank() }?.let { position ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = contentColor.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = position,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = contentColor.copy(alpha = 0.8f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    // 教师行
                                    course.teacher.takeIf { it.isNotBlank() }?.let { teacher ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = contentColor.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = teacher,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = contentColor.copy(alpha = 0.8f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

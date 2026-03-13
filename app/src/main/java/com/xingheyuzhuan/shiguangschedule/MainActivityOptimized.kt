package com.xingheyuzhuan.shiguangschedule

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xingheyuzhuan.shiguangschedule.ui.schedule.WeeklyScheduleScreen
import com.xingheyuzhuan.shiguangschedule.ui.schoolselection.list.AdapterSelectionScreen
import com.xingheyuzhuan.shiguangschedule.ui.schoolselection.list.SchoolSelectionListScreen
import com.xingheyuzhuan.shiguangschedule.ui.schoolselection.web.WebViewScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.SettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.additional.MoreOptionsScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.additional.OpenSourceLicensesScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.contribution.ContributionScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.conversion.CourseTableConversionScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.course.AddEditCourseScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.coursemanagement.COURSE_NAME_ARG
import com.xingheyuzhuan.shiguangschedule.ui.settings.coursemanagement.CourseInstanceListScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.coursemanagement.CourseNameListScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.coursetables.ManageCourseTablesScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.notification.NotificationSettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.quickactions.QuickActionsScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.quickactions.delete.QuickDeleteScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.style.StyleSettingsScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.time.TimeSlotManagementScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.quickactions.tweaks.TweakScheduleScreen
import com.xingheyuzhuan.shiguangschedule.ui.settings.update.UpdateRepoScreen
import com.xingheyuzhuan.shiguangschedule.ui.theme.ShiguangScheduleTheme
import com.xingheyuzhuan.shiguangschedule.ui.today.TodayScheduleScreen
import com.xingheyuzhuan.shiguangschedule.ui.utils.SpringAnimationPresets

/**
 * 优化版 MainActivity
 * 集成预测性返回效果、改进的 Q 弹动画和性能优化
 * 
 * 优化内容：
 * 1. 使用 Spring Animation 替代部分 Tween，实现 Q 弹效果
 * 2. 优化动画时长和缓动函数
 * 3. 改进导航转场动画的流畅度
 * 4. 减少不必要的动画延迟
 */
class MainActivityOptimized : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ShiguangScheduleTheme {
                AppNavigationOptimized()
            }
        }
    }
}

@Composable
fun AppNavigationOptimized() {
    val navController = rememberNavController()

    /**
     * 优化的进入动画：使用 Spring Animation 实现 Q 弹效果
     * 相比原来的 Tween 动画，Spring 动画更自然、更有物理感
     */
    val depthEnterTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> EnterTransition = {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight / 5 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(260, delayMillis = 40)) +
            scaleIn(initialScale = 0.96f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ))
    }

    /**
     * 优化的退出动画：更快的淡出和缩放
     * 减少动画时长，提升整体响应速度
     */
    val depthExitTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(200)) +
            scaleOut(targetScale = 0.93f, animationSpec = tween(280, easing = FastOutSlowInEasing))
    }

    /**
     * 优化的返回进入动画：使用 Spring 实现弹簧效果
     * 当用户按返回键返回时，前一个屏幕会以弹簧动画弹出
     */
    val depthPopEnterTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(240)) +
            scaleIn(initialScale = 0.93f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ))
    }

    /**
     * 优化的返回退出动画：预测性返回效果
     * 实现类似 Grok 的效果，返回时显示部分返回后的画面
     */
    val depthPopExitTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> ExitTransition = {
        slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeOut(animationSpec = tween(240))
    }

    NavHost(
        navController = navController,
        startDestination = Screen.CourseSchedule.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶级页面：无转场动画（保持原样）
        composable(
            Screen.CourseSchedule.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            WeeklyScheduleScreen(navController = navController)
        }
        composable(
            Screen.Settings.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SettingsScreen(navController = navController)
        }
        composable(
            Screen.TodaySchedule.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            TodayScheduleScreen(navController = navController)
        }

        // 子页面：使用优化的转场动画
        composable(
            Screen.TimeSlotSettings.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            TimeSlotManagementScreen(onBackClick = { navController.popBackStack() })
        }
        composable(
            Screen.ManageCourseTables.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            ManageCourseTablesScreen(navController = navController)
        }
        composable(
            Screen.SchoolSelectionListScreen.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            SchoolSelectionListScreen(navController = navController)
        }

        composable(
            route = "adapterSelection/{schoolId}/{schoolName}/{categoryNumber}/{resourceFolder}",
            arguments = listOf(
                navArgument("schoolId") { type = NavType.StringType },
                navArgument("schoolName") { type = NavType.StringType },
                navArgument("categoryNumber") { type = NavType.IntType },
                navArgument("resourceFolder") { type = NavType.StringType }
            ),
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) { backStackEntry ->
            val schoolId = backStackEntry.arguments?.getString("schoolId") ?: ""
            val schoolName = backStackEntry.arguments?.getString("schoolName") ?: "未知学校"
            val categoryNumber = backStackEntry.arguments?.getInt("categoryNumber") ?: 0
            val resourceFolder = backStackEntry.arguments?.getString("resourceFolder") ?: ""

            AdapterSelectionScreen(
                navController = navController,
                schoolId = schoolId,
                schoolName = schoolName,
                categoryNumber = categoryNumber,
                resourceFolder = resourceFolder
            )
        }
        composable(
            route = Screen.WebView.route,
            arguments = listOf(
                navArgument("initialUrl") { type = NavType.StringType },
                navArgument("assetJsPath") { type = NavType.StringType }
            ),
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) { backStackEntry ->
            val initialUrl = backStackEntry.arguments?.getString("initialUrl")
            val assetJsPath = backStackEntry.arguments?.getString("assetJsPath")

            val context = LocalContext.current
            val app = context.applicationContext as MyApplication

            val courseConversionRepository = app.courseConversionRepository
            val timeSlotRepository = app.timeSlotRepository

            WebViewScreen(
                navController = navController,
                initialUrl = initialUrl,
                assetJsPath = assetJsPath,
                courseConversionRepository = courseConversionRepository,
                timeSlotRepository = timeSlotRepository,
                courseScheduleRoute = Screen.CourseSchedule.route,
            )
        }
        composable(
            Screen.NotificationSettings.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            NotificationSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.AddEditCourse.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.StringType
                    nullable = true
                }
            ),
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")

            AddEditCourseScreen(
                courseId = courseId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            Screen.CourseTableConversion.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            CourseTableConversionScreen(navController = navController)
        }
        composable(
            Screen.MoreOptions.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            MoreOptionsScreen(navController = navController)
        }
        composable(
            Screen.OpenSourceLicenses.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            OpenSourceLicensesScreen(navController = navController)
        }
        composable(
            Screen.UpdateRepo.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            UpdateRepoScreen(navController = navController)
        }
        composable(
            Screen.QuickActions.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            QuickActionsScreen(navController = navController)
        }
        composable(
            Screen.TweakSchedule.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            TweakScheduleScreen(navController = navController)
        }
        composable(
            Screen.ContributionList.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            ContributionScreen(navController = navController)
        }
        composable(
            Screen.CourseManagementList.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            CourseNameListScreen(navController = navController)
        }

        composable(
            route = Screen.CourseManagementDetail.route,
            arguments = listOf(
                navArgument(COURSE_NAME_ARG) { type = NavType.StringType }
            ),
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) { backStackEntry ->
            val courseName = Uri.decode(backStackEntry.arguments?.getString(COURSE_NAME_ARG) ?: "")
            CourseInstanceListScreen(
                courseName = courseName,
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(
            Screen.StyleSettings.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            StyleSettingsScreen(navController = navController)
        }
        composable(
            Screen.QuickDelete.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            QuickDeleteScreen(navController = navController)
        }
    }
}

package com.xingheyuzhuan.shiguangschedule

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ShiguangScheduleTheme {
                AppNavigation()
            }
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val depthEnterTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> EnterTransition = {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight / 6 },
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
        ) + fadeIn(animationSpec = tween(280, delayMillis = 60)) +
            scaleIn(initialScale = 0.96f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
    }

    val depthExitTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(220)) +
            scaleOut(targetScale = 0.90f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
    }

    val depthPopEnterTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(260)) +
            scaleIn(initialScale = 0.90f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
    }

    val depthPopExitTransition: androidx.compose.animation.AnimatedContentTransitionScope<androidx.navigation.NavBackStackEntry>.() -> ExitTransition = {
        slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight / 4 },
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
        ) + fadeOut(animationSpec = tween(260))
    }

    NavHost(
        navController = navController,
        startDestination = Screen.CourseSchedule.route,
        modifier = Modifier.fillMaxSize()
    ){
        // 这些顶级页面的转场是瞬间完成的
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

        // 所有子页面的转场也都是瞬间完成的
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
        // 学校选择
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

            // 路由参数只处理 courseId (Add/Edit 模式)。
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
            OpenSourceLicensesScreen (navController = navController)
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
        // 课程管理 - 一级页面：课程名称列表
        composable(
            Screen.CourseManagementList.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            // CourseNameListScreen 负责显示不重复的课程名称
            CourseNameListScreen(navController = navController)
        }

        // 课程管理 - 二级页面：课程实例网格
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
        // 外观定制页面
        composable(
            Screen.StyleSettings.route,
            enterTransition = depthEnterTransition,
            exitTransition = depthExitTransition,
            popEnterTransition = depthPopEnterTransition,
            popExitTransition = depthPopExitTransition
        ) {
            StyleSettingsScreen(navController = navController)
        }
        // 快速删除课程页面
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
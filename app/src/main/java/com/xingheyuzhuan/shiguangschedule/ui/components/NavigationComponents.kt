package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.R

/**
 * WakeUp 风格的底部导航栏：悬浮胶囊、低对比度背景和轻量选中指示器。
 */
@Composable
fun BottomNavigationBar(
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    isTransparent: Boolean = false,
    contentColor: Color? = null
) {
    val navItems = listOf(
        Triple(stringResource(R.string.nav_today_schedule), Destination.TodaySchedule, Icons.Filled.ViewAgenda to Icons.Outlined.ViewAgenda),
        Triple(stringResource(R.string.nav_course_schedule), Destination.CourseSchedule, Icons.Filled.ViewWeek to Icons.Outlined.ViewWeek),
        Triple(stringResource(R.string.nav_settings), Destination.Settings, Icons.Filled.AccountCircle to Icons.Outlined.AccountCircle)
    )

    val finalContentColor = contentColor ?: MaterialTheme.colorScheme.onSurface
    val finalSubTextColor = finalContentColor.copy(alpha = 0.58f)
    val pillShape = RoundedCornerShape(28.dp)

    NavigationBar(
        containerColor = if (isTransparent) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        },
        tonalElevation = 0.dp,
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(pillShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                shape = pillShape
            )
    ) {
        navItems.forEach { (label, destination, icons) ->
            val isSelected = currentDestination::class == destination::class

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onTabSelected(destination)
                    }
                },
                icon = {
                    val (selectedIcon, unselectedIcon) = icons
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = if (isSelected) 1f else 0f
                    ),
                    selectedIconColor = if (contentColor != null) {
                        finalContentColor
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    },
                    selectedTextColor = if (contentColor != null) {
                        finalContentColor
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    unselectedIconColor = if (contentColor != null) {
                        finalSubTextColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    unselectedTextColor = if (contentColor != null) {
                        finalSubTextColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    MaterialTheme {
        BottomNavigationBar(
            currentDestination = Destination.Settings,
            onTabSelected = {}
        )
    }
}

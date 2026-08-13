package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
 * iOS Liquid Glass 风格底部导航：低高度悬浮玻璃胶囊，选中项使用内层透明玻璃。
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
    val finalSubTextColor = finalContentColor.copy(alpha = 0.52f)
    val pillShape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 9.dp)
            .height(66.dp)
            .liquidGlass(
                tint = if (isTransparent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = pillShape,
                shadowElevation = 10.dp
            )
            .padding(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            navItems.forEach { (label, destination, icons) ->
                val isSelected = currentDestination::class == destination::class
                val itemShape = RoundedCornerShape(24.dp)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(itemShape)
                        .background(
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.46f)
                            } else {
                                Color.Transparent
                            },
                            shape = itemShape
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.52f),
                                    shape = itemShape
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable {
                            if (!isSelected) onTabSelected(destination)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val (selectedIcon, unselectedIcon) = icons
                        Icon(
                            imageVector = if (isSelected) selectedIcon else unselectedIcon,
                            contentDescription = label,
                            modifier = Modifier.size(21.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else finalSubTextColor
                        )
                    }
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = if (isSelected) finalContentColor else finalSubTextColor,
                        modifier = Modifier.padding(bottom = 5.dp, top = 2.dp)
                    )
                }
            }
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

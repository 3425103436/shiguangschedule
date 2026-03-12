package com.xingheyuzhuan.shiguangschedule.ui.transition

import android.app.Activity
import com.xingheyuzhuan.shiguangschedule.R

/**
 * Activity 场景下的景深转场（用于 overridePendingTransition）。
 */
fun Activity.applyDepthOpenTransition() {
    overridePendingTransition(R.anim.slide_in_up, R.anim.scale_down_back)
}

fun Activity.applyDepthCloseTransition() {
    overridePendingTransition(R.anim.scale_restore_front, R.anim.slide_out_down)
}

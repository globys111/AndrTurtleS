package com.rebloom.app.ui.components

import androidx.annotation.DrawableRes
import com.rebloom.app.R
import com.rebloom.app.domain.model.TaskStatus
import com.rebloom.app.domain.model.TaskType

@DrawableRes
fun taskIconRes(type: TaskType, status: TaskStatus, isCompleted: Boolean): Int {
    return when (type) {
        TaskType.WATER -> when {
            isCompleted -> R.drawable.ic_water_done
            status == TaskStatus.OVERDUE -> R.drawable.ic_water_overdue
            status == TaskStatus.TODAY -> R.drawable.ic_water_today
            else -> R.drawable.ic_water_future
        }
        TaskType.REPOT -> when {
            isCompleted -> R.drawable.ic_replant_done
            status == TaskStatus.OVERDUE -> R.drawable.ic_replant_overdue
            status == TaskStatus.TODAY -> R.drawable.ic_replant_today
            else -> R.drawable.ic_replant_future
        }
        TaskType.LIGHT -> when {
            isCompleted -> R.drawable.ic_light_done
            status == TaskStatus.OVERDUE -> R.drawable.ic_light_overdue
            status == TaskStatus.TODAY -> R.drawable.ic_light_today
            else -> R.drawable.ic_light_future
        }
    }
}
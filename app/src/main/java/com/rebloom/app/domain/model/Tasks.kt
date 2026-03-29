package com.rebloom.app.domain.model

import androidx.annotation.DrawableRes

data class TaskItem(
    val id: Int,
    val title: String,
    val plantName: String,
    @DrawableRes val imageResId: Int
)
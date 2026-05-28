package com.rebloom.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.rebloom.app.R

data class BottomItem(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
)

val bottomItems = listOf(
    BottomItem(MainDestinations.Tasks.route,     R.string.tasks_title,     R.drawable.ic_tasks_selected,     R.drawable.ic_tasks_not_selected),
    BottomItem(MainDestinations.Home.route,      R.string.home_title,      R.drawable.ic_home_selected,      R.drawable.ic_home_not_selected),
    BottomItem(MainDestinations.Plants.route,    R.string.plants_title,    R.drawable.ic_plants_selected,    R.drawable.ic_plants_not_selected)
)

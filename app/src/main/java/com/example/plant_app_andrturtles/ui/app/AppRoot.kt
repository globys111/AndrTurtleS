package com.example.plant_app_andrturtles.ui.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.example.plant_app_andrturtles.ui.navigation.*
import com.example.plant_app_andrturtles.ui.screens.*
import com.example.plant_app_andrturtles.domain.model.Plant
import com.example.plant_app_andrturtles.ui.screens.home.HomeScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    val showBottomBar = current in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFE9EED9)
                ) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(AppRoute.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(item.labelRes),
                                    fontSize = 13.sp
                                )
                            },
                            icon = {
                                Image(
                                    painter = painterResource(
                                        id = if (current == item.route) item.selectedIcon else item.unselectedIcon
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppRoute.Home.route) {
                HomeScreen(
                    onOpenProfile = { navController.navigate(AppRoute.Profile.route) },
                    onOpenPlant = { plant ->
                        // Parcelable передача через SavedStateHandle
                        navController.currentBackStackEntry?.savedStateHandle?.set("plant", plant)
                        navController.navigate(AppRoute.PlantDetails.route)
                    }
                )
            }

            composable(AppRoute.Profile.route) {
                ProfileScreen(onBack = { navController.popBackStack() })
            }

            composable(AppRoute.PlantDetails.route) {
                val plant = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Plant>("plant")

                PlantDetailsScreen(
                    plant = plant,
                    onBack = { navController.popBackStack() }
                )
            }

            // заглушки для нижних вкладок (чтобы BottomNav работал как в макете)
            composable(AppRoute.Tasks.route) { PlaceholderScreen(titleRes = com.example.plant_app_andrturtles.R.string.tasks_title) }
            composable(AppRoute.Plants.route) { PlaceholderScreen(titleRes = com.example.plant_app_andrturtles.R.string.plants_title) }
            composable(AppRoute.Articles.route) { PlaceholderScreen(titleRes = com.example.plant_app_andrturtles.R.string.articles_title) }
            composable(AppRoute.Community.route) { PlaceholderScreen(titleRes = com.example.plant_app_andrturtles.R.string.community_title) }
        }
    }
}

package com.example.plant_app_andrturtles.ui.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.example.plant_app_andrturtles.R
import com.example.plant_app_andrturtles.ui.navigation.*
import com.example.plant_app_andrturtles.ui.screens.*
import com.example.plant_app_andrturtles.ui.screens.auth.AuthScreen
import com.example.plant_app_andrturtles.ui.screens.auth.RegisterScreen
import com.example.plant_app_andrturtles.domain.model.Plant
import com.example.plant_app_andrturtles.ui.screens.home.HomeScreen
import com.example.plant_app_andrturtles.ui.screens.plants.PlaceholderScreen
import com.example.plant_app_andrturtles.ui.screens.plants.PlantDetailsScreen
import com.example.plant_app_andrturtles.ui.screens.plants.PlantsScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    var isLoggedIn by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (isLoggedIn && current in bottomItems.map { it.route }) {
                NavigationBar(
                    containerColor = Color(0xFFE9EED9)
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
            startDestination = if (isLoggedIn) AppRoute.Home.route else "auth",
            modifier = Modifier.padding(padding)
        ) {
            composable("auth") {
                AuthScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onNavigateToForgotPassword = {
                        // TODO: добавить экран восстановления пароля
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        isLoggedIn = true
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(AppRoute.Home.route) {
                HomeScreen(
                    onOpenProfile = { navController.navigate(AppRoute.Profile.route) },
                    onOpenPlant = { plant ->
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

                if (plant != null) {
                    PlantDetailsScreen(
                        plant = plant,
                        onBack = {
                            navController.currentBackStackEntry?.savedStateHandle?.remove<Plant>("plant")
                            navController.popBackStack()
                        },
                        onEdit = {}
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable(AppRoute.Tasks.route) {
                PlaceholderScreen(titleRes = R.string.tasks_title)
            }

            composable(AppRoute.Plants.route) {
                PlantsScreen(
                    onPlantClick = { plant ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("plant", plant)
                        navController.navigate(AppRoute.PlantDetails.route)
                    },
                    onAddPlant = {}
                )
            }

            composable(AppRoute.Articles.route) {
                PlaceholderScreen(titleRes = R.string.articles_title)
            }

            composable(AppRoute.Community.route) {
                PlaceholderScreen(titleRes = R.string.community_title)
            }
        }
    }
}
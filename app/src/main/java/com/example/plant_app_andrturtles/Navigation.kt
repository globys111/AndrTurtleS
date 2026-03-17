package com.example.plant_app_andrturtles.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plant_app_andrturtles.ScrollScreen
import com.example.plant_app_andrturtles.ScrollScreenRed

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            ScrollScreen(
                onOverdueClick = {
                    navController.navigate("overdue")
                }
            )
        }

        composable("overdue") {
            ScrollScreenRed(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
package com.rebloom.app.ui.navigation

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.rebloom.app.R
import com.rebloom.app.domain.model.Article
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.ui.screens.articles.ArticleDetailsScreen
import com.rebloom.app.ui.screens.articles.ArticlesScreen
import com.rebloom.app.ui.screens.auth.AuthViewModel
import com.rebloom.app.ui.screens.auth.ForgotPasswordScreen
import com.rebloom.app.ui.screens.auth.LoginScreen
import com.rebloom.app.ui.screens.auth.RegisterScreen
import com.rebloom.app.ui.screens.community.CommunityScreen
import com.rebloom.app.ui.screens.home.HomeScreen
import com.rebloom.app.ui.screens.plants.PlantDetailsScreen
import com.rebloom.app.ui.screens.plants.PlantsScreen
import com.rebloom.app.ui.screens.profile.ProfileScreen
import com.rebloom.app.ui.screens.tasks.TasksScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    var isLoggedIn by remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            isLoggedIn = true
        }
    }

    val startDestination = if (isLoggedIn) AppRoute.Home.route else "auth"
    val showBottomBar = isLoggedIn && current in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable("auth") {
                LoginScreen(
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
                        navController.navigate("forgot_password")
                    },
                    viewModel = authViewModel
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
                    },
                    viewModel = authViewModel
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    viewModel = authViewModel
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
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable(AppRoute.Tasks.route) {
                TasksScreen(
                    onOverdueClick = {

                    }
                )
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
                ArticlesScreen(
                    onArticleClick = { article ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                        navController.navigate(AppRoute.ArticleDetails.route)
                    }
                )
            }

            composable(AppRoute.ArticleDetails.route) {
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article>("article")
                if (article != null) {
                    ArticleDetailsScreen(
                        article = article,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable(AppRoute.Community.route) {
                CommunityScreen()
            }
        }
    }
}
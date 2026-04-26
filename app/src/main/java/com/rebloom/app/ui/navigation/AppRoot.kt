package com.rebloom.app.ui.navigation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
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

private const val NAV_TAG = "NAV"

@Composable
fun AppRoot(deepLinkUri: Uri? = null) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Проверяем сохранённую сессию при старте (только если нет deep link)
    LaunchedEffect(Unit) {
        if (deepLinkUri == null) authViewModel.checkSession()
    }

    // Обрабатываем deep link
    LaunchedEffect(deepLinkUri) {
        deepLinkUri?.let { authViewModel.handleDeepLink(it) }
    }

    // Recovery deep link → экран смены пароля
    LaunchedEffect(authState.isPasswordRecovery) {
        if (authState.isPasswordRecovery) {
            authViewModel.clearPasswordRecovery()
            navController.navigate(AuthDestinations.NewPassword.route) {
                popUpTo(AuthDestinations.Login.route) { inclusive = false }
            }
        }
    }

    // Ждём завершения инициализации (проверка сессии / обработка deep link)
    if (authState.isInitializing) return

    // Централизованная навигация на главный экран при входе
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            val stack = navController.currentBackStack.value.map { it.destination.route }
            Log.d(NAV_TAG, "isLoggedIn=true → navigate MAIN, currentStack=$stack")
            navController.navigate(MAIN_ROUTE) {
                // Убираем всё что есть в стеке, кроме корня
                stack.filterNotNull().forEach { route ->
                    Log.d(NAV_TAG, "popping route=$route")
                }
                val rootEntry = navController.currentBackStack.value.firstOrNull()
                if (rootEntry != null) {
                    popUpTo(rootEntry.destination.id) { inclusive = false }
                }
                launchSingleTop = true
            }
        }
    }

    // startDestination фиксируется ОДИН РАЗ при первой отрисовке после инициализации.
    // Нельзя пересчитывать при каждой рекомпозиции — NavHost сбрасывает стек при изменении startDestination.
    val startDestination = remember { if (authState.isLoggedIn) MAIN_ROUTE else AUTH_ROUTE }
    Log.d(NAV_TAG, "compose startDestination=$startDestination current=$current isLoggedIn=${authState.isLoggedIn}")

    val showBottomBar = current in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color(0xFFE9EED9)) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(MainDestinations.Home.route) { saveState = true }
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
            // ── Auth Graph ───────────────────────────────────────────────
            navigation(startDestination = AuthDestinations.Login.route, route = AUTH_ROUTE) {

                composable(AuthDestinations.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            Log.d(NAV_TAG, "onLoginSuccess callback fired (navigation handled by LaunchedEffect)")
                        },
                        onNavigateToRegister = {
                            Log.d(NAV_TAG, "Login → Register")
                            navController.navigate(AuthDestinations.Register.route)
                        },
                        onNavigateToForgotPassword = {
                            Log.d(NAV_TAG, "Login → ForgotPass")
                            navController.navigate(AuthDestinations.ForgotPass.route)
                        },
                        viewModel = authViewModel
                    )
                }

                composable(AuthDestinations.Register.route) {
                    RegisterScreen(
                        onRegisterSuccess = {
                            Log.d(NAV_TAG, "onRegisterSuccess callback fired (navigation handled by LaunchedEffect)")
                        },
                        onNavigateToLogin = {
                            Log.d(NAV_TAG, "Register → popBackStack to Login")
                            navController.popBackStack()
                        },
                        viewModel = authViewModel
                    )
                }

                composable(AuthDestinations.ForgotPass.route) {
                    ForgotPasswordScreen(
                        onNavigateToLogin = {
                            Log.d(NAV_TAG, "ForgotPass → popBackStack")
                            navController.popBackStack()
                        },
                        viewModel = authViewModel
                    )
                }

                composable(AuthDestinations.NewPassword.route) {
                    ForgotPasswordScreen(
                        isRecoveryMode = true,
                        onPasswordUpdated = {
                            Log.d(NAV_TAG, "onPasswordUpdated callback fired (navigation handled by LaunchedEffect)")
                        },
                        viewModel = authViewModel
                    )
                }
            }

            // ── Main Graph ───────────────────────────────────────────────
            navigation(startDestination = MainDestinations.Home.route, route = MAIN_ROUTE) {

                composable(MainDestinations.Home.route) {
                    HomeScreen(
                        onOpenProfile = { navController.navigate(MainDestinations.Profile.route) },
                        onOpenPlant = { plant ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("plant", plant)
                            navController.navigate(MainDestinations.PlantDetails.route)
                        }
                    )
                }

                composable(MainDestinations.Profile.route) {
                    ProfileScreen(onBack = { navController.popBackStack() })
                }

                composable(MainDestinations.PlantDetails.route) {
                    val plant = remember {
                        navController.previousBackStackEntry?.savedStateHandle?.get<Plant>("plant")
                    }
                    if (plant != null) {
                        PlantDetailsScreen(
                            plant = plant,
                            onBack = {
                                navController.popBackStack()
                            },
                            onEdit = {}
                        )
                    } else {
                        LaunchedEffect(Unit) { navController.popBackStack() }
                    }
                }

                composable(MainDestinations.Tasks.route) {
                    TasksScreen(onOverdueClick = {})
                }

                composable(MainDestinations.Plants.route) {
                    PlantsScreen(
                        onPlantClick = { plant ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("plant", plant)
                            navController.navigate(MainDestinations.PlantDetails.route)
                        },
                        onAddPlant = {}
                    )
                }

                composable(MainDestinations.Articles.route) {
                    ArticlesScreen(
                        onArticleClick = { article ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                            navController.navigate(MainDestinations.ArticleDetails.route)
                        }
                    )
                }

                composable(MainDestinations.ArticleDetails.route) {
                    val article = remember {
                        navController.previousBackStackEntry?.savedStateHandle?.get<Article>("article")
                    }
                    if (article != null) {
                        ArticleDetailsScreen(article = article, onBack = { navController.popBackStack() })
                    } else {
                        LaunchedEffect(Unit) { navController.popBackStack() }
                    }
                }

                composable(MainDestinations.Community.route) {
                    CommunityScreen()
                }
            }
        }
    }
}

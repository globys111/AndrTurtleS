package com.rebloom.app.ui.navigation

sealed class AppRoute(val route: String) {
    data object Tasks : AppRoute("tasks")
    data object Plants : AppRoute("plants")
    data object Home : AppRoute("home")
    data object Articles : AppRoute("articles")

    data object ArticleDetails : AppRoute("article_details/{articleId}")
    data object Community : AppRoute("community")

    data object Profile : AppRoute("profile")
    data object PlantDetails : AppRoute("plant_details")
}

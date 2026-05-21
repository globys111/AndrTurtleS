package com.rebloom.app.ui.navigation

const val AUTH_ROUTE = "auth"
const val MAIN_ROUTE = "main"

sealed class AuthDestinations(val route: String) {
    data object Login       : AuthDestinations("${AUTH_ROUTE}_login")
    data object Register    : AuthDestinations("${AUTH_ROUTE}_register")
    data object ForgotPass  : AuthDestinations("${AUTH_ROUTE}_forgot")
    data object NewPassword : AuthDestinations("${AUTH_ROUTE}_new_password")
}

sealed class MainDestinations(val route: String) {
    data object Home          : MainDestinations("${MAIN_ROUTE}_home")
    data object Tasks         : MainDestinations("${MAIN_ROUTE}_tasks")
    data object Plants        : MainDestinations("${MAIN_ROUTE}_plants")
    data object Articles      : MainDestinations("${MAIN_ROUTE}_articles")
    data object Community     : MainDestinations("${MAIN_ROUTE}_community")
    data object Profile       : MainDestinations("${MAIN_ROUTE}_profile")
    data object PlantDetails  : MainDestinations("${MAIN_ROUTE}_plant_details")
    data object ArticleDetails: MainDestinations("${MAIN_ROUTE}_article_details")
    data object AddPlant : MainDestinations("${MAIN_ROUTE}_add_plant")
    data object EditProfile: MainDestinations("${MAIN_ROUTE}_edit_profile")
}

package com.example.navajasuiza.ui.navigation

sealed class AppScreen(val route: String) {
    object LoginScreen : AppScreen("login_screen")
    object RegistroScreen : AppScreen("registro_screen")
    object DashboardScreen : AppScreen("dashboard_screen") // Para el futuro
    object AboutScreen : AppScreen("about_screen")
}
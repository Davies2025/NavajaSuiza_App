package com.example.navajasuiza.ui.navigation

sealed class AppScreen(val route: String) {

    object LoginScreen : AppScreen("login_screen")

    object RegistroScreen : AppScreen("registro_screen")

    object DashboardScreen : AppScreen("dashboard_screen/{userId}") {
        fun createRoute(userId: Int): String = "dashboard_screen/$userId"
    }

    object AboutScreen : AppScreen("about_screen")

    object EstacionScreen : AppScreen("estacion_screen")

    object DatosClimaScreen : AppScreen("datos_clima_screen")

    object ProximityScreen : AppScreen("proximity_screen") //nueva
}







package com.example.navajasuiza.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navajasuiza.ui.screens.AboutScreen // <-- NUEVO IMPORT
import com.example.navajasuiza.ui.screens.LoginScreen
import com.example.navajasuiza.ui.screens.RegistroScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.LoginScreen.route
    ) {
        // Ruta para la pantalla de Login
        composable(route = AppScreen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        // Ruta para la pantalla de Registro
        composable(route = AppScreen.RegistroScreen.route) {
            RegistroScreen(navController = navController)
        }

        // Ruta para la pantalla principal (Dashboard) después del login
        composable(route = AppScreen.DashboardScreen.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Bienvenido! Dashboard en construcción.")
            }
        }

        // --- RUTA AÑADIDA PARA LA PANTALLA "ACERCA DE" ---
        composable(route = AppScreen.AboutScreen.route) {
            AboutScreen(navController = navController)
        }
    }
}
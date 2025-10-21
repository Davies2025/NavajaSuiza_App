package com.example.navajasuiza.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navajasuiza.ui.screens.AboutScreen
import com.example.navajasuiza.ui.screens.DashboardScreen
import com.example.navajasuiza.ui.screens.LoginScreen
import com.example.navajasuiza.ui.screens.RegistroScreen
import com.example.navajasuiza.ui.screens.EstacionScreen
import com.example.navajasuiza.ui.screens.DatosClimaScreen
import com.example.navajasuiza.ui.screens.ProximityScreen
import com.example.navajasuiza.ui.theme.NavajaSuizaTheme
import com.example.navajasuiza.ui.viewmodels.DashboardViewModel
import com.example.navajasuiza.ui.viewmodels.ThemeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application


    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.Factory(application))
    val themeUiState by themeViewModel.uiState.collectAsState()

    NavajaSuizaTheme(darkTheme = themeUiState.isDarkTheme) {
        NavHost(
            navController = navController,
            startDestination = AppScreen.LoginScreen.route
        ) {
            composable(route = AppScreen.LoginScreen.route) {
                LoginScreen(navController = navController)
            }

            composable(route = AppScreen.RegistroScreen.route) {
                RegistroScreen(navController = navController)
            }

            composable(
                route = AppScreen.DashboardScreen.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->


                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.Factory(
                        application,
                        LocalSavedStateRegistryOwner.current,
                        backStackEntry.arguments
                    )
                )
                DashboardScreen(navController = navController, viewModel = dashboardViewModel)
            }

            composable(route = AppScreen.AboutScreen.route) {
                AboutScreen(navController = navController)
            }

            composable(route = AppScreen.EstacionScreen.route) { // LA RUTA ES DEL 14 DE OCTUBRE

                EstacionScreen(
                    navController = navController,
                    themeViewModel = themeViewModel
                )
            }

            composable(route = AppScreen.DatosClimaScreen.route) {
                DatosClimaScreen(navController = navController)
            }

            composable(route = AppScreen.ProximityScreen.route) {
                ProximityScreen(navController = navController, themeViewModel = themeViewModel)
            }
        }
    }
}




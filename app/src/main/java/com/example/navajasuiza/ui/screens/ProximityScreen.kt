package com.example.navajasuiza.ui.screens

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.navajasuiza.R
import com.example.navajasuiza.ui.navigation.AppScreen
import com.example.navajasuiza.ui.viewmodels.ProximityViewModel
import com.example.navajasuiza.ui.viewmodels.ProximityViewModelFactory
import com.example.navajasuiza.ui.viewmodels.ThemeViewModel

@Composable
fun ProximityScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val factory = ProximityViewModelFactory(application)
    val proximityViewModel: ProximityViewModel = viewModel(factory = factory)

    val uiState by proximityViewModel.uiState.collectAsState()
    var returnButtonEnabled by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        themeViewModel.setAutomaticMode()
        onDispose {

        }
    }

    val indicatorText: String
    val indicatorColor: Color
    val indicatorImage: Int

    if (uiState.isObjectNear) {
        indicatorText = "CERCA"
        indicatorColor = Color.Red
        indicatorImage = R.drawable.advertencia
    } else {
        indicatorText = "LEJOS"
        indicatorColor = Color(0xFF4CAF50)
        indicatorImage = R.drawable.celular_lejos
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E4932))
            ) {
                Text(
                    text = "PROXIMIDAD",
                    modifier = Modifier.padding(horizontal = 50.dp, vertical = 18.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = "Indicaciones: Acerca un objeto con cuidado a la pantalla.",
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = indicatorImage),
                contentDescription = indicatorText,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = indicatorText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = indicatorColor
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    returnButtonEnabled = false
                    navController.popBackStack()
                },
                enabled = returnButtonEnabled,
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
            ) {
                Text("REGRESAR", color = Color.White)
            }
        }
    }
}






package com.example.navajasuiza.ui.screens

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.navajasuiza.R
import com.example.navajasuiza.ui.navigation.AppScreen
import com.example.navajasuiza.ui.viewmodels.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

private val GreenColor = Color(0xFF2E4117)
private val BlueColor = Color(0xFF1D294E)
private val RedColor = Color(0xFF351515)
private val BrownColor = Color(0xFF544516)
private val YellowColor = Color(0xFF635613)

@Composable
fun EstacionScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val application = LocalContext.current.applicationContext as Application
    val estacionViewModel: EstacionViewModel = viewModel(
        factory = viewModelFactory { EstacionViewModel(application) }
    )
    val context = LocalContext.current

    val uiState by estacionViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            estacionViewModel.onPermissionResult(isGranted)
        }
    )

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                estacionViewModel.loadApiData()
            }
        }
    )

    DisposableEffect(Unit) {
        themeViewModel.setManualMode()
        onDispose {
            themeViewModel.setAutomaticMode()
        }
    }

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(key1 = uiState.shouldRequestLocationEnable) {
        if (uiState.shouldRequestLocationEnable) {
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        locationSettingsLauncher.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {

                    }
                }
            }
            estacionViewModel.onLocationEnableRequestCompleted()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderCard()
            Spacer(modifier = Modifier.height(24.dp))
            WeatherDataCards(
                hardwareSensorData = uiState.hardwareSensorData,
                apiDataState = uiState.apiDataState
            )
            Spacer(modifier = Modifier.height(24.dp))
            ThemeToggle(
                isDarkTheme = themeUiState.isDarkTheme,
                onToggle = { themeViewModel.toggleTheme() }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ActionButtons(navController = navController)
        }
    }
}

@Composable
private fun WeatherDataCards(hardwareSensorData: HardwareSensorData, apiDataState: ApiDataState) {
    val temperatureValue = if (apiDataState is ApiDataState.Success) apiDataState.temperature else "Cargando"
    val humidityValue = if (apiDataState is ApiDataState.Success) apiDataState.humidity else "Cargando"
    val sunriseValue = if (apiDataState is ApiDataState.Success) apiDataState.sunrise else "Cargando"
    val sunsetValue = if (apiDataState is ApiDataState.Success) apiDataState.sunset else "Cargando"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard("Nivel de Humedad", humidityValue, R.drawable.humedad, GreenColor, Modifier.weight(1f))
            InfoCard("Presión del Aire", hardwareSensorData.pressure, R.drawable.calidad_del_aire, BlueColor, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard("Temperatura", temperatureValue, R.drawable.control_de_temperatura, RedColor, Modifier.weight(1f))
            InfoCard("Salida del Sol", sunriseValue, R.drawable.salida_del_sol, BrownColor, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LightInfoCard(
                title = "Luz Ambiental",
                value = hardwareSensorData.lightValue,
                description = hardwareSensorData.lightDescription,
                iconId = R.drawable.luz,
                cardColor = YellowColor,
                modifier = Modifier.weight(1f)
            )

            InfoCard("Puesta del Sol", sunsetValue, R.drawable.sol_tarde, BlueColor, Modifier.weight(1f))
        }

        when (apiDataState) {
            is ApiDataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
            is ApiDataState.Error -> {
                Text(
                    text = apiDataState.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = GreenColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ESTACIÓN\nMETEOROLÓGICA", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Image(painter = painterResource(id = R.drawable.estacion_meteorologica), contentDescription = "Estación", modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String, iconId: Int, cardColor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(120.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(painter = painterResource(id = iconId), contentDescription = title, modifier = Modifier.size(32.dp))

            Text(text = title, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)

            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun LightInfoCard(title: String, value: String, description: String, iconId: Int, cardColor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(120.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(painter = painterResource(id = iconId), contentDescription = title, modifier = Modifier.size(32.dp))

            Text(text = title, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

                Text(text = description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))

            }
        }
    }
}

@Composable
private fun ThemeToggle(isDarkTheme: Boolean, onToggle: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

        Text(if (isDarkTheme) "MODO OSCURO" else "MODO CLARO", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.width(16.dp))
        Switch(checked = isDarkTheme, onCheckedChange = { onToggle() })
    }
}

@Composable
private fun ActionButtons(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = { navController.navigate(AppScreen.DatosClimaScreen.route) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenColor)
        ) {
            Text("DATOS DEL CLIMA", color = Color.White, textAlign = TextAlign.Center, fontSize = 12.sp)
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrownColor)
        ) {
            Text("REGRESAR", color = Color.White, fontSize = 12.sp)
        }
    }
}


inline fun <reified T : ViewModel> viewModelFactory(crossinline factory: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <U : ViewModel> create(modelClass: Class<U>): U {
            return factory() as U
        }
    }
}



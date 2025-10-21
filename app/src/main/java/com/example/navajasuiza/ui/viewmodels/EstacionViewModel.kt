package com.example.navajasuiza.ui.viewmodels

import android.app.Application
import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.navajasuiza.sensors.SensorController
import com.example.navajasuiza.utils.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class HardwareSensorData(
    val pressure: String = "Cargando",
    val lightValue: String = "Cargando",
    val lightDescription: String = "Cargando"
)

sealed class ApiDataState {
    object Idle : ApiDataState()
    object Loading : ApiDataState()
    data class Success(
        val temperature: String,
        val humidity: String,
        val sunrise: String,
        val sunset: String
    ) : ApiDataState()
    data class Error(val message: String) : ApiDataState()
}

data class EstacionUiState(
    val hardwareSensorData: HardwareSensorData = HardwareSensorData(),
    val apiDataState: ApiDataState = ApiDataState.Idle,
    val shouldRequestLocationEnable: Boolean = false
)

class EstacionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EstacionUiState())
    val uiState: StateFlow<EstacionUiState> = _uiState.asStateFlow()

    private val locationProvider = LocationProvider(application)
    private val sensorController = SensorController(application)

    init {
        listenToAllSensors()
    }


    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            if (isLocationEnabled()) {
                loadApiData()
            } else {
                _uiState.update { it.copy(shouldRequestLocationEnable = true) }
            }
        } else {
            _uiState.update { it.copy(apiDataState = ApiDataState.Error("El permiso de ubicación es necesario.")) }
        }
    }


    fun loadApiData() {
        viewModelScope.launch {
            _uiState.update { it.copy(apiDataState = ApiDataState.Loading) }
            val location = locationProvider.getCurrentLocation()
            if (location == null) {
                _uiState.update { it.copy(apiDataState = ApiDataState.Error("No se pudo obtener la ubicación.\n¿GPS activado?")) }
                return@launch
            }

            val result = withContext(Dispatchers.IO) {
                try {
                    val apiKey = "5b8bb6a1d974644221e80a172fbf0e04"
                    val url = "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$apiKey&units=metric&lang=es"
                    val apiResultJson = URL(url).readText()
                    val json = JSONObject(apiResultJson)

                    val main = json.getJSONObject("main")
                    val sys = json.getJSONObject("sys")
                    val temp = main.getDouble("temp").toInt()

                    ApiDataState.Success(
                        temperature = "$temp °C",
                        humidity = "${main.getInt("humidity")} %",
                        sunrise = formatTime(sys.getLong("sunrise")),
                        sunset = formatTime(sys.getLong("sunset"))
                    )
                } catch (e: Exception) {
                    ApiDataState.Error("Sin conexión a internet.")
                }
            }
            _uiState.update { it.copy(apiDataState = result) }
        }
    }

    private fun listenToAllSensors() {
        sensorController.startListening()

        viewModelScope.launch {
            sensorController.pressureFlow.collect { pressure ->
                _uiState.update { it.copy(hardwareSensorData = it.hardwareSensorData.copy(pressure = "${pressure.toInt()} hPa")) }
            }
        }
        viewModelScope.launch {
            sensorController.lightSensorFlow.collect { lux ->
                val formattedValue = "${lux.toInt()} lux"
                val description = interpretLuxValue(lux)
                _uiState.update {
                    it.copy(
                        hardwareSensorData = it.hardwareSensorData.copy(
                            lightValue = formattedValue,
                            lightDescription = description
                        )
                    )
                }
            }
        }
    }

    private fun interpretLuxValue(lux: Float): String {
        return when (lux.toInt()) {
            0 -> "Oscuridad"
            in 1..50 -> "Muy oscuro"
            in 51..500 -> "Interior"
            in 501..4000 -> "Luz Fuerte"
            in 4001..15000 -> "Luz indirecta"
            else -> "Luz solar directa"
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = getApplication<Application>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    fun onLocationEnableRequestCompleted() {
        _uiState.update { it.copy(shouldRequestLocationEnable = false) }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        return sdf.format(date)
    }

    override fun onCleared() {
        super.onCleared()
        sensorController.stopListening()
    }
}



























package com.example.navajasuiza.ui.viewmodels

import android.app.Application
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

data class SensorData(
    val temperature: String = "PRUEBA",
    val pressure: String = "PRUEBA"
)


sealed class ApiDataState {
    object Idle : ApiDataState()
    object Loading : ApiDataState()
    data class Success(
        val humidity: String,
        val sunrise: String,
        val sunset: String
    ) : ApiDataState()
    data class Error(val message: String) : ApiDataState()
}


data class EstacionUiState(
    val sensorData: SensorData = SensorData(),
    val apiDataState: ApiDataState = ApiDataState.Idle
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
            loadApiData()
        } else {
            _uiState.update { it.copy(apiDataState = ApiDataState.Error("El permiso de ubicación es necesario.")) }
        }
    }


    private fun loadApiData() {
        viewModelScope.launch {
            _uiState.update { it.copy(apiDataState = ApiDataState.Loading) }
            val location = locationProvider.getCurrentLocation()
            if (location == null) {
                _uiState.update { it.copy(apiDataState = ApiDataState.Error("No se pudo obtener la ubicación.\n¿GPS activado?")) }
                return@launch
            }


            val result = withContext(Dispatchers.IO) {
                try {
                    val apiKey = "5b88b6a1d974644221e80a172fbf9e04" // Tu API Key
                    val url = "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$apiKey&units=metric&lang=es"
                    val apiResultJson = URL(url).readText()
                    val json = JSONObject(apiResultJson)

                    val main = json.getJSONObject("main")
                    val sys = json.getJSONObject("sys")

                    ApiDataState.Success(
                        humidity = "${main.getInt("humidity")} %",
                        sunrise = formatTime(sys.getLong("sunrise")),
                        sunset = formatTime(sys.getLong("sunset"))
                    )
                } catch (e: Exception) {
                    ApiDataState.Error("Sin conexión a internet.\nMostrando solo datos de sensores.")
                }
            }


            _uiState.update { it.copy(apiDataState = result) }
        }
    }


    private fun listenToAllSensors() {
        sensorController.startListening()
        viewModelScope.launch {
            sensorController.ambientTemperatureFlow.collect { temp ->
                _uiState.update { it.copy(sensorData = it.sensorData.copy(temperature = "${temp.toInt()} °C")) }
            }
        }
        viewModelScope.launch {
            sensorController.pressureFlow.collect { pressure ->
                _uiState.update { it.copy(sensorData = it.sensorData.copy(pressure = "${pressure.toInt()} hPa")) }
            }
        }
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
package com.example.navajasuiza.ui.viewmodels

import android.app.Application
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.navajasuiza.data.AppDatabase
import com.example.navajasuiza.data.entities.User
import com.example.navajasuiza.data.repository.UserRepository
import com.example.navajasuiza.sensors.SensorController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DashboardUiState(
    val compassRotation: Float = 0f,
    val angleText: String = "0°",
    val cardinalPoint: String = "Norte",
    val currentUser: User? = null
)

class DashboardViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val userId: Int = savedStateHandle["userId"] ?: -1
    private val repository: UserRepository
    private val sensorController = SensorController(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    init {
        val db = AppDatabase.getInstance(application)
        repository = UserRepository(db.userDao())

        Log.d("DashboardViewModel", "ViewModel INICIADO con userId: $userId")

        loadUser()
        startSensorListeners() // Activamos la lógica de los sensores
    }

    private fun loadUser() {
        if (userId != -1) {
            viewModelScope.launch {
                val user = repository.getUserById(userId)
                if (user != null) {
                    Log.d("DashboardViewModel", "Usuario ENCONTRADO para id $userId: ${user.fullName}")
                    _uiState.update { it.copy(currentUser = user) }
                } else {
                    Log.e("DashboardViewModel", "ERROR: Usuario NO encontrado para id $userId")
                }
            }
        } else {
            Log.w("DashboardViewModel", "Modo invitado (userId: $userId).")
        }
    }

    private fun startSensorListeners() {

        sensorController.startListening()


        viewModelScope.launch {
            sensorController.accelerometerFlow
                .combine(sensorController.magnetometerFlow) { accel, magnet ->
                    System.arraycopy(accel, 0, gravity, 0, accel.size)
                    System.arraycopy(magnet, 0, geomagnetic, 0, magnet.size)
                }
                .collect {
                    if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()

                        _uiState.update {
                            it.copy(

                                compassRotation = azimuthDegrees,
                                angleText = "${azimuthDegrees.toInt()}°",
                                cardinalPoint = getCardinalPoint(azimuthDegrees)
                            )
                        }
                    }
                }
        }
    }


    override fun onCleared() {
        super.onCleared()
        sensorController.stopListening()
        Log.d("DashboardViewModel", "ViewModel destruido. Sensores detenidos.")
    }

    private fun getCardinalPoint(degrees: Float): String {

        val adjustedDegrees = (degrees + 360) % 360
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "O", "NO")

        val index = ((adjustedDegrees + 22.5) / 45).toInt() % 8
        return directions[index]
    }


    companion object {
        fun Factory(
            application: Application,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return DashboardViewModel(application, handle) as T
                }
            }
    }
}







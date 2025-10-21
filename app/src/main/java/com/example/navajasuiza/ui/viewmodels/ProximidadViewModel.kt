package com.example.navajasuiza.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.navajasuiza.sensors.SensorController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ProximityUiState(
    val isObjectNear: Boolean = false
)

class ProximityViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProximityUiState())
    val uiState = _uiState.asStateFlow()

    private val sensorController = SensorController(application)

    init {

        listenToProximitySensor()
    }

    private fun listenToProximitySensor() {
        sensorController.startListening()

        viewModelScope.launch {
            sensorController.proximityFlow.collect { distance ->
                _uiState.update { currentState ->
                    /*currentState.copy(isObjectNear = distance < 5.0f)*/ //PARA EL CEL DE RONY
                    currentState.copy(isObjectNear = distance == 0.0f)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorController.stopListening()
    }
}


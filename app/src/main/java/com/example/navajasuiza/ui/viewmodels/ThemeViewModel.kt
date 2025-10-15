package com.example.navajasuiza.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.navajasuiza.sensors.SensorController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


enum class ThemeMode {
    AUTOMATIC,
    MANUAL
}


data class ThemeUiState(
    val isDarkTheme: Boolean = true,
    val mode: ThemeMode = ThemeMode.AUTOMATIC
)

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorController = SensorController(application)
    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    init {

        sensorController.startListening()
        viewModelScope.launch {
            sensorController.lightSensorFlow.collect { luxValue ->

                if (_uiState.value.mode == ThemeMode.AUTOMATIC) {
                    val isDark = luxValue < 50
                    _uiState.update { it.copy(isDarkTheme = isDark) }
                }
            }
        }
    }


    fun toggleTheme() {
        _uiState.update { currentState ->
            currentState.copy(
                isDarkTheme = !currentState.isDarkTheme,
                mode = ThemeMode.MANUAL
            )
        }
    }


    fun setManualMode() {
        _uiState.update { it.copy(mode = ThemeMode.MANUAL) }
    }

    fun setAutomaticMode() {
        _uiState.update { it.copy(mode = ThemeMode.AUTOMATIC) }
    }

    override fun onCleared() {
        super.onCleared()
        sensorController.stopListening()
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                        return ThemeViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}













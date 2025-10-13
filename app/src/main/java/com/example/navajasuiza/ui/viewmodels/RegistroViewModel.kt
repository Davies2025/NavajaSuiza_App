package com.example.navajasuiza.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navajasuiza.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RegistrationUiState(
    val email: String = "",
    val fullName: String = "",
    val sportsActivity: String = "Ninguna",
    val password: String = "",
    val confirmPassword: String = ""
)

class RegistroViewModel(private val repository: UserRepository) : ViewModel() {


    private val _uiState = MutableStateFlow(RegistrationUiState())

    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()


    private val _registrationMessage = MutableSharedFlow<String>()
    val registrationMessage: SharedFlow<String> = _registrationMessage.asSharedFlow()



    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onSportsActivityChange(activity: String) {
        _uiState.update { it.copy(sportsActivity = activity) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(confirm: String) {
        _uiState.update { it.copy(confirmPassword = confirm) }
    }



    fun onRegisterClicked() {
        val state = _uiState.value


        if (state.email.isBlank() || state.fullName.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            sendMessage("Todos los campos son obligatorios.")
            return
        }
        if (!state.email.contains("@")) {
            sendMessage("El correo electrónico no es válido.")
            return
        }
        if (state.password.length < 8) {
            sendMessage("La contraseña debe tener al menos 8 caracteres.")
            return
        }
        if (state.password != state.confirmPassword) {
            sendMessage("Las contraseñas no coinciden.")
            return
        }


        viewModelScope.launch {
            try {
                repository.registerUser(
                    email = state.email.trim(),
                    fullName = state.fullName.trim(),
                    sportsActivity = state.sportsActivity,
                    password = state.password
                )
                sendMessage("¡Usuario registrado correctamente!")
            } catch (e: Exception) {

                sendMessage("El correo electrónico ya está en uso.")
            }
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            _registrationMessage.emit(message)
        }
    }
}


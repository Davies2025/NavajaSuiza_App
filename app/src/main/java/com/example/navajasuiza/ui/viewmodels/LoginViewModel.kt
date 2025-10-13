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


data class LoginUiState(
    val email: String = "",
    val password: String = ""
)


sealed class LoginEvent {
    data object Success : LoginEvent()
    data class Error(val message: String) : LoginEvent()
}

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onLoginClicked() {
        val state = _uiState.value

        // Validación de campos vacíos
        if (state.email.isBlank() || state.password.isBlank()) {
            sendEvent(LoginEvent.Error("Por favor, ingresa correo y contraseña."))
            return
        }

        viewModelScope.launch {
            val user = repository.loginUser(state.email.trim(), state.password)
            if (user != null) {
                // El usuario y contraseña son correctos
                sendEvent(LoginEvent.Success)
            } else {
                // No se encontró el usuario
                sendEvent(LoginEvent.Error("Correo o contraseña incorrectos."))
            }
        }
    }

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            _loginEvent.emit(event)
        }
    }
}


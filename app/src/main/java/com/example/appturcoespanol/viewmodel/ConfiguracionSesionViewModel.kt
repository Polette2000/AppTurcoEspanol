
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.ConfiguracionSesionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConfiguracionSesionViewModel : ViewModel() {

    // Estado privado que puede modificarse
    private val _uiState = MutableStateFlow(
        ConfiguracionSesionUiState()
    )

    // Estado publico que observa la interfaz
    val uiState: StateFlow<ConfiguracionSesionUiState> =
        _uiState.asStateFlow()

    // Cambia el avatar seleccionado
    fun seleccionarAvatar(avatar: Avatar) {
        _uiState.update { estado ->
            estado.copy(
                avatarSeleccionado = avatar,
                errorAvatar = null,
                confirmado = false
            )
        }
    }

    // Comprueba si se selecciono un avatar
    fun confirmarSeleccion() {
        _uiState.update { estado ->

            if (estado.avatarSeleccionado == null) {
                estado.copy(
                    errorAvatar = "Selecciona un avatar para continuar",
                    confirmado = false
                )
            } else {
                estado.copy(
                    errorAvatar = null,
                    confirmado = true
                )
            }
        }
    }
}

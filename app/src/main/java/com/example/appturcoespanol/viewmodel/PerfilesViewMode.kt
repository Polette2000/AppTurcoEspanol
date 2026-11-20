package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.PerfilesUiState
import com.example.appturcoespanol.repository.PerfilInfantilRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilesViewModel(
    private val repository: PerfilInfantilRepository,
    private val cuentaAdulta: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilesUiState())
    val uiState: StateFlow<PerfilesUiState> = _uiState.asStateFlow()

    init {
        if (cuentaAdulta.isNotBlank()) {
            viewModelScope.launch {
                try {
                    repository.observarPerfiles(cuentaAdulta).collect { perfiles ->
                        _uiState.update {
                            it.copy(perfiles = perfiles, cargando = false, error = null)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(cargando = false, error = "No se pudieron leer los perfiles.")
                    }
                }
            }
        } else {
            _uiState.update { it.copy(cargando = false) }
        }
    }

    fun crearPerfil(apodo: String, idioma: Idioma?, avatar: Avatar?) {
        if (_uiState.value.guardando) return
        if (idioma == null || avatar == null) {
            _uiState.update { it.copy(error = "Selecciona idioma nativo y avatar.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, error = null, perfilCreadoId = null) }
            try {
                val nuevo = repository.crearPerfil(cuentaAdulta, apodo, idioma, avatar)
                _uiState.update {
                    it.copy(guardando = false, perfilCreadoId = nuevo.id, error = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(guardando = false, error = e.message) }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(guardando = false, error = "No se pudo guardar el perfil.")
                }
            }
        }
    }

    fun consumirPerfilCreado() {
        _uiState.update { it.copy(perfilCreadoId = null, error = null) }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}

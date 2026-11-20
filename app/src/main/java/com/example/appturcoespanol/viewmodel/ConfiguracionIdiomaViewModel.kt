
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturcoespanol.model.ConfiguracionIdiomaUiState
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.repository.ConfiguracionIdiomaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfiguracionIdiomaViewModel(
    private val repository: ConfiguracionIdiomaRepository,
    private val cuentaAdulta: String
) : ViewModel() {

    // Estado interno de la pantalla
    private val _uiState = MutableStateFlow(
        ConfiguracionIdiomaUiState()
    )

    // Estado que observará Jetpack Compose
    val uiState: StateFlow<ConfiguracionIdiomaUiState> =
        _uiState.asStateFlow()

    init {
        // Recupera el idioma previamente guardado
        viewModelScope.launch {

            try {
                val guardado = repository
                    .observarIdiomaNativo(cuentaAdulta)
                    .first()

                _uiState.update {
                    it.copy(
                        idiomaNativo = guardado,
                        cargando = false
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = "No pudimos cargar la configuración. " +
                                "Inténtalo otra vez."
                    )
                }
            }
        }
    }

    // Selecciona el idioma nativo del niño
    fun seleccionarIdioma(idioma: Idioma) {

        _uiState.update {
            it.copy(
                idiomaNativo = idioma,
                error = null,
                guardado = false
            )
        }
    }

    // Guarda el idioma y permite continuar
    fun guardarYContinuar() {

        val idioma = _uiState.value.idiomaNativo

        if (_uiState.value.guardando ||
            _uiState.value.cargando
        ) {
            return
        }

        if (idioma == null) {

            _uiState.update {
                it.copy(
                    error = "Selecciona el idioma nativo del niño."
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    guardando = true,
                    error = null
                )
            }

            try {

                repository.guardarIdiomaNativo(
                    cuentaAdulta,
                    idioma
                )

                _uiState.update {
                    it.copy(
                        guardando = false,
                        guardado = true
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (_: Exception) {

                _uiState.update {
                    it.copy(
                        guardando = false,
                        error = "No se pudo guardar el idioma. " +
                                "Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    // Reinicia la señal después de navegar
    fun consumirNavegacion() {

        _uiState.update {
            it.copy(guardado = false)
        }
    }
}

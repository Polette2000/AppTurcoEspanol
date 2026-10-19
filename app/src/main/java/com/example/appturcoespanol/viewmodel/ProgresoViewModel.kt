
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.ProgresoUiState
import com.example.appturcoespanol.repository.ProgresoRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProgresoViewModel(
    private val progresoRepository: ProgresoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProgresoUiState()
    )

    val uiState: StateFlow<ProgresoUiState> =
        _uiState.asStateFlow()

    private var observacion: Job? = null

    // Recuperar los resultados del avatar
    fun cargarProgreso(avatar: Avatar) {

        if (
            _uiState.value.avatar == avatar &&
            observacion?.isActive == true
        ) {
            return
        }

        observacion?.cancel()

        _uiState.value = ProgresoUiState(
            avatar = avatar,
            cargando = true
        )

        observacion = viewModelScope.launch {

            val categorias = CategoriaAprendizaje.entries

            val flujos = categorias.map { categoria ->
                progresoRepository.observarProgreso(
                    avatar = avatar,
                    categoria = categoria
                )
            }

            combine(flujos) { resultados ->

                categorias.zip(
                    resultados.toList()
                ).toMap()

            }.catch { excepcion ->

                if (excepcion is CancellationException) {
                    throw excepcion
                }

                _uiState.update { estado ->
                    estado.copy(
                        cargando = false,
                        error = "No se pudo leer el progreso."
                    )
                }

            }.collect { progresosGuardados ->

                // Mantener el estado del reinicio
                _uiState.update { estado ->

                    if (estado.avatar == avatar) {
                        estado.copy(
                            progresos = progresosGuardados,
                            cargando = false,
                            error = null
                        )
                    } else {
                        estado
                    }
                }
            }
        }
    }

    // NUEVO: Reiniciar progreso del avatar
    fun reiniciarProgreso(avatar: Avatar) {

        val estadoActual = _uiState.value

        // Evitar operaciones repetidas
        if (
            estadoActual.reiniciando ||
            estadoActual.avatar != avatar
        ) {
            return
        }

        _uiState.update { estado ->
            estado.copy(
                reiniciando = true,
                mensajeReinicio = null,
                errorReinicio = null
            )
        }

        viewModelScope.launch {

            try {

                progresoRepository.reiniciarProgreso(
                    avatar = avatar
                )

                _uiState.update { estado ->
                    estado.copy(
                        reiniciando = false,
                        mensajeReinicio =
                            "✅ Progreso reiniciado correctamente.",
                        errorReinicio = null
                    )
                }

            } catch (e: IOException) {

                _uiState.update { estado ->
                    estado.copy(
                        reiniciando = false,
                        mensajeReinicio = null,
                        errorReinicio =
                            "No se pudo reiniciar el progreso."
                    )
                }
            }
        }
    }
}

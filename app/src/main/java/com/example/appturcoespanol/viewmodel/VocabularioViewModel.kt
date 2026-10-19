
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.VocabularioUiState

import com.example.appturcoespanol.repository.ProgresoRepository
import com.example.appturcoespanol.repository.VocabularioRepository

import java.io.IOException

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VocabularioViewModel(
    private val progresoRepository: ProgresoRepository
) : ViewModel() {

    // Estado de la pantalla de vocabulario
    private val _uiState = MutableStateFlow(
        VocabularioUiState()
    )

    val uiState: StateFlow<VocabularioUiState> =
        _uiState.asStateFlow()

    // Estado del mensaje de guardado
    private val _mensajeGuardado =
        MutableStateFlow<String?>(null)

    val mensajeGuardado: StateFlow<String?> =
        _mensajeGuardado.asStateFlow()

    // Cargar las palabras de la categoría seleccionada
    fun cargarCategoria(
        categoria: CategoriaAprendizaje
    ) {

        // Evitar cargar nuevamente la misma categoría
        if (
            _uiState.value.categoria == categoria &&
            _uiState.value.palabras.isNotEmpty()
        ) {
            return
        }

        val palabras =
            VocabularioRepository.obtenerPalabras(categoria)

        _uiState.value = VocabularioUiState(
            categoria = categoria,
            palabras = palabras
        )

        _mensajeGuardado.value = null
    }

    // Avanzar a la siguiente palabra
    // Guardar progreso cuando finaliza la lección
    fun siguientePalabra(avatar: Avatar) {

        val estado = _uiState.value

        // Evitar avanzar si no hay palabras o ya finalizó
        if (
            estado.palabras.isEmpty() ||
            estado.completada
        ) {
            return
        }

        // Todavía quedan palabras por aprender
        if (estado.indiceActual < estado.palabras.lastIndex) {

            _uiState.update { estadoActual ->
                estadoActual.copy(
                    indiceActual = estadoActual.indiceActual + 1
                )
            }

        } else {

            // Se completó la última palabra
            _uiState.update { estadoActual ->
                estadoActual.copy(
                    completada = true
                )
            }

            val categoria = estado.categoria ?: return

            _mensajeGuardado.value = "Guardando progreso..."

            // Guardar el avance utilizando corrutinas
            viewModelScope.launch {

                try {

                    progresoRepository.guardarLeccionCompletada(
                        avatar = avatar,
                        categoria = categoria
                    )

                    _mensajeGuardado.value =
                        "✅ Progreso guardado correctamente"

                } catch (e: IOException) {

                    _mensajeGuardado.value =
                        "No se pudo guardar el progreso. " +
                                "Puedes repetir la lección."
                }
            }
        }
    }

    // Volver a la palabra anterior
    fun anteriorPalabra() {

        _uiState.update { estado ->

            if (estado.indiceActual > 0) {

                estado.copy(
                    indiceActual = estado.indiceActual - 1
                )

            } else {

                estado
            }
        }
    }

    // Reiniciar la lección desde la primera palabra
    fun reiniciarLeccion() {

        _uiState.update { estado ->

            estado.copy(
                indiceActual = 0,
                completada = false
            )
        }

        // Limpiar el mensaje anterior
        _mensajeGuardado.value = null
    }
}

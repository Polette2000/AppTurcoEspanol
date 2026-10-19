
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

    private val _uiState = MutableStateFlow(
        VocabularioUiState()
    )

    val uiState: StateFlow<VocabularioUiState> =
        _uiState.asStateFlow()

    // Mensaje para informar el resultado del guardado
    private val _mensajeGuardado =
        MutableStateFlow<String?>(null)

    val mensajeGuardado: StateFlow<String?> =
        _mensajeGuardado.asStateFlow()

    // Cargar las palabras seleccionadas
    fun cargarCategoria(
        categoria: CategoriaAprendizaje
    ) {

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

    // Avanzar y guardar si termina la leccion
    fun siguientePalabra(avatar: Avatar) {

        val estado = _uiState.value

        if (
            estado.palabras.isEmpty() ||
            estado.completada
        ) {
            return
        }

        if (estado.indiceActual < estado.palabras.lastIndex) {

            _uiState.update {
                it.copy(
                    indiceActual = it.indiceActual + 1
                )
            }

        } else {

            // Se completo la ultima palabra
            _uiState.update {
                it.copy(completada = true)
            }

            val categoria = estado.categoria ?: return

            _mensajeGuardado.value = "Guardando progreso..."

            // Guardar utilizando corrutinas
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

    // Retroceder entre palabras
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

    // Reiniciar la leccion
    fun reiniciarLeccion() {

        _uiState.update { estado ->
            estado.copy(
                indiceActual = 0,
                completada = false
            )
        }

        _mensajeGuardado.value = null
    }
}

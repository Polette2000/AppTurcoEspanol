
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.VocabularioUiState
import com.example.appturcoespanol.repository.VocabularioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VocabularioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        VocabularioUiState()
    )

    val uiState: StateFlow<VocabularioUiState> =
        _uiState.asStateFlow()

    // Carga palabras de la categoria elegida
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
    }

    // Avanza a la siguiente palabra
    fun siguientePalabra() {

        _uiState.update { estado ->

            when {
                estado.palabras.isEmpty() ||
                        estado.completada -> estado

                estado.indiceActual <
                        estado.palabras.lastIndex -> {
                    estado.copy(
                        indiceActual = estado.indiceActual + 1
                    )
                }

                else -> {
                    estado.copy(completada = true)
                }
            }
        }
    }

    // Regresa a la palabra anterior
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

    // Permite repetir la leccion
    fun reiniciarLeccion() {

        _uiState.update { estado ->
            estado.copy(
                indiceActual = 0,
                completada = false
            )
        }
    }
}

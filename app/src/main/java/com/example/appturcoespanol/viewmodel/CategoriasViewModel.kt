
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.CategoriasUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoriasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CategoriasUiState()
    )

    val uiState: StateFlow<CategoriasUiState> =
        _uiState.asStateFlow()

    fun seleccionarCategoria(categoria: CategoriaAprendizaje) {

        _uiState.update { estado ->
            estado.copy(
                categoriaSeleccionada = categoria
            )
        }
    }
}

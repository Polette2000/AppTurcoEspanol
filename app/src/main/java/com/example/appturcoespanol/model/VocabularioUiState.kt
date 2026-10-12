
package com.example.appturcoespanol.model

data class VocabularioUiState(
    val categoria: CategoriaAprendizaje? = null,
    val palabras: List<Palabra> = emptyList(),
    val indiceActual: Int = 0,
    val completada: Boolean = false
) {
    val palabraActual: Palabra?
        get() = palabras.getOrNull(indiceActual)
}

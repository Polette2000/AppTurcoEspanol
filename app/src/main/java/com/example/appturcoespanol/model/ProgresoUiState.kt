
package com.example.appturcoespanol.model

data class ProgresoUiState(
    val avatar: Avatar? = null,

    val progresos:
    Map<CategoriaAprendizaje, ProgresoAprendizaje> =
        emptyMap(),

    val cargando: Boolean = true,

    val error: String? = null,

    // Estado del reinicio
    val reiniciando: Boolean = false,

    // Confirmacion de reinicio exitoso
    val mensajeReinicio: String? = null,

    // Error al reiniciar
    val errorReinicio: String? = null
)


package com.example.appturcoespanol.model

data class PreguntaJuego(
    val palabraTurca: String,
    val respuestaCorrecta: String,
    val opciones: List<String>,
    val emoji: String
)

data class JuegoUiState(
    val categoria: CategoriaAprendizaje? = null,
    val preguntas: List<PreguntaJuego> = emptyList(),
    val indicePregunta: Int = 0,
    val respuestaSeleccionada: String? = null,
    val aciertos: Int = 0,
    val finalizado: Boolean = false
) {
    val preguntaActual: PreguntaJuego?
        get() = preguntas.getOrNull(indicePregunta)

    val esCorrecta: Boolean
        get() = respuestaSeleccionada != null &&
                respuestaSeleccionada ==
                preguntaActual?.respuestaCorrecta
}

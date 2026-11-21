package com.example.appturcoespanol.model

// Una pregunta puede representar una imagen, una traduccion o un sonido.
data class PreguntaJuego(
    val pista: String,
    val respuestaCorrecta: String,
    val opciones: List<String>,
    val emoji: String
)

data class JuegoUiState(
    val categoria: CategoriaAprendizaje? = null,
    val idiomaNativo: Idioma? = null,
    val avatar: Avatar? = null,
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
                respuestaSeleccionada == preguntaActual?.respuestaCorrecta
}

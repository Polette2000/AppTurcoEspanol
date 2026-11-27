
package com.example.appturcoespanol.model

object EvaluadorJuego {

    fun responder(
        estado: JuegoUiState,
        opcion: String
    ): JuegoUiState {

        val pregunta = estado.preguntaActual
            ?: return estado

        if (
            estado.finalizado ||
            estado.respuestaSeleccionada != null ||
            opcion !in pregunta.opciones
        ) {
            return estado
        }

        val esCorrecta =
            opcion == pregunta.respuestaCorrecta

        return estado.copy(
            respuestaSeleccionada = opcion,
            aciertos = estado.aciertos +
                    if (esCorrecta) 1 else 0
        )
    }
}

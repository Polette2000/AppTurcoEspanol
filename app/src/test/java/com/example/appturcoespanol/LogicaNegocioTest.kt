
package com.example.appturcoespanol

import com.example.appturcoespanol.model.JuegoUiState
import com.example.appturcoespanol.model.PreguntaJuego
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.repository.VocabularioRepository

import org.junit.Assert.*
import org.junit.Test
import com.example.appturcoespanol.model.EvaluadorJuego

class LogicaNegocioTest {

    private val pregunta = PreguntaJuego(
        pista = "¿Cómo se dice hola en turco?",
        respuestaCorrecta = "Merhaba",
        opciones = listOf("Merhaba", "Su", "Kedi"),
        emoji = "👋"
    )

    @Test
    fun respuestaCorrecta() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta),
            respuestaSeleccionada = "Merhaba"
        )

        assertTrue(estado.esCorrecta)
    }

    @Test
    fun respuestaIncorrecta() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta),
            respuestaSeleccionada = "Su"
        )

        assertFalse(estado.esCorrecta)
    }

    @Test
    fun sinRespuestaNoEsCorrecta() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta)
        )

        assertFalse(estado.esCorrecta)
    }

    @Test
    fun vocabularioAnimalesTienePalabras() {
        val palabras = VocabularioRepository
            .obtenerPalabras(CategoriaAprendizaje.ANIMALES)

        assertTrue(palabras.isNotEmpty())
    }

    @Test
    fun respuestaCorrectaSumaUnPunto() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta)
        )

        val resultado = EvaluadorJuego.responder(
            estado, "Merhaba"
        )

        assertEquals(1, resultado.aciertos)
    }

    @Test
    fun respuestaIncorrectaNoSumaPuntos() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta)
        )

        val resultado = EvaluadorJuego.responder(
            estado, "Su"
        )

        assertEquals(0, resultado.aciertos)
    }

    @Test
    fun respuestaRepetidaNoSumaDosVeces() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta)
        )

        val primera = EvaluadorJuego.responder(
            estado, "Merhaba"
        )

        val segunda = EvaluadorJuego.responder(
            primera, "Merhaba"
        )

        assertEquals(1, segunda.aciertos)
    }

    @Test
    fun juegoFinalizadoNoPermiteResponder() {
        val estado = JuegoUiState(
            preguntas = listOf(pregunta),
            finalizado = true
        )

        val resultado = EvaluadorJuego.responder(
            estado, "Merhaba"
        )

        assertEquals(estado, resultado)
    }

}

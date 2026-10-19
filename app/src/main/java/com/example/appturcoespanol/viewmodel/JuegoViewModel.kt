
package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.JuegoUiState
import com.example.appturcoespanol.model.PreguntaJuego
import com.example.appturcoespanol.repository.ProgresoRepository
import com.example.appturcoespanol.repository.VocabularioRepository
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JuegoViewModel(
    private val progresoRepository: ProgresoRepository
) : ViewModel() {

    // Estado privado del minijuego
    private val _uiState = MutableStateFlow(
        JuegoUiState()
    )

    // Estado publico para la interfaz
    val uiState: StateFlow<JuegoUiState> =
        _uiState.asStateFlow()

    // Estado del guardado en DataStore
    private val _mensajeGuardado =
        MutableStateFlow<String?>(null)

    val mensajeGuardado: StateFlow<String?> =
        _mensajeGuardado.asStateFlow()

    // Preparar las preguntas de una categoria
    fun cargarCategoria(
        categoria: CategoriaAprendizaje
    ) {

        if (
            _uiState.value.categoria == categoria &&
            _uiState.value.preguntas.isNotEmpty()
        ) {
            return
        }

        val palabras =
            VocabularioRepository.obtenerPalabras(categoria)

        val opcionesDisponibles =
            palabras.map { it.espanol }.distinct()

        val preguntas = palabras.map { palabra ->

            PreguntaJuego(
                palabraTurca = palabra.turco,
                respuestaCorrecta = palabra.espanol,
                opciones = opcionesDisponibles.shuffled(),
                emoji = palabra.emoji
            )
        }

        _uiState.value = JuegoUiState(
            categoria = categoria,
            preguntas = preguntas
        )

        _mensajeGuardado.value = null
    }

    // Verificar la respuesta elegida
    fun responder(opcion: String) {

        _uiState.update { estado ->

            val pregunta = estado.preguntaActual

            if (
                pregunta == null ||
                estado.respuestaSeleccionada != null ||
                estado.finalizado ||
                opcion !in pregunta.opciones
            ) {
                estado
            } else {

                val correcta =
                    opcion == pregunta.respuestaCorrecta

                estado.copy(
                    respuestaSeleccionada = opcion,
                    aciertos = estado.aciertos +
                            if (correcta) 1 else 0
                )
            }
        }
    }

    // Avanzar y guardar cuando termina el juego
    fun siguientePregunta(avatar: Avatar) {

        val estado = _uiState.value

        // No avanzar sin responder
        if (
            estado.respuestaSeleccionada == null ||
            estado.finalizado
        ) {
            return
        }

        if (
            estado.indicePregunta <
            estado.preguntas.lastIndex
        ) {

            _uiState.update {
                it.copy(
                    indicePregunta = it.indicePregunta + 1,
                    respuestaSeleccionada = null
                )
            }

        } else {

            val categoria = estado.categoria ?: return

            // Marcar el juego como finalizado
            _uiState.value = estado.copy(
                finalizado = true
            )

            _mensajeGuardado.value =
                "Guardando puntuación..."

            // Guardar en DataStore
            viewModelScope.launch {

                try {

                    progresoRepository.guardarMejorPuntaje(
                        avatar = avatar,
                        categoria = categoria,
                        puntaje = estado.aciertos
                    )

                    _mensajeGuardado.value =
                        "✅ Resultado guardado. " +
                                "Se conserva tu mejor puntuación."

                } catch (e: IOException) {

                    _mensajeGuardado.value =
                        "⚠️ No se pudo guardar la puntuación."
                }
            }
        }
    }

    // Reiniciar el minijuego
    fun reiniciarJuego() {

        _uiState.update { estado ->
            estado.copy(
                indicePregunta = 0,
                respuestaSeleccionada = null,
                aciertos = 0,
                finalizado = false
            )
        }

        _mensajeGuardado.value = null
    }
}

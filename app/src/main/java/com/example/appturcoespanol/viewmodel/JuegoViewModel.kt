package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.JuegoUiState
import com.example.appturcoespanol.model.PreguntaJuego
import com.example.appturcoespanol.repository.ProgresoRepository
import com.example.appturcoespanol.repository.VocabularioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JuegoViewModel(
    private val progresoRepository: ProgresoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JuegoUiState())
    val uiState: StateFlow<JuegoUiState> = _uiState.asStateFlow()

    private val _mensajeGuardado = MutableStateFlow<String?>(null)
    val mensajeGuardado: StateFlow<String?> = _mensajeGuardado.asStateFlow()

    fun cargarCategoria(
        categoria: CategoriaAprendizaje,
        idiomaNativo: Idioma,
        avatar: Avatar
    ) {
        val anterior = _uiState.value
        if (anterior.categoria == categoria &&
            anterior.idiomaNativo == idiomaNativo &&
            anterior.avatar == avatar &&
            anterior.preguntas.isNotEmpty()
        ) return

        val palabras = VocabularioRepository.obtenerPalabras(categoria)
        val aprendeTurco = idiomaNativo == Idioma.ESPANOL

        fun palabraObjetivo(espanol: String, turco: String): String =
            if (aprendeTurco) turco else espanol

        fun palabraNativa(espanol: String, turco: String): String =
            if (aprendeTurco) espanol else turco

        // Opciones distintas para cada modalidad del avatar.
        val todasLasOpciones = palabras.map { palabra ->
            when (avatar) {
                Avatar.ZORRO -> palabra.emoji // Asociacion visual
                Avatar.PANDA -> palabraObjetivo(palabra.espanol, palabra.turco)
                Avatar.GATO -> palabraNativa(palabra.espanol, palabra.turco)
            }
        }.distinct()

        val preguntas = palabras.shuffled().map { palabra ->
            val enIdiomaNuevo = palabraObjetivo(palabra.espanol, palabra.turco)
            val enIdiomaNativo = palabraNativa(palabra.espanol, palabra.turco)

            PreguntaJuego(
                pista = when (avatar) {
                    Avatar.ZORRO -> enIdiomaNuevo // Encuentra la imagen
                    Avatar.PANDA -> enIdiomaNativo // Traduce
                    Avatar.GATO -> enIdiomaNuevo  // Escucha esta palabra
                },
                respuestaCorrecta = when (avatar) {
                    Avatar.ZORRO -> palabra.emoji
                    Avatar.PANDA -> enIdiomaNuevo
                    Avatar.GATO -> enIdiomaNativo
                },
                opciones = todasLasOpciones.shuffled(),
                emoji = palabra.emoji
            )
        }

        _uiState.value = JuegoUiState(
            categoria = categoria,
            idiomaNativo = idiomaNativo,
            avatar = avatar,
            preguntas = preguntas
        )
        _mensajeGuardado.value = null
    }

    fun responder(opcion: String) {
        _uiState.update { estado ->
            val pregunta = estado.preguntaActual
            if (pregunta == null || estado.finalizado ||
                estado.respuestaSeleccionada != null ||
                opcion !in pregunta.opciones
            ) {
                estado
            } else {
                estado.copy(
                    respuestaSeleccionada = opcion,
                    aciertos = estado.aciertos +
                            if (opcion == pregunta.respuestaCorrecta) 1 else 0
                )
            }
        }
    }

    fun siguientePregunta(avatar: Avatar) {
        val estado = _uiState.value
        if (estado.respuestaSeleccionada == null || estado.finalizado ||
            estado.avatar != avatar
        ) return

        if (estado.indicePregunta < estado.preguntas.lastIndex) {
            _uiState.update {
                it.copy(
                    indicePregunta = it.indicePregunta + 1,
                    respuestaSeleccionada = null
                )
            }
        } else {
            val categoria = estado.categoria ?: return
            _uiState.value = estado.copy(finalizado = true)
            _mensajeGuardado.value = "Guardando puntuación..."

            viewModelScope.launch {
                try {
                    progresoRepository.guardarMejorPuntaje(
                        avatar = avatar,
                        categoria = categoria,
                        puntaje = estado.aciertos
                    )
                    _mensajeGuardado.value = "✅ Mejor puntuación guardada."
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _mensajeGuardado.value = "⚠️ No se pudo guardar la puntuación."
                }
            }
        }
    }

    fun reiniciarJuego() {
        _uiState.update { estado ->
            estado.copy(
                preguntas = estado.preguntas.shuffled().map { pregunta ->
                    pregunta.copy(opciones = pregunta.opciones.shuffled())
                },
                indicePregunta = 0,
                respuestaSeleccionada = null,
                aciertos = 0,
                finalizado = false
            )
        }
        _mensajeGuardado.value = null
    }
}


package com.example.appturcoespanol.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.viewmodel.JuegoViewModel

@Composable
fun JuegoScreen(
    categoria: CategoriaAprendizaje,
    avatar: Avatar,
    viewModel: JuegoViewModel,
    onVolver: () -> Unit
) {

    // Cargar preguntas de la categoria
    LaunchedEffect(categoria) {
        viewModel.cargarCategoria(categoria)
    }

    // Observar el estado del juego
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Observar el resultado del guardado
    val mensajeGuardado by
    viewModel.mensajeGuardado.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "¡Vamos a jugar! 🎮",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${categoria.nombre} / ${categoria.nombreTurco}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        // Mostrar el avatar seleccionado
        Text(
            text = "${avatar.emoji} ${avatar.nombre}",
            style = MaterialTheme.typography.titleMedium
        )

        if (estado.preguntas.isEmpty()) {

            Text("Preparando preguntas...")

        } else if (estado.finalizado) {

            // RESULTADO FINAL
            Text(
                text = "🏆",
                fontSize = 72.sp
            )

            Text(
                text = "¡Juego terminado!",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Obtuviste ${estado.aciertos} de ${estado.preguntas.size} puntos",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Harika! ¡Muy buen trabajo!",
                textAlign = TextAlign.Center
            )

            // Mensaje del guardado en DataStore
            mensajeGuardado?.let { mensaje ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = mensaje,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = viewModel::reiniciarJuego,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a jugar")
            }

        } else {

            estado.preguntaActual?.let { pregunta ->

                Text(
                    text = "Pregunta ${estado.indicePregunta + 1} de ${estado.preguntas.size}"
                )

                // Barra de progreso
                LinearProgressIndicator(
                    progress = {
                        (estado.indicePregunta + 1).toFloat() /
                                estado.preguntas.size
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Tarjeta de pregunta
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = pregunta.emoji,
                            fontSize = 72.sp
                        )

                        Text(
                            text = "¿Cómo se dice en español?",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = pregunta.palabraTurca,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                // ALTERNATIVAS
                pregunta.opciones.forEach { opcion ->

                    val respondida =
                        estado.respuestaSeleccionada != null

                    val esRespuestaCorrecta =
                        opcion == pregunta.respuestaCorrecta

                    val esSeleccionada =
                        opcion == estado.respuestaSeleccionada

                    val colorBorde = when {

                        respondida && esRespuestaCorrecta ->
                            MaterialTheme.colorScheme.primary

                        respondida && esSeleccionada ->
                            MaterialTheme.colorScheme.error

                        else ->
                            MaterialTheme.colorScheme.outline
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.responder(opcion)
                        },
                        enabled = !respondida,
                        border = BorderStroke(
                            2.dp,
                            colorBorde
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(opcion)
                    }
                }

                // RETROALIMENTACION ANIMADA
                AnimatedVisibility(
                    visible = estado.respuestaSeleccionada != null
                ) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            if (estado.esCorrecta) {

                                Text(
                                    text = "✅ ¡Excelente! Respuesta correcta",
                                    color = MaterialTheme.colorScheme.primary
                                )

                            } else {

                                Text(
                                    text = "❌ ¡Sigue practicando!",
                                    color = MaterialTheme.colorScheme.error
                                )

                                Text(
                                    text = "La respuesta correcta es: ${pregunta.respuestaCorrecta}"
                                )
                            }

                            // Avanzar o guardar puntuacion
                            Button(
                                onClick = {
                                    viewModel.siguientePregunta(avatar)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (
                                        estado.indicePregunta ==
                                        estado.preguntas.lastIndex
                                    ) {
                                        "Ver resultados"
                                    } else {
                                        "Siguiente pregunta"
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Puntos: ${estado.aciertos}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a la lección")
        }
    }
}

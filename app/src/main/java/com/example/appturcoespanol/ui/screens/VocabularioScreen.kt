
package com.example.appturcoespanol.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.tts.TextToSpeechManager
import com.example.appturcoespanol.viewmodel.VocabularioViewModel

@Composable
fun VocabularioScreen(
    categoria: CategoriaAprendizaje,
    avatar: Avatar,
    viewModel: VocabularioViewModel,
    onVolver: () -> Unit,
    onAbrirJuego: () -> Unit,
    onAbrirCamara: () -> Unit
) {

    // Obtener el contexto de Android
    val context = LocalContext.current

    // Administrador para reproducir pronunciaciones
    val ttsManager = remember(context) {
        TextToSpeechManager(context)
    }

    // Estado para mostrar errores de pronunciación
    var errorPronunciacion by remember {
        mutableStateOf(false)
    }

    // Liberar recursos de audio al salir
    DisposableEffect(ttsManager) {
        onDispose {
            ttsManager.liberar()
        }
    }

    // Cargar las palabras de la categoría
    LaunchedEffect(categoria) {
        viewModel.cargarCategoria(categoria)
    }

    // Observar el estado del ViewModel
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Mensaje del guardado del progreso
    val mensajeGuardado by viewModel.mensajeGuardado
        .collectAsStateWithLifecycle()

    // Detener el audio al cambiar de palabra
    LaunchedEffect(
        ttsManager,
        estado.indiceActual,
        estado.categoria,
        estado.completada
    ) {
        ttsManager.detener()
        errorPronunciacion = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Título de la lección
        Text(
            text = "¡Aprendamos! 📚",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${categoria.nombre} / ${categoria.nombreTurco}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        if (estado.palabras.isEmpty()) {

            Text("Preparando palabras...")

        } else {

            // Calcular el progreso de la lección
            val progreso = if (estado.completada) {
                1f
            } else {
                (estado.indiceActual + 1).toFloat() /
                        estado.palabras.size
            }

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth()
            )

            if (estado.completada) {

                // Mensaje de lección completada
                Text(
                    text = "🎉",
                    fontSize = 72.sp
                )

                Text(
                    text = "¡Lección completada!",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Harika! Çok iyi!",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Has recorrido todas las palabras.",
                    textAlign = TextAlign.Center
                )

                // Mostrar resultado del guardado en DataStore
                mensajeGuardado?.let { mensaje ->
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // Repetir la lección
                Button(
                    onClick = viewModel::reiniciarLeccion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Repetir lección")
                }

                // Abrir el minijuego
                Button(
                    onClick = onAbrirJuego,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎮 Jugar y practicar")
                }

            } else {

                estado.palabraActual?.let { palabra ->

                    Text(
                        text = "Palabra ${estado.indiceActual + 1} de ${estado.palabras.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Tarjeta de vocabulario
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Text(
                                text = palabra.emoji,
                                fontSize = 80.sp
                            )

                            Text(
                                text = palabra.espanol,
                                style = MaterialTheme.typography.headlineLarge
                            )

                            Text(
                                text = palabra.turco,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            // Reproducir la palabra en español
                            Button(
                                onClick = {
                                    errorPronunciacion =
                                        !ttsManager.hablar(palabra.espanol)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🔊 Escuchar en español")
                            }

                            if (errorPronunciacion) {
                                Text(
                                    text = "La voz española no está disponible o todavía no está lista. Inténtalo nuevamente.",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Navegar entre las palabras
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(
                            onClick = viewModel::anteriorPalabra,
                            enabled = estado.indiceActual > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Anterior")
                        }

                        Button(
                            onClick = {
                                viewModel.siguientePalabra(avatar)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (
                                    estado.indiceActual ==
                                    estado.palabras.lastIndex
                                ) {
                                    "Finalizar"
                                } else {
                                    "Siguiente"
                                }
                            )
                        }
                    }
                }
            }
        }

        // Actividad con la cámara
        Button(
            onClick = onAbrirCamara,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷 Busca y fotografía")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Regresar a categorías
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a categorías")
        }
    }
}

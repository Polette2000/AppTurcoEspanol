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


    // Obtenemos el contexto de Android
    val context = LocalContext.current

    // Creamos el administrador de audio nativo
    val ttsManager = remember(context) {
        TextToSpeechManager(context)
    }

    // Estado para mostrar errores de audio
    var errorPronunciacion by remember {
        mutableStateOf(false)
    }

    // Liberamos recursos al salir de la pantalla
    DisposableEffect(ttsManager) {
        onDispose {
            ttsManager.liberar()
        }
    }

    // Cargamos las palabras de la categoria
    LaunchedEffect(categoria) {
        viewModel.cargarCategoria(categoria)
    }

    // Observamos el estado del ViewModel
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val mensajeGuardado by viewModel.mensajeGuardado.collectAsStateWithLifecycle()

    // Detenemos el audio cuando cambia la palabra
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

            // Calculamos el progreso de la leccion
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

                Text(
                    text = "🎉",
                    fontSize = 72.sp
                )

                Text(
                    text = "¡Lección completada!",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Harika! Çok iyi!"
                )

                // Resultado del guardado en DataStore
                mensajeGuardado?.let { mensaje ->
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = viewModel::reiniciarLeccion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Repetir lección")
                }

                // Abrir minijuego despues de completar la leccion
                Button(
                    onClick = onAbrirJuego,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎮 Jugar y practicar")
                }

            } else {

                estado.palabraActual?.let { palabra ->

                    Text(
                        text = "Palabra ${estado.indiceActual + 1} de ${estado.palabras.size}"
                    )

                    // Tarjeta de vocabulario
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
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

                            // Recurso nativo TextToSpeech
                            Button(
                                onClick = {
                                    errorPronunciacion =
                                        !ttsManager.hablar(
                                            palabra.espanol
                                        )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🔊 Escuchar en español")
                            }

                            // Mensaje si el audio falla
                            if (errorPronunciacion) {
                                Text(
                                    text = "La voz española no está disponible o todavía no está lista. Inténtalo nuevamente.",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Botones de navegacion de palabras
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

        // Actividad opcional con la camara nativa.
        Button(
            onClick = onAbrirCamara,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷 Busca y fotografía")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a categorías")
        }
    }
}


package com.example.appturcoespanol.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.ProgresoAprendizaje
import com.example.appturcoespanol.viewmodel.ProgresoViewModel

@Composable
fun ProgresoScreen(
    avatar: Avatar,
    viewModel: ProgresoViewModel,
    onVolver: () -> Unit
) {

    // Consultar los datos guardados
    LaunchedEffect(avatar) {
        viewModel.cargarProgreso(avatar)
    }

    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Controlar el dialogo de confirmacion
    var mostrarConfirmacion by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "📊 Mi progreso",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${avatar.emoji} ${avatar.nombre}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Öğrenme ilerlemem",
            style = MaterialTheme.typography.titleMedium
        )

        when {

            estado.cargando -> {

                CircularProgressIndicator()

                Text("Cargando progreso...")
            }

            estado.error != null -> {

                Text(
                    text = estado.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            else -> {

                val categorias = CategoriaAprendizaje.entries

                val completadas = categorias.count { categoria ->
                    estado.progresos[categoria]
                        ?.leccionCompletada == true
                }

                Text(
                    text = "Lecciones completadas: " +
                            "$completadas de ${categorias.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                LinearProgressIndicator(
                    progress = {
                        completadas.toFloat() / categorias.size
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mostrar las categorias
                categorias.forEach { categoria ->

                    val progreso =
                        estado.progresos[categoria]
                            ?: ProgresoAprendizaje()

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                text = "${categoria.emoji} ${categoria.nombre}",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = categoria.nombreTurco
                            )

                            Text(
                                text = if (
                                    progreso.leccionCompletada
                                ) {
                                    "✅ Lección completada"
                                } else {
                                    "⏳ Lección pendiente"
                                }
                            )

                            Text(
                                text = "Mejor puntuación: " +
                                        "${progreso.mejorPuntaje} puntos",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                // Indicar que se esta reiniciando
                if (estado.reiniciando) {

                    CircularProgressIndicator()

                    Text("Reiniciando progreso...")
                }

                // Mensaje de reinicio exitoso
                estado.mensajeReinicio?.let { mensaje ->

                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }

                // Mensaje de error al reiniciar
                estado.errorReinicio?.let { mensaje ->

                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                // Boton para solicitar el reinicio
                Button(
                    onClick = {
                        mostrarConfirmacion = true
                    },
                    enabled = !estado.reiniciando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.error,
                        contentColor =
                            MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🗑️ Reiniciar progreso")
                }
            }
        }

        OutlinedButton(
            onClick = onVolver,
            enabled = !estado.reiniciando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a categorías")
        }
    }

    // Cuadro de confirmacion
    if (mostrarConfirmacion) {

        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },

            title = {
                Text("¿Reiniciar progreso?")
            },

            text = {
                Text(
                    "Se eliminarán las lecciones completadas " +
                            "y las mejores puntuaciones de " +
                            "${avatar.nombre}. " +
                            "No se borrará el progreso de otros avatares. " +
                            "Esta acción no se puede deshacer."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        viewModel.reiniciarProgreso(avatar)
                    }
                ) {
                    Text(
                        text = "Sí, reiniciar",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

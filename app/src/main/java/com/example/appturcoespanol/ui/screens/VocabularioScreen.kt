
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.viewmodel.VocabularioViewModel

@Composable
fun VocabularioScreen(
    categoria: CategoriaAprendizaje,
    viewModel: VocabularioViewModel,
    onVolver: () -> Unit
) {

    LaunchedEffect(categoria) {
        viewModel.cargarCategoria(categoria)
    }

    val estado by viewModel.uiState.collectAsStateWithLifecycle()

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

            val progreso =
                if (estado.completada) {
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
                    text = "Harika! Çok iyi!",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Has recorrido todas las palabras.",
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = viewModel::reiniciarLeccion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Repetir lección")
                }

            } else {

                estado.palabraActual?.let { palabra ->

                    Text(
                        text = "Palabra ${estado.indiceActual + 1} de ${estado.palabras.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )

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
                        }
                    }

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
                            onClick = viewModel::siguientePalabra,
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a categorías")
        }
    }
}

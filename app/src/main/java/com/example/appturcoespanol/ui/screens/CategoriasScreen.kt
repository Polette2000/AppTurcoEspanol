
package com.example.appturcoespanol.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.viewmodel.CategoriasViewModel

@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel,
    onVolver: () -> Unit,
    onAbrirLeccion: (CategoriaAprendizaje) -> Unit,
    onVerProgreso: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Título principal
        Text(
            text = "¡Vamos a aprender! 🌟",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ne öğrenmek istersin?",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "¿Qué quieres aprender hoy?",
            textAlign = TextAlign.Center
        )

        // Mostrar categorías de aprendizaje
        CategoriaAprendizaje.entries.forEach { categoria ->

            val seleccionada =
                uiState.categoriaSeleccionada == categoria

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.seleccionarCategoria(categoria)
                    },
                border = BorderStroke(
                    width = if (seleccionada) 3.dp else 1.dp,
                    color = if (seleccionada) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = categoria.emoji,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {

                        Text(
                            text = categoria.nombre,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = categoria.nombreTurco,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Mostrar información de la categoría seleccionada
        AnimatedVisibility(
            visible = uiState.categoriaSeleccionada != null
        ) {

            uiState.categoriaSeleccionada?.let { categoria ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "¡Excelente elección! 🎉",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = categoria.descripcion
                        )

                        Text(
                            text = "Categoría: ${categoria.nombre}"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para abrir vocabulario
                        Button(
                            onClick = {
                                onAbrirLeccion(categoria)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📚 Aprender palabras")
                        }
                    }
                }
            }
        }

        // Nuevo botón para consultar el progreso
        Button(
            onClick = onVerProgreso,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📊 Ver mi progreso")
        }

        // Botón para regresar
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

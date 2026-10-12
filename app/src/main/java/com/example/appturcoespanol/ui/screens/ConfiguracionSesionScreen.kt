
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.viewmodel.ConfiguracionSesionViewModel

@Composable
fun ConfiguracionSesionScreen(
    viewModel: ConfiguracionSesionViewModel,
    onVolver: () -> Unit,
    onVerCategorias: () -> Unit

) {

    // Observamos los cambios del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Elige tu aventura! 🌟",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Karakterini seç!",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Selecciona tu personaje favorito",
            textAlign = TextAlign.Center
        )

        // Mostramos las opciones disponibles
        Avatar.entries.forEach { avatar ->

            val seleccionado =
                uiState.avatarSeleccionado == avatar

            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    width = if (seleccionado) 2.dp else 1.dp,
                    color = if (seleccionado)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.seleccionarAvatar(avatar)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = avatar.emoji,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = avatar.nombre,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    RadioButton(
                        selected = seleccionado,
                        onClick = {
                            viewModel.seleccionarAvatar(avatar)
                        }
                    )
                }
            }
        }

        // Error si no se selecciona un avatar
        uiState.errorAvatar?.let { mensaje ->
            Text(
                text = "⚠️ $mensaje",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                viewModel.confirmarSeleccion()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }

        // Mensaje de confirmacion animado
        AnimatedVisibility(
            visible = uiState.confirmado
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¡Excelente! Elegiste a " +
                            "${uiState.avatarSeleccionado?.nombre}. " +
                            "¡Tu aventura está por comenzar!",
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (uiState.confirmado) {

            Button(
                onClick = onVerCategorias,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Elegir una lección")
            }
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Volver")
        }
    }
}

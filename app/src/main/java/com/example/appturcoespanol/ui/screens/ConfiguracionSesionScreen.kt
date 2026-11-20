
package com.example.appturcoespanol.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.viewmodel.ConfiguracionSesionViewModel

@Composable
fun ConfiguracionSesionScreen(
    viewModel: ConfiguracionSesionViewModel,
    onVolver: () -> Unit,
    onVerCategorias: () -> Unit
) {

    // Colores de EsTurk
    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val azulOscuro = Color(0xFF243A53)
    val menta = Color(0xFFBCE9CE)
    val amarillo = Color(0xFFFFD579)
    val coralClaro = Color(0xFFFFD7CB)

    // Estado del avatar seleccionado
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Descripción de cada personaje
    fun descripcion(avatar: Avatar): String {
        return when (avatar) {
            Avatar.ZORRO ->
                "Explora, descubre y fotografía"

            Avatar.PANDA ->
                "Resuelve juegos y desafíos"

            Avatar.GATO ->
                "Escucha y practica palabras"
        }
    }

    // Nombre de la aventura
    fun nombreAventura(avatar: Avatar): String {
        return when (avatar) {
            Avatar.ZORRO -> "El explorador"
            Avatar.PANDA -> "El pensador"
            Avatar.GATO -> "El comunicador"
        }
    }

    // Color característico del avatar
    fun colorAvatar(avatar: Avatar): Color {
        return when (avatar) {
            Avatar.ZORRO -> coralClaro
            Avatar.PANDA -> amarillo
            Avatar.GATO -> menta
        }
    }

    Scaffold(
        containerColor = crema,

        // Botones siempre visibles en la parte inferior
        bottomBar = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(crema)
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 12.dp,
                        bottom = 16.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {

                        // Validar que haya un avatar
                        viewModel.confirmarSeleccion()

                        // Avanzar si la selección es válida
                        if (estado.avatarSeleccionado != null) {
                            onVerCategorias()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = turquesa,
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = "¡Comenzar mi aventura!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Volver",
                        color = turquesa
                    )
                }
            }
        }

    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(18.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🌟",
                fontSize = 52.sp
            )

            Text(
                text = "¡Elige tu compañero!",
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                color = azulOscuro,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Cada personaje tiene una forma " +
                        "especial de aprender.",
                fontSize = 16.sp,
                color = azulOscuro,
                textAlign = TextAlign.Center
            )

            // Tarjetas de selección
            Avatar.entries.forEach { avatar ->

                val seleccionado =
                    estado.avatarSeleccionado == avatar

                val fondoAnimado by animateColorAsState(
                    targetValue = if (seleccionado) {
                        colorAvatar(avatar)
                    } else {
                        Color.White
                    },
                    animationSpec = tween(300),
                    label = "colorAvatar"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.seleccionarAvatar(avatar)
                        },

                    shape = RoundedCornerShape(24.dp),

                    border = BorderStroke(
                        width = if (seleccionado) {
                            3.dp
                        } else {
                            1.dp
                        },
                        color = if (seleccionado) {
                            turquesa
                        } else {
                            Color(0xFFD5E2E2)
                        }
                    ),

                    colors = CardDefaults.cardColors(
                        containerColor = fondoAnimado
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            text = avatar.emoji,
                            fontSize = 56.sp
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement =
                                Arrangement.spacedBy(5.dp)
                        ) {

                            Text(
                                text = avatar.nombre,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = azulOscuro
                            )

                            Text(
                                text = nombreAventura(avatar),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = turquesa
                            )

                            Text(
                                text = descripcion(avatar),
                                fontSize = 13.sp,
                                color = azulOscuro
                            )
                        }

                        Text(
                            text = if (seleccionado) {
                                "✓"
                            } else {
                                "○"
                            },
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            color = turquesa
                        )
                    }
                }
            }

            // Mostrar error cuando no selecciona personaje
            estado.errorAvatar?.let { mensaje ->

                Text(
                    text = "⚠️ $mensaje",
                    color = Color(0xFFB33939),
                    textAlign = TextAlign.Center
                )
            }

            // Mensaje de apoyo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = menta
                )
            ) {

                Text(
                    text = "💛 ¡Puedes probar diferentes " +
                            "aventuras y descubrir cuál te gusta más!",
                    modifier = Modifier.padding(16.dp),
                    color = azulOscuro,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
    }
}

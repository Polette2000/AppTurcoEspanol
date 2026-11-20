
package com.example.appturcoespanol.ui.screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.viewmodel.ConfiguracionIdiomaViewModel

@Composable
fun ConfiguracionIdiomaScreen(
    viewModel: ConfiguracionIdiomaViewModel,
    onVolver: () -> Unit,
    onContinuar: () -> Unit
) {

    // Estado recibido desde el ViewModel
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Colores de EsTurk
    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val azulOscuro = Color(0xFF243A53)
    val menta = Color(0xFFBCE9CE)

    // Cuando se guarda correctamente, navegamos
    LaunchedEffect(estado.guardado) {

        if (estado.guardado) {
            viewModel.consumirNavegacion()
            onContinuar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(crema)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "🌍",
            fontSize = 64.sp
        )

        Text(
            text = "¡Personalicemos su aventura!",
            color = azulOscuro,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "¿Qué idioma habla el niño en casa?",
            color = azulOscuro,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )

        // Esperar a que se recupere la configuración
        if (estado.cargando) {

            CircularProgressIndicator(
                color = turquesa
            )

        } else {

            // Mostrar español y turco
            Idioma.entries.forEach { idioma ->

                val seleccionado =
                    idioma == estado.idiomaNativo

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(20.dp),

                    border = BorderStroke(
                        width = if (seleccionado) 3.dp else 1.dp,
                        color = if (seleccionado) {
                            turquesa
                        } else {
                            Color(0xFFB8D0CE)
                        }
                    ),

                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) {
                            menta
                        } else {
                            Color.White
                        }
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.seleccionarIdioma(idioma)
                            }
                            .padding(18.dp),

                        verticalAlignment = Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text = if (idioma == Idioma.TURCO) {
                                "🇹🇷"
                            } else {
                                "🇪🇸"
                            },
                            fontSize = 30.sp
                        )

                        Text(
                            text = idioma.nombreVisible,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.SemiBold,
                            color = azulOscuro,
                            fontSize = 18.sp
                        )

                        RadioButton(
                            selected = seleccionado,
                            onClick = {
                                viewModel.seleccionarIdioma(idioma)
                            }
                        )
                    }
                }
            }
        }

        // Mostrar idioma que aprenderá
        estado.idiomaAprendizaje?.let { idiomaObjetivo ->

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = menta
                ),

                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Entonces aprenderá:",
                        color = azulOscuro,
                        fontSize = 15.sp
                    )

                    Text(
                        text = if (idiomaObjetivo == Idioma.ESPANOL) {
                            "🇪🇸 Español"
                        } else {
                            "🇹🇷 Türkçe (turco)"
                        },

                        color = azulOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp
                    )
                }
            }
        }

        // Mostrar errores de validación
        estado.error?.let { mensaje ->

            Text(
                text = mensaje,
                color = Color(0xFFAA3434),
                textAlign = TextAlign.Center
            )
        }

        // Guardar idioma seleccionado
        Button(
            onClick = viewModel::guardarYContinuar,

            enabled = !estado.cargando && !estado.guardando,

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            shape = RoundedCornerShape(18.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = turquesa
            )
        ) {

            Text(
                text = if (estado.guardando) {
                    "Guardando..."
                } else {
                    "Continuar con el avatar"
                },

                color = Color.White
            )
        }

        // Volver a la bienvenida
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {

            Text(
                text = "Volver a bienvenida",
                color = turquesa
            )
        }
    }
}

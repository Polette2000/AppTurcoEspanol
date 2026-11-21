package com.example.appturcoespanol.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.tts.TextToSpeechManager
import com.example.appturcoespanol.viewmodel.VocabularioViewModel

@Composable
fun VocabularioScreen(
    categoria: CategoriaAprendizaje,
    avatar: Avatar,
    idiomaNativo: Idioma,
    viewModel: VocabularioViewModel,
    onVolver: () -> Unit,
    onAbrirJuego: () -> Unit,
    onAbrirCamara: () -> Unit
) {
    val contexto = LocalContext.current
    val ttsManager = remember(contexto) { TextToSpeechManager(contexto) }
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val mensajeGuardado by viewModel.mensajeGuardado.collectAsStateWithLifecycle()
    var errorPronunciacion by remember { mutableStateOf(false) }

    // El aprendizaje siempre es del idioma contrario al idioma nativo.
    val aprendeTurco = idiomaNativo == Idioma.ESPANOL
    val idiomaObjetivo = if (aprendeTurco) Idioma.TURCO else Idioma.ESPANOL
    val tituloCategoria = if (aprendeTurco) categoria.nombreTurco else categoria.nombre

    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val oscuro = Color(0xFF243A53)

    LaunchedEffect(categoria) { viewModel.cargarCategoria(categoria) }
    DisposableEffect(ttsManager) { onDispose { ttsManager.liberar() } }
    LaunchedEffect(estado.indiceActual, estado.completada, idiomaObjetivo) {
        ttsManager.detener()
        errorPronunciacion = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(crema)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = if (aprendeTurco) "¡Aprendamos! 📚" else "Hadi öğrenelim! 📚",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = oscuro,
            textAlign = TextAlign.Center
        )
        Text(
            text = "$tituloCategoria  •  ${if (aprendeTurco) "🇹🇷 Türkçe" else "🇪🇸 Español"}",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = turquesa,
            textAlign = TextAlign.Center
        )

        if (estado.palabras.isEmpty()) {
            Text(if (aprendeTurco) "Preparando palabras..." else "Kelimeler hazırlanıyor...")
        } else {
            val progreso = if (estado.completada) {
                1f
            } else {
                (estado.indiceActual + 1).toFloat() / estado.palabras.size
            }
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth()
            )

            if (estado.completada) {
                Text("🎉", fontSize = 70.sp)
                Text(
                    if (aprendeTurco) "¡Lección completada!" else "Ders tamamlandı!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = oscuro
                )
                Text(
                    if (aprendeTurco) "¡Aprendiste todas las palabras!" else "Tüm kelimeleri öğrendin!",
                    textAlign = TextAlign.Center,
                    color = oscuro
                )

                // Traducción de los mensajes del repositorio actual.
                mensajeGuardado?.let { mensaje ->
                    val mensajeVisible = if (aprendeTurco) mensaje else when {
                        mensaje.startsWith("Guardando") -> "İlerleme kaydediliyor..."
                        mensaje.startsWith("✅") -> "✅ İlerleme kaydedildi"
                        else -> "İlerleme kaydedilemedi. Dersi tekrar deneyebilirsin."
                    }
                    Text(mensajeVisible, textAlign = TextAlign.Center, color = oscuro)
                }

                Button(onClick = viewModel::reiniciarLeccion, modifier = Modifier.fillMaxWidth()) {
                    Text(if (aprendeTurco) "Repetir lección" else "Dersi tekrarla")
                }
                Button(onClick = onAbrirJuego, modifier = Modifier.fillMaxWidth()) {
                    Text(if (aprendeTurco) "🎮 Jugar y practicar" else "🎮 Oyna ve pratik yap")
                }
            } else {
                estado.palabraActual?.let { palabra ->
                    Text(
                        if (aprendeTurco) {
                            "Palabra ${estado.indiceActual + 1} de ${estado.palabras.size}"
                        } else {
                            "Kelime ${estado.indiceActual + 1} / ${estado.palabras.size}"
                        },
                        color = oscuro
                    )

                    val palabraObjetivo = if (aprendeTurco) palabra.turco else palabra.espanol
                    val traduccionNativa = if (aprendeTurco) palabra.espanol else palabra.turco

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(palabra.emoji, fontSize = 78.sp)
                            // Idioma que está aprendiendo: grande y protagonista.
                            Text(
                                text = palabraObjetivo,
                                fontSize = 35.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = turquesa,
                                textAlign = TextAlign.Center
                            )
                            // Idioma nativo: traducción para ayudar a comprender.
                            Text(
                                text = traduccionNativa,
                                fontSize = 19.sp,
                                color = oscuro,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    // Pronuncia la palabra en el idioma que aprende.
                                    errorPronunciacion = !ttsManager.hablar(
                                        palabraObjetivo,
                                        idiomaObjetivo
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = turquesa)
                            ) {
                                Text(
                                    if (aprendeTurco) "🔊 Escuchar en turco" else "🔊 İspanyolca dinle",
                                    color = Color.White
                                )
                            }
                            if (errorPronunciacion) {
                                Text(
                                    if (aprendeTurco)
                                        "La voz turca no está disponible. Revisa los idiomas de voz de Android."
                                    else
                                        "İspanyolca ses kullanılamıyor. Cihazın ses ayarlarını kontrol et.",
                                    color = Color(0xFFB33939),
                                    textAlign = TextAlign.Center
                                )
                            }
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
                            Text(if (aprendeTurco) "Anterior" else "Önceki")
                        }
                        Button(
                            onClick = { viewModel.siguientePalabra(avatar) },
                            modifier = Modifier.weight(1f)
                        ) {
                            val ultima = estado.indiceActual == estado.palabras.lastIndex
                            Text(
                                if (aprendeTurco) {
                                    if (ultima) "Finalizar" else "Siguiente"
                                } else {
                                    if (ultima) "Bitir" else "Sonraki"
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(onClick = onAbrirCamara, modifier = Modifier.fillMaxWidth()) {
            Text(if (aprendeTurco) "📷 Busca y fotografía" else "📷 Bul ve fotoğrafını çek")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text(if (aprendeTurco) "Volver a categorías" else "Kategorilere dön")
        }
    }
}

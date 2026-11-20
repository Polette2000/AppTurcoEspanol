package com.example.appturcoespanol.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import com.example.appturcoespanol.viewmodel.JuegoViewModel

private val cremaJuego = Color(0xFFFFF5E5)
private val turquesaJuego = Color(0xFF197E89)
private val oscuroJuego = Color(0xFF243A53)
private val mentaJuego = Color(0xFFBCE9CE)
private val coralJuego = Color(0xFFFFD7CB)
private val amarilloJuego = Color(0xFFFFD579)

@Composable
fun JuegoScreen(
    categoria: CategoriaAprendizaje,
    avatar: Avatar,
    idiomaNativo: Idioma,
    viewModel: JuegoViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val mensajeGuardado by viewModel.mensajeGuardado.collectAsStateWithLifecycle()
    val espanolNativo = idiomaNativo == Idioma.ESPANOL
    val idiomaObjetivo = if (espanolNativo) Idioma.TURCO else Idioma.ESPANOL

    val contexto = LocalContext.current
    val tts = remember(contexto) { TextToSpeechManager(contexto) }
    DisposableEffect(tts) {
        onDispose { tts.liberar() }
    }

    // Reiniciamos la actividad al cambiar categoria, idioma o avatar.
    LaunchedEffect(categoria, idiomaNativo, avatar) {
        viewModel.cargarCategoria(categoria, idiomaNativo, avatar)
    }

    // En el juego de Gato es necesario escuchar antes de responder.
    var audioEscuchado by remember(
        categoria, idiomaNativo, avatar, estado.indicePregunta
    ) { mutableStateOf(false) }
    var errorAudio by remember(
        categoria, idiomaNativo, avatar, estado.indicePregunta
    ) { mutableStateOf(false) }

    LaunchedEffect(estado.indicePregunta, avatar) {
        tts.detener()
    }

    val titulo = when (avatar) {
        Avatar.ZORRO -> if (espanolNativo) "🔎 ¡Explora y encuentra!" else "🔎 Keşfet ve bul!"
        Avatar.PANDA -> if (espanolNativo) "🧩 Reto de palabras" else "🧩 Kelime bulmacası"
        Avatar.GATO -> if (espanolNativo) "🎧 Escucha y adivina" else "🎧 Dinle ve tahmin et"
    }

    Scaffold(
        containerColor = cremaJuego,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cremaJuego)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        if (espanolNativo) "← Volver a la lección" else "← Derse dön",
                        color = turquesaJuego
                    )
                }
            }
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                titulo,
                color = oscuroJuego,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                "${avatar.emoji} ${if (espanolNativo) categoria.nombre else categoria.nombreTurco}",
                color = turquesaJuego,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            if (estado.preguntas.isEmpty() || estado.avatar != avatar ||
                estado.idiomaNativo != idiomaNativo || estado.categoria != categoria
            ) {
                Text(if (espanolNativo) "Preparando el juego..." else "Oyun hazırlanıyor...")

            } else if (estado.finalizado) {
                Text("🏆", fontSize = 72.sp)
                Text(
                    if (espanolNativo) "¡Aventura terminada!" else "Macera tamamlandı!",
                    color = oscuroJuego,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (espanolNativo) {
                        "${estado.aciertos} de ${estado.preguntas.size} aciertos"
                    } else {
                        "${estado.preguntas.size} sorudan ${estado.aciertos} doğru cevap"
                    },
                    color = oscuroJuego,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = mentaJuego),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        if (espanolNativo) "⭐ ¡Buen trabajo!" else "⭐ Aferin!",
                        modifier = Modifier.padding(18.dp),
                        color = oscuroJuego
                    )
                }
                mensajeGuardado?.let { mensaje ->
                    val traducido = if (espanolNativo) mensaje else when {
                        mensaje.startsWith("Guardando") -> "Puan kaydediliyor..."
                        mensaje.startsWith("✅") -> "✅ En iyi puan kaydedildi."
                        else -> "⚠️ Puan kaydedilemedi."
                    }
                    Text(traducido, color = oscuroJuego)
                }
                Button(
                    onClick = viewModel::reiniciarJuego,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = turquesaJuego)
                ) {
                    Text(if (espanolNativo) "Volver a jugar" else "Yeniden oyna")
                }

            } else {
                estado.preguntaActual?.let { pregunta ->
                    Text(
                        if (espanolNativo) {
                            "Reto ${estado.indicePregunta + 1} de ${estado.preguntas.size}"
                        } else {
                            "Görev ${estado.indicePregunta + 1} / ${estado.preguntas.size}"
                        },
                        color = oscuroJuego
                    )
                    LinearProgressIndicator(
                        progress = {
                            (estado.indicePregunta + 1f) / estado.preguntas.size
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = turquesaJuego,
                        trackColor = mentaJuego
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (avatar) {
                                Avatar.ZORRO -> {
                                    Text("🧭", fontSize = 56.sp)
                                    Text(
                                        if (espanolNativo) {
                                            "Busca la imagen de esta palabra:"
                                        } else {
                                            "Bu kelimenin görselini bul:"
                                        },
                                        color = oscuroJuego,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        pregunta.pista,
                                        color = turquesaJuego,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 30.sp
                                    )
                                }

                                Avatar.PANDA -> {
                                    Text("🧩", fontSize = 56.sp)
                                    Text(
                                        if (espanolNativo) {
                                            "Elige la traducción en turco:"
                                        } else {
                                            "İspanyolca çeviriyi seç:"
                                        },
                                        color = oscuroJuego,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        pregunta.pista,
                                        color = turquesaJuego,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Avatar.GATO -> {
                                    Text("🎧", fontSize = 62.sp)
                                    Text(
                                        if (espanolNativo) {
                                            "Escucha la palabra y elige su significado:"
                                        } else {
                                            "Kelimeyi dinle ve anlamını seç:"
                                        },
                                        color = oscuroJuego,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = {
                                            val pudoHablar = tts.hablar(
                                                pregunta.pista,
                                                idiomaObjetivo
                                            )
                                            errorAudio = !pudoHablar
                                            if (pudoHablar) audioEscuchado = true
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = turquesaJuego
                                        )
                                    ) {
                                        Text(
                                            if (espanolNativo) "🔊 Escuchar palabra" else "🔊 Kelimeyi dinle"
                                        )
                                    }
                                    if (errorAudio) {
                                        Text(
                                            if (espanolNativo) {
                                                "No se pudo reproducir. Inténtalo otra vez."
                                            } else {
                                                "Ses oynatılamadı. Yeniden dene."
                                            },
                                            color = Color(0xFFB33939),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    if (!audioEscuchado) {
                                        Text(
                                            if (espanolNativo) {
                                                "Pulsa escuchar para activar las respuestas."
                                            } else {
                                                "Cevapları açmak için dinleye bas."
                                            },
                                            color = oscuroJuego,
                                            textAlign = TextAlign.Center,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val respondida = estado.respuestaSeleccionada != null
                    val habilitarRespuestas = avatar != Avatar.GATO || audioEscuchado

                    pregunta.opciones.forEach { opcion ->
                        val correcta = opcion == pregunta.respuestaCorrecta
                        val seleccionada = opcion == estado.respuestaSeleccionada
                        val fondo = when {
                            respondida && correcta -> mentaJuego
                            respondida && seleccionada -> coralJuego
                            else -> Color.White
                        }
                        val borde = when {
                            respondida && correcta -> turquesaJuego
                            respondida && seleccionada -> Color(0xFFB33939)
                            else -> Color(0xFFC4D5D7)
                        }

                        OutlinedButton(
                            onClick = { viewModel.responder(opcion) },
                            enabled = !respondida && habilitarRespuestas,
                            modifier = Modifier.fillMaxWidth().height(
                                if (avatar == Avatar.ZORRO) 82.dp else 58.dp
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(2.dp, borde),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = fondo,
                                contentColor = oscuroJuego,
                                disabledContainerColor = fondo,
                                disabledContentColor = oscuroJuego
                            )
                        ) {
                            if (avatar == Avatar.ZORRO) {
                                // Mostramos circulos nativos para evitar el emoji amarillo roto.
                                if (categoria == CategoriaAprendizaje.COLORES) {
                                    val colorCirculo = when (opcion) {
                                        "🔴" -> Color.Red
                                        "🔵" -> Color.Blue
                                        "🟡" -> Color.Yellow
                                        else -> Color.Gray
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(49.dp)
                                            .background(colorCirculo, CircleShape)
                                    )
                                } else {
                                    Text(opcion, fontSize = 42.sp)
                                }
                            } else {
                                Text(
                                    opcion,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = respondida,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (estado.esCorrecta) mentaJuego
                                else amarilloJuego
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    if (estado.esCorrecta) {
                                        if (espanolNativo) "✅ ¡Correcto!" else "✅ Doğru!"
                                    } else {
                                        if (espanolNativo) "💛 ¡Sigue practicando!"
                                        else "💛 Tekrar dene!"
                                    },
                                    color = oscuroJuego,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!estado.esCorrecta) {
                                    Text(
                                        if (espanolNativo) {
                                            "Respuesta correcta: ${pregunta.respuestaCorrecta}"
                                        } else {
                                            "Doğru cevap: ${pregunta.respuestaCorrecta}"
                                        },
                                        color = oscuroJuego
                                    )
                                }
                                Button(
                                    onClick = { viewModel.siguientePregunta(avatar) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = turquesaJuego
                                    )
                                ) {
                                    val ultimo = estado.indicePregunta ==
                                            estado.preguntas.lastIndex
                                    Text(
                                        if (espanolNativo) {
                                            if (ultimo) "Ver resultados" else "Siguiente reto"
                                        } else {
                                            if (ultimo) "Sonuçları gör" else "Sonraki görev"
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        if (espanolNativo) "Puntos: ${estado.aciertos}"
                        else "Puan: ${estado.aciertos}",
                        fontWeight = FontWeight.SemiBold,
                        color = oscuroJuego
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

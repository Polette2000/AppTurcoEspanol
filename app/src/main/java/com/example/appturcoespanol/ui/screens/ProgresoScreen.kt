package com.example.appturcoespanol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.ProgresoAprendizaje
import com.example.appturcoespanol.viewmodel.ProgresoViewModel

@Composable
fun ProgresoScreen(
    avatar: Avatar,
    idiomaNativo: Idioma,
    viewModel: ProgresoViewModel,
    onVolver: () -> Unit,
    permitirReinicio: Boolean = false
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    val esEspanol = idiomaNativo == Idioma.ESPANOL

    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val oscuro = Color(0xFF243A53)
    val menta = Color(0xFFBCE9CE)
    val coral = Color(0xFFF28778)
    val nombreAvatar = if (esEspanol) avatar.nombre else when (avatar) {
        Avatar.ZORRO -> "Tilki"
        Avatar.PANDA -> "Panda"
        Avatar.GATO -> "Kedi"
    }

    LaunchedEffect(avatar) { viewModel.cargarProgreso(avatar) }

    Scaffold(
        containerColor = crema,
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(crema)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                OutlinedButton(
                    onClick = onVolver,
                    enabled = !estado.reiniciando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        if (esEspanol) "← Volver a categorías" else "← Kategorilere dön",
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (esEspanol) "📊 Mi progreso" else "📊 İlerlemem",
                color = oscuro,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text("${avatar.emoji} $nombreAvatar", color = turquesa, fontSize = 20.sp)
            Text(
                text = if (esEspanol) "¡Mira cuánto has aprendido!" else "Ne kadar öğrendiğine bak!",
                color = oscuro,
                textAlign = TextAlign.Center
            )

            when {
                estado.cargando -> {
                    CircularProgressIndicator(color = turquesa)
                    Text(
                        if (esEspanol) "Cargando progreso..." else "İlerleme yükleniyor...",
                        color = oscuro
                    )
                }
                estado.error != null -> {
                    Text(
                        text = if (esEspanol) "No se pudo cargar el progreso." else "İlerleme yüklenemedi.",
                        color = Color(0xFFB33939),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    val categorias = CategoriaAprendizaje.paraAvatar(avatar)
                    val completadas = categorias.count { categoria ->
                        estado.progresos[categoria]?.leccionCompletada == true
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = menta)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (esEspanol) "Lecciones completadas" else "Tamamlanan dersler",
                                fontWeight = FontWeight.Bold,
                                color = oscuro
                            )
                            Text(
                                "$completadas / ${categorias.size}",
                                fontSize = 29.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = turquesa
                            )
                            LinearProgressIndicator(
                                progress = { if (categorias.isEmpty()) 0f else completadas.toFloat() / categorias.size },
                                modifier = Modifier.fillMaxWidth(),
                                color = turquesa,
                                trackColor = Color.White
                            )
                        }
                    }

                    categorias.forEach { categoria ->
                        val progreso = estado.progresos[categoria] ?: ProgresoAprendizaje()
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${categoria.emoji} ${if (esEspanol) categoria.nombre else categoria.nombreTurco}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = oscuro
                                )
                                Text(
                                    if (progreso.leccionCompletada) {
                                        if (esEspanol) "✅ Lección completada" else "✅ Ders tamamlandı"
                                    } else {
                                        if (esEspanol) "⏳ Lección pendiente" else "⏳ Ders henüz tamamlanmadı"
                                    },
                                    color = turquesa
                                )
                                Text(
                                    if (esEspanol) {
                                        "🏆 Mejor puntaje: ${progreso.mejorPuntaje}"
                                    } else {
                                        "🏆 En iyi puan: ${progreso.mejorPuntaje}"
                                    },
                                    color = oscuro
                                )
                            }
                        }
                    }

                    if (estado.reiniciando) {
                        CircularProgressIndicator(color = turquesa)
                        Text(
                            if (esEspanol) "Reiniciando progreso..." else "İlerleme sıfırlanıyor...",
                            color = oscuro
                        )
                    }
                    if (estado.mensajeReinicio != null) {
                        Text(
                            if (esEspanol) "✅ Progreso reiniciado." else "✅ İlerleme sıfırlandı.",
                            color = turquesa
                        )
                    }
                    if (estado.errorReinicio != null) {
                        Text(
                            if (esEspanol) "No se pudo reiniciar el progreso." else "İlerleme sıfırlanamadı.",
                            color = Color(0xFFB33939)
                        )
                    }

                    // Accion de borrado protegida; por defecto oculta al niño.
                    if (permitirReinicio) {
                        Button(
                            onClick = { mostrarConfirmacion = true },
                            enabled = !estado.reiniciando,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = coral, contentColor = oscuro)
                        ) {
                            Text(
                                if (esEspanol) "Reiniciar progreso" else "İlerlemeyi sıfırla",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (permitirReinicio && mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = {
                Text(if (esEspanol) "¿Reiniciar progreso?" else "İlerleme sıfırlansın mı?")
            },
            text = {
                Text(
                    if (esEspanol) {
                        "Se borrarán las lecciones completadas y los puntajes de ${avatar.nombre}. " +
                                "No se puede deshacer."
                    } else {
                        "$nombreAvatar için tamamlanan dersler ve puanlar silinecek. " +
                                "Bu işlem geri alınamaz."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    viewModel.reiniciarProgreso(avatar)
                }) {
                    Text(
                        if (esEspanol) "Sí, reiniciar" else "Evet, sıfırla",
                        color = Color(0xFFB33939)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text(if (esEspanol) "Cancelar" else "İptal")
                }
            }
        )
    }
}

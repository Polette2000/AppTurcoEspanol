package com.example.appturcoespanol.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
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
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.viewmodel.CategoriasViewModel

@Composable
fun CategoriasScreen(
    viewModel: CategoriasViewModel,
    idiomaNativo: Idioma,
    avatar: Avatar,
    onVolver: () -> Unit,
    onAbrirLeccion: (CategoriaAprendizaje) -> Unit,
    onVerProgreso: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val espanolNativo = idiomaNativo == Idioma.ESPANOL
    val categorias = CategoriaAprendizaje.paraAvatar(avatar)
    // Si se cambió de avatar, no mostrar una selección del mundo anterior.
    val categoriaSeleccionada = estado.categoriaSeleccionada?.takeIf { it in categorias }

    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val oscuro = Color(0xFF243A53)
    val menta = Color(0xFFBCE9CE)

    val tituloMundo = when (avatar) {
        Avatar.ZORRO -> if (espanolNativo) "Mundo Explorador" else "Keşif Dünyası"
        Avatar.PANDA -> if (espanolNativo) "Mundo de los Desafíos" else "Görevler Dünyası"
        Avatar.GATO -> if (espanolNativo) "Mundo de las Palabras" else "Kelimeler Dünyası"
    }

    val explicacionMundo = when (avatar) {
        Avatar.ZORRO -> if (espanolNativo)
            "Explora animales, frutas y la naturaleza con imágenes."
        else "Hayvanları, meyveleri ve doğayı görsellerle keşfet."
        Avatar.PANDA -> if (espanolNativo)
            "Aprende colores, formas y números con desafíos."
        else "Renkleri, şekilleri ve sayıları oyunlarla öğren."
        Avatar.GATO -> if (espanolNativo)
            "Escucha saludos, familia y emociones para comunicarte."
        else "Selamlaşmaları, aileyi ve duyguları dinleyerek öğren."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(crema)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(avatar.emoji, fontSize = 62.sp)
        Text(
            tituloMundo,
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold,
            color = oscuro,
            textAlign = TextAlign.Center
        )
        Text(
            explicacionMundo,
            color = oscuro,
            textAlign = TextAlign.Center
        )
        Text(
            if (espanolNativo) "🇹🇷 Aprendiendo turco" else "🇪🇸 İspanyolca öğreniyorsun",
            fontWeight = FontWeight.SemiBold,
            color = turquesa
        )
        Text(
            if (espanolNativo) "Elige una aventura" else "Bir macera seç",
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = oscuro
        )

        categorias.forEach { categoria ->
            val seleccionada = categoriaSeleccionada == categoria
            val palabraNueva = if (espanolNativo) categoria.nombreTurco else categoria.nombre
            val traduccion = if (espanolNativo) categoria.nombre else categoria.nombreTurco

            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    viewModel.seleccionarCategoria(categoria)
                },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (seleccionada) menta else Color.White
                ),
                border = BorderStroke(
                    if (seleccionada) 3.dp else 1.dp,
                    if (seleccionada) turquesa else Color(0xFFD5E2E2)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(categoria.emoji, fontSize = 37.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            palabraNueva,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = oscuro
                        )
                        Text(traduccion, color = turquesa)
                    }
                }
            }
        }

        AnimatedVisibility(visible = categoriaSeleccionada != null) {
            categoriaSeleccionada?.let { categoria ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = menta)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            if (espanolNativo) "¡A explorar! ✨" else "Keşfe başlayalım! ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = oscuro
                        )
                        Text(
                            if (espanolNativo) categoria.descripcion else categoria.descripcionTurco,
                            color = oscuro
                        )
                        Button(
                            onClick = { onAbrirLeccion(categoria) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = turquesa),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (espanolNativo) "📚 Aprender palabras" else "📚 Kelimeleri öğren")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onVerProgreso,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = turquesa),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (espanolNativo) "📊 Mi progreso" else "📊 İlerlemem")
        }
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (espanolNativo) "Cambiar avatar" else "Avatarı değiştir")
        }
    }
}

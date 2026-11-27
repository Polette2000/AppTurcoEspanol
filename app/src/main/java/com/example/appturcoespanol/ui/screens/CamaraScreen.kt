package com.example.appturcoespanol.ui.screens

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Idioma

/**
 * Mundo Explorador de Zorro.
 * Usa la cámara externa mediante ACTION_IMAGE_CAPTURE/TakePicturePreview.
 * No necesita ni solicita permiso CAMERA de nuestra aplicación.
 * La miniatura solo se mantiene en memoria: no se guarda ni se envía.
 */
@Composable
fun CamaraScreen(
    categoria: CategoriaAprendizaje,
    onVolver: () -> Unit,
    idiomaNativo: Idioma = Idioma.ESPANOL
) {
    val espanol = idiomaNativo == Idioma.ESPANOL
    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val oscuro = Color(0xFF243A53)

    var mostrarAviso by rememberSaveable { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var foto by remember { mutableStateOf<Bitmap?>(null) }

    // El sistema se encarga de abrir la aplicación de cámara del teléfono.
    // Si la persona cancela, el resultado es null.
    val lanzarCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen: Bitmap? ->
        foto = imagen
        mensajeError = if (imagen == null) {
            if (espanol) "No se tomó ninguna fotografía. Puedes intentarlo otra vez."
            else "Fotoğraf çekilmedi. Yeniden deneyebilirsin."
        } else null
    }

    val mision = when (categoria) {
        CategoriaAprendizaje.ANIMALES -> if (espanol)
            "Busca un animal de juguete o un dibujo de un animal y fotografíalo."
        else "Oyuncak bir hayvan veya hayvan resmi bul ve fotoğrafını çek."

        CategoriaAprendizaje.FRUTAS -> if (espanol)
            "Busca una fruta o una imagen de fruta y fotografíala."
        else "Bir meyve veya meyve resmi bul ve fotoğrafını çek."

        CategoriaAprendizaje.NATURALEZA -> if (espanol)
            "Busca una hoja, una flor u otro objeto de la naturaleza y fotografíalo."
        else "Bir yaprak, çiçek veya doğadan bir nesne bul ve fotoğrafını çek."

        else -> if (espanol)
            "Busca y fotografía un objeto relacionado con esta categoría."
        else "Bu kategoriyle ilgili bir nesne bul ve fotoğrafını çek."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(crema)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🦊 📷", fontSize = 46.sp)
        Text(
            text = if (espanol) "¡Misión exploradora!" else "Keşif görevi!",
            color = oscuro,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (espanol) categoria.nombre else categoria.nombreTurco,
            color = turquesa,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = mision,
                modifier = Modifier.padding(20.dp),
                color = oscuro,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = if (espanol)
                "Con ayuda de un adulto, fotografía solo objetos o dibujos; nunca personas. La foto se muestra temporalmente y EsTurk no la almacena ni la envía."
            else "Bir yetişkinin yardımıyla yalnızca nesnelerin veya çizimlerin fotoğrafını çek; insanların değil. EsTurk fotoğrafı saklamaz veya göndermez.",
            color = oscuro,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = { mostrarAviso = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = turquesa)
        ) {
            Text(if (espanol) "📷 Fotografiar objeto" else "📷 Nesnenin fotoğrafını çek")
        }

        mensajeError?.let { error ->
            Text(
                text = error,
                color = Color(0xFFB33939),
                textAlign = TextAlign.Center
            )
        }

        foto?.let { imagen ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (espanol) "Fotografía tomada" else "Fotoğraf çekildi",
                        color = turquesa,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        bitmap = imagen.asImageBitmap(),
                        contentDescription = if (espanol) "Vista previa de la fotografía" else "Fotoğraf önizlemesi",
                        modifier = Modifier.fillMaxWidth().height(230.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        if (espanol) "¿El objeto coincide con la misión? ¡Muy bien!"
                        else "Nesne görevle eşleşiyor mu? Aferin!",
                        color = oscuro,
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(onClick = { foto = null; mensajeError = null }) {
                        Text(if (espanol) "Eliminar foto" else "Fotoğrafı sil")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text(if (espanol) "Volver al vocabulario" else "Kelimelere dön")
        }
    }

    // Aviso educativo propio de EsTurk (no es un permiso de Android).
    if (mostrarAviso) {
        AlertDialog(
            onDismissRequest = { mostrarAviso = false },
            title = {
                Text(if (espanol) "¿Abrir la cámara?" else "Kamera açılsın mı?")
            },
            text = {
                Text(
                    if (espanol)
                        "Se abrirá la aplicación de cámara de tu celular. Con ayuda de un adulto, fotografía solamente objetos o dibujos. Puedes cancelar en cualquier momento."
                    else "Telefonunun kamera uygulaması açılacak. Bir yetişkinin yardımıyla yalnızca nesnelerin veya çizimlerin fotoğrafını çek. İstediğin zaman iptal edebilirsin."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarAviso = false
                    mensajeError = null
                    try {
                        lanzarCamara.launch(null)
                    } catch (_: ActivityNotFoundException) {
                        mensajeError = if (espanol)
                            "Este celular no tiene una aplicación de cámara disponible."
                        else "Bu telefonda kullanılabilir bir kamera uygulaması yok."
                    } catch (_: SecurityException) {
                        mensajeError = if (espanol)
                            "Android bloqueó la cámara. Comprueba que el permiso CAMERA no siga declarado en el manifiesto combinado y vuelve a ejecutar la app."
                        else "Android kamerayı engelledi. Birleştirilmiş manifest dosyasında CAMERA izninin bulunmadığını kontrol edip uygulamayı yeniden çalıştır."
                    } catch (_: Exception) {
                        mensajeError = if (espanol)
                            "No se pudo abrir la cámara. Comprueba que la cámara normal del celular funcione."
                        else "Kamera açılamadı. Telefonun normal kamera uygulamasının çalıştığından emin ol."
                    }
                }) {
                    Text(if (espanol) "Abrir cámara" else "Kamerayı aç")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAviso = false }) {
                    Text(if (espanol) "Cancelar" else "İptal")
                }
            }
        )
    }
}

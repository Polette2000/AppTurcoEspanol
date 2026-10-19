package com.example.appturcoespanol.ui.screens

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.repository.VocabularioRepository
import com.example.appturcoespanol.tts.TextToSpeechManager

@Composable
fun CamaraScreen(
    categoria: CategoriaAprendizaje,
    onVolver: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    // Las palabras ya existen en nuestro repositorio de vocabulario.
    val palabras = remember(categoria) {
        VocabularioRepository.obtenerPalabras(categoria)
    }

    var indicePalabra by remember(categoria) {
        mutableIntStateOf(0)
    }

    // Solo mantenemos una miniatura en memoria: no creamos archivos.
    var fotoCapturada by remember(categoria) {
        mutableStateOf<Bitmap?>(null)
    }

    var mensajeCamara by remember(categoria) {
        mutableStateOf<String?>(null)
    }

    var errorPronunciacion by remember(categoria) {
        mutableStateOf(false)
    }

    val ttsManager = remember(context) {
        TextToSpeechManager(context)
    }

    DisposableEffect(ttsManager) {
        onDispose {
            ttsManager.liberar()
        }
    }

    // ActivityResult abre la aplicacion de camara del sistema.
    // TakePicturePreview devuelve una miniatura Bitmap, sin archivo permanente.
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoCapturada = bitmap
            mensajeCamara = "Foto recibida. Puedes compararla con la palabra."
        } else {
            mensajeCamara = "No se tomó ninguna fotografía."
        }
    }

    // Cambiar de palabra elimina la miniatura anterior de esta pantalla.
    LaunchedEffect(indicePalabra) {
        fotoCapturada = null
        mensajeCamara = null
        errorPronunciacion = false
        ttsManager.detener()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📷 Busca y fotografía",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Categoría: ${categoria.nombre}",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Busca un objeto, juguete o dibujo que represente la palabra. " +
                    "Fotografía solo objetos, nunca personas.",
            textAlign = TextAlign.Center
        )

        if (palabras.isEmpty()) {
            Text("No hay palabras disponibles en esta categoría.")
        } else {
            val palabra = palabras[indicePalabra]

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = palabra.emoji, fontSize = 64.sp)
                    Text(
                        text = palabra.espanol,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = palabra.turco,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Palabra ${indicePalabra + 1} de ${palabras.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = {
                    errorPronunciacion = !ttsManager.hablar(palabra.espanol)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔊 Escuchar en español")
            }

            if (errorPronunciacion) {
                Text(
                    text = "La voz aún no está lista o no está disponible.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    mensajeCamara = null
                    try {
                        camaraLauncher.launch(null)
                    } catch (e: ActivityNotFoundException) {
                        mensajeCamara = "No hay una aplicación de cámara disponible."
                    } catch (e: SecurityException) {
                        mensajeCamara = "Android no permitió abrir la cámara."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📷 Tomar fotografía de un objeto")
            }

            mensajeCamara?.let { mensaje ->
                Text(
                    text = mensaje,
                    textAlign = TextAlign.Center
                )
            }

            fotoCapturada?.let { bitmap ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Vista previa temporal")
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Vista previa del objeto fotografiado",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentScale = ContentScale.Fit
                        )
                        OutlinedButton(
                            onClick = {
                                fotoCapturada = null
                                mensajeCamara = "Vista previa eliminada."
                            }
                        ) {
                            Text("Eliminar fotografía")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { indicePalabra -= 1 },
                    enabled = indicePalabra > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anterior")
                }

                Button(
                    onClick = { indicePalabra += 1 },
                    enabled = indicePalabra < palabras.lastIndex,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Siguiente")
                }
            }
        }

        Text(
            text = "La aplicación no identifica el objeto ni guarda la foto en DataStore.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al vocabulario")
        }
    }
}

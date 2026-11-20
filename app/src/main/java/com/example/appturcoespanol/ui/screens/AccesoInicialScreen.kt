package com.example.appturcoespanol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.tts.TextToSpeechManager

private val cremaInicio = Color(0xFFFFF5E5)
private val turquesaInicio = Color(0xFF197E89)
private val oscuroInicio = Color(0xFF243A53)

@Composable
fun AccesoInicialScreen(
    onCrearCuenta: () -> Unit,
    onIniciarSesion: () -> Unit,
    onVolver: () -> Unit,
    idiomaInterfaz: Idioma = Idioma.ESPANOL
) {
    val turco = idiomaInterfaz == Idioma.TURCO
    Column(
        modifier = Modifier.fillMaxSize().background(cremaInicio)
            .verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text("EsTurk", fontSize = 38.sp, color = turquesaInicio,
            fontWeight = FontWeight.ExtraBold)
        Text("🦊 🐼 🐱", fontSize = 49.sp)
        Text(
            text = if (turco) "Maceraya hoş geldin!" else "¡Bienvenido a la aventura!",
            fontSize = 25.sp, fontWeight = FontWeight.Bold,
            color = oscuroInicio, textAlign = TextAlign.Center
        )
        Text(
            text = if (turco) "Nasıl başlamak istersin?" else "¿Cómo quieres comenzar?",
            color = oscuroInicio, fontSize = 17.sp, textAlign = TextAlign.Center
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text(
                    text = if (turco) "🔐 Bir yetişkinin yardımıyla giriş" else
                        "🔐 Acceso con ayuda de un adulto",
                    color = oscuroInicio, fontWeight = FontWeight.Bold, fontSize = 17.sp
                )
                Text(
                    text = if (turco) "E-posta ve şifre ile bir hesap oluştur." else
                        "Crea una cuenta con correo y contraseña.",
                    color = oscuroInicio, fontSize = 13.sp
                )
                Button(
                    onClick = onCrearCuenta,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = turquesaInicio)
                ) {
                    Text(if (turco) "➕ Hesap oluştur" else "➕ Crear cuenta",
                        fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                OutlinedButton(
                    onClick = onIniciarSesion,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Text(if (turco) "👤 Giriş yap" else "👤 Iniciar sesión",
                        color = turquesaInicio, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
        TextButton(onClick = onVolver) {
            Text(if (turco) "← Hoş geldin ekranına dön" else "← Volver a bienvenida",
                color = turquesaInicio)
        }
        Text(
            text = if (turco) "Hesaplar ve ilerleme yalnızca bu cihazda saklanır." else
                "Las cuentas y avances se guardan solamente en este celular.",
            color = oscuroInicio, fontSize = 12.sp, textAlign = TextAlign.Center
        )
    }
}

private data class PalabraDemo(val espanol: String, val turco: String, val emoji: String)

/** Demostracion sin cuenta: no almacena ninguna puntuacion. */
@Composable
fun ModoInvitadoScreen(onVolver: () -> Unit, onConfigurar: () -> Unit) {
    val palabras = remember {
        listOf(
            PalabraDemo("Gato", "Kedi", "🐱"),
            PalabraDemo("Perro", "Köpek", "🐶"),
            PalabraDemo("Manzana", "Elma", "🍎")
        )
    }
    var indice by rememberSaveable { mutableIntStateOf(0) }
    var eleccion by rememberSaveable { mutableStateOf<String?>(null) }
    var puntaje by rememberSaveable { mutableIntStateOf(0) }
    var errorVoz by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val voz = remember(context) { TextToSpeechManager(context) }
    DisposableEffect(voz) { onDispose { voz.liberar() } }

    Column(
        modifier = Modifier.fillMaxSize().background(cremaInicio)
            .verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🎮 ¡Vamos a probar!", fontSize = 27.sp,
            fontWeight = FontWeight.Bold, color = oscuroInicio)
        Text("Esta demostración no guarda puntajes.", color = oscuroInicio)
        if (indice < palabras.size) {
            val palabra = palabras[indice]
            Text("Reto ${indice + 1} de ${palabras.size}", color = turquesaInicio)
            Card(modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(palabra.emoji, fontSize = 60.sp)
                    Text("¿Cómo se dice en turco?", color = oscuroInicio)
                    Text(palabra.espanol, fontWeight = FontWeight.ExtraBold,
                        fontSize = 29.sp, color = turquesaInicio)
                    Button(onClick = {
                        errorVoz = !voz.hablar(palabra.turco, Idioma.TURCO)
                    }, colors = ButtonDefaults.buttonColors(containerColor = turquesaInicio)) {
                        Text("🔊 Escuchar en turco")
                    }
                }
            }
            if (errorVoz) {
                Text("No se encontró voz turca en este dispositivo.",
                    color = Color(0xFFB33939))
            }
            palabras.forEach { opcion ->
                OutlinedButton(
                    onClick = {
                        if (eleccion == null) {
                            eleccion = opcion.turco
                            if (opcion.turco == palabra.turco) puntaje++
                        }
                    },
                    enabled = eleccion == null,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(opcion.turco, color = oscuroInicio)
                }
            }
            if (eleccion != null) {
                Text(
                    if (eleccion == palabra.turco) "✅ ¡Correcto!"
                    else "💛 La respuesta correcta es ${palabra.turco}",
                    color = oscuroInicio, fontWeight = FontWeight.SemiBold
                )
                Button(onClick = {
                    indice++
                    eleccion = null
                    errorVoz = false
                }, colors = ButtonDefaults.buttonColors(containerColor = turquesaInicio)) {
                    Text("Siguiente →")
                }
            }
        } else {
            Text("🏆 ¡Terminaste!", color = oscuroInicio, fontSize = 25.sp)
            Text("$puntaje de ${palabras.size} respuestas correctas", color = oscuroInicio)
            Button(onClick = onConfigurar,
                colors = ButtonDefaults.buttonColors(containerColor = turquesaInicio)) {
                Text("Guardar mi aventura con ayuda de un adulto")
            }
            TextButton(onClick = { indice = 0; eleccion = null; puntaje = 0 }) {
                Text("Jugar de nuevo", color = turquesaInicio)
            }
        }
        TextButton(onClick = onVolver) { Text("← Volver", color = turquesaInicio) }
    }
}

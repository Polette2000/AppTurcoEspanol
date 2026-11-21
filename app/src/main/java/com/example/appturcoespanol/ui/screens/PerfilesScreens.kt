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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.PerfilInfantil
import com.example.appturcoespanol.viewmodel.PerfilesViewModel

private val cremaP = Color(0xFFFFF5E5)
private val turquesaP = Color(0xFF197E89)
private val oscuroP = Color(0xFF243A53)
private val mentaP = Color(0xFFBCE9CE)
private val bordeP = Color(0xFFD8E6E4)

/** Pantalla que ve el niño al volver a abrir EsTurk. */
@Composable
fun SeleccionPerfilScreen(
    viewModel: PerfilesViewModel,
    onEntrar: (PerfilInfantil) -> Unit,
    onZonaAdultos: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(containerColor = cremaP) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text("🦊 🐼 🐱", fontSize = 45.sp)
            Text("¡Hola, explorador!", fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold, color = oscuroP,
                textAlign = TextAlign.Center)
            Text("¿Quién va a jugar hoy?", fontSize = 18.sp,
                color = turquesaP, textAlign = TextAlign.Center)

            if (estado.cargando) {
                CircularProgressIndicator(color = turquesaP)
            } else if (estado.perfiles.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(22.dp)) {
                    Text("Todavía no hay perfiles. Un adulto puede crear el primero.",
                        modifier = Modifier.padding(22.dp), color = oscuroP,
                        textAlign = TextAlign.Center)
                }
            } else {
                estado.perfiles.forEach { perfil ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onEntrar(perfil) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, bordeP),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(perfil.avatar.emoji, fontSize = 46.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(perfil.apodo, fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold, color = oscuroP)
                                Text(
                                    if (perfil.idiomaNativo == Idioma.ESPANOL)
                                        "🇹🇷 Aprendiendo turco" else "🇪🇸 İspanyolca öğreniyor",
                                    fontSize = 14.sp, color = turquesaP
                                )
                                Text("Toca para continuar →", fontSize = 12.sp,
                                    color = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
            estado.error?.let { Text(it, color = Color(0xFFB33939)) }
            Spacer(Modifier.height(18.dp))
            OutlinedButton(
                onClick = onZonaAdultos,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("🔒 Zona para adultos", color = turquesaP)
            }
            Text("Perfiles guardados solo en este celular.",
                color = Color(0xFF64748B), fontSize = 12.sp,
                textAlign = TextAlign.Center)
        }
    }
}

/** El adulto crea el perfil; el menor no necesita correo ni nombre real. */
@Composable
fun CrearPerfilScreen(
    viewModel: PerfilesViewModel,
    onVolver: () -> Unit,
    onPerfilCreado: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var apodo by rememberSaveable { mutableStateOf("") }
    var idioma by rememberSaveable { mutableStateOf<Idioma?>(null) }
    var avatar by rememberSaveable { mutableStateOf<Avatar?>(null) }

    LaunchedEffect(estado.perfilCreadoId) {
        if (estado.perfilCreadoId != null) {
            viewModel.consumirPerfilCreado()
            onPerfilCreado()
        }
    }

    Scaffold(containerColor = cremaP) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text("👧 Nuevo perfil infantil", fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, color = oscuroP)
            Text("Usa un apodo inventado. No escribas nombres reales ni datos personales.",
                color = oscuroP)
            OutlinedTextField(
                value = apodo,
                onValueChange = { apodo = it.take(16); viewModel.limpiarError() },
                label = { Text("Apodo (2 a 16 caracteres)") },
                placeholder = { Text("Ej.: Estrella") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text("¿Qué idioma habla el niño?", fontWeight = FontWeight.Bold,
                color = oscuroP, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(Idioma.ESPANOL to "🇪🇸 Español",
                    Idioma.TURCO to "🇹🇷 Türkçe").forEach { (valor, titulo) ->
                    OutlinedButton(
                        onClick = { idioma = valor },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(if (idioma == valor) 2.dp else 1.dp,
                            if (idioma == valor) turquesaP else bordeP),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (idioma == valor) mentaP else Color.White
                        )
                    ) { Text(titulo, color = oscuroP) }
                }
            }
            if (idioma != null) {
                Text(if (idioma == Idioma.ESPANOL) "Aprenderá turco 🇹🇷" else "İspanyolca öğrenecek 🇪🇸",
                    fontWeight = FontWeight.SemiBold, color = turquesaP)
            }
            Text("Elige su mundo favorito", fontWeight = FontWeight.Bold,
                color = oscuroP, fontSize = 18.sp)
            Avatar.entries.forEach { valor ->
                val descripcion = when (valor) {
                    Avatar.ZORRO -> "Explorar: animales, frutas y naturaleza"
                    Avatar.PANDA -> "Desafíos: colores, formas y números"
                    Avatar.GATO -> "Escuchar: saludos, familia y emociones"
                }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { avatar = valor },
                    border = BorderStroke(if (avatar == valor) 2.dp else 1.dp,
                        if (avatar == valor) turquesaP else bordeP),
                    colors = CardDefaults.cardColors(
                        containerColor = if (avatar == valor) mentaP else Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                        Text(valor.emoji, fontSize = 38.sp)
                        Column {
                            Text(valor.nombre, fontWeight = FontWeight.Bold, color = oscuroP)
                            Text(descripcion, fontSize = 12.sp, color = oscuroP)
                        }
                    }
                }
            }
            estado.error?.let { Text(it, color = Color(0xFFB33939)) }
            Button(
                onClick = { viewModel.crearPerfil(apodo, idioma, avatar) },
                enabled = !estado.guardando && !estado.cargando,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = turquesaP),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (estado.guardando) "Guardando..." else "Crear perfil")
            }
            TextButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Volver", color = turquesaP)
            }
        }
    }
}

/** Revalidacion del PIN: nunca expone el formulario de registro al niño. */
@Composable
fun VerificarAdultoScreen(
    cuentaAdulta: String,
    procesando: Boolean,
    error: String?,
    onVerificar: (String) -> Unit,
    onVolver: () -> Unit
) {
    var pin by rememberSaveable { mutableStateOf("") }
    Scaffold(containerColor = cremaP) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text("🔒", fontSize = 65.sp)
            Text("Zona para adultos", fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold, color = oscuroP)
            Text("Cuenta: $cuentaAdulta", color = oscuroP)
            Text("Un adulto debe ingresar su PIN para cambiar perfiles o ajustes.",
                color = oscuroP, textAlign = TextAlign.Center)
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit).take(6) },
                label = { Text("PIN del adulto") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) Text(error, color = Color(0xFFB33939))
            Button(
                onClick = { onVerificar(pin) },
                enabled = !procesando && pin.length == 6,
                colors = ButtonDefaults.buttonColors(containerColor = turquesaP),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) { Text(if (procesando) "Verificando..." else "Entrar") }
            TextButton(onClick = onVolver) { Text("Cancelar", color = turquesaP) }
        }
    }
}

@Composable
fun ZonaAdultoScreen(
    cantidadPerfiles: Int,
    onNuevoPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    Scaffold(containerColor = cremaP) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            Text("⚙️", fontSize = 56.sp)
            Text("Zona para adultos", fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold, color = oscuroP)
            Text("Perfiles creados: $cantidadPerfiles / 5", color = oscuroP)
            Button(
                onClick = onNuevoPerfil,
                enabled = cantidadPerfiles < 5,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = turquesaP)
            ) { Text("+ Crear perfil infantil") }
            OutlinedButton(onClick = { confirmarSalida = true },
                modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión de la cuenta adulta", color = turquesaP)
            }
            TextButton(onClick = onVolver) {
                Text("← Volver a los perfiles", color = turquesaP)
            }
            Text("Configuración académica local: no sincroniza con otros celulares.",
                color = Color(0xFF64748B), fontSize = 12.sp,
                textAlign = TextAlign.Center)
        }
    }
    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("La cuenta permanecerá guardada en este dispositivo, pero será necesario ingresar de nuevo con el PIN del adulto.") },
            confirmButton = {
                TextButton(onClick = { confirmarSalida = false; onCerrarSesion() }) {
                    Text("Cerrar sesión", color = Color(0xFFB33939))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}

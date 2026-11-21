package com.example.appturcoespanol.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.ModoAcceso
import com.example.appturcoespanol.viewmodel.RegistroAdultoViewModel

/** Formulario adulto de EsTurk: correo + contraseña, bilingüe y sin apodo. */
@Composable
fun RegistroAdultoScreen(
    viewModel: RegistroAdultoViewModel,
    onVolver: () -> Unit,
    onAccesoAutorizado: () -> Unit,
    idiomaInterfaz: Idioma = Idioma.ESPANOL,
    onSeleccionarIdioma: (Idioma) -> Unit = {}
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val esRegistro = estado.modo == ModoAcceso.REGISTRARSE
    val enTurco = idiomaInterfaz == Idioma.TURCO
    fun tr(es: String, turco: String): String = if (enTurco) turco else es

    val crema = Color(0xFFFFF5E5)
    val turquesa = Color(0xFF197E89)
    val texto = Color(0xFF243A53)
    val gris = Color(0xFF617183)
    val menta = Color(0xFFDCF4E7)
    val rojo = Color(0xFFB33939)

    var menuIdioma by rememberSaveable { mutableStateOf(false) }
    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(estado.registroCompletado) {
        if (estado.registroCompletado) onAccesoAutorizado()
    }

    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = turquesa,
        focusedLabelColor = turquesa,
        cursorColor = turquesa,
        errorBorderColor = rojo
    )

    Scaffold(containerColor = crema) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .background(crema)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVolver) {
                    Text(tr("← Volver", "← Geri dön"), color = turquesa)
                }
                Box {
                    TextButton(onClick = { menuIdioma = true }) {
                        Text(
                            "🌐 " + if (enTurco) "Türkçe ▾" else "Español ▾",
                            color = turquesa,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DropdownMenu(
                        expanded = menuIdioma,
                        onDismissRequest = { menuIdioma = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("🇪🇸 Español") },
                            onClick = {
                                onSeleccionarIdioma(Idioma.ESPANOL)
                                menuIdioma = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🇹🇷 Türkçe") },
                            onClick = {
                                onSeleccionarIdioma(Idioma.TURCO)
                                menuIdioma = false
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text("🦊 🐼 🐱", fontSize = 40.sp)
                Text(
                    "EsTurk",
                    color = turquesa,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    if (esRegistro) tr("Crear una cuenta", "Hesap oluştur")
                    else tr("Iniciar sesión", "Giriş yap"),
                    color = texto,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    if (esRegistro) tr(
                        "Un adulto configura el acceso para comenzar la aventura.",
                        "Maceraya başlamak için hesabı bir yetişkin oluşturur."
                    ) else tr(
                        "Ingresa para continuar el aprendizaje.",
                        "Öğrenmeye devam etmek için giriş yap."
                    ),
                    color = gris,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDFE9E7))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(19.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = estado.correo,
                        onValueChange = viewModel::actualizarCorreo,
                        label = { Text(tr("Correo electrónico", "E-posta adresi")) },
                        placeholder = { Text("adulto@ejemplo.com") },
                        singleLine = true,
                        enabled = !estado.procesando,
                        isError = estado.errorCorreo != null,
                        supportingText = if (estado.errorCorreo != null) {
                            { Text(traducirError(estado.errorCorreo.orEmpty(), idiomaInterfaz), color = rojo) }
                        } else null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = estado.contrasena,
                        onValueChange = viewModel::actualizarContrasena,
                        label = { Text(tr("Contraseña", "Şifre")) },
                        singleLine = true,
                        enabled = !estado.procesando,
                        isError = estado.errorContrasena != null,
                        supportingText = if (estado.errorContrasena != null) {
                            { Text(traducirError(estado.errorContrasena.orEmpty(), idiomaInterfaz), color = rojo) }
                        } else null,
                        visualTransformation = if (mostrarContrasena) {
                            VisualTransformation.None
                        } else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                Text(
                                    if (mostrarContrasena) tr("Ocultar", "Gizle") else tr("Ver", "Göster"),
                                    color = turquesa,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (esRegistro) ImeAction.Next else ImeAction.Done
                        ),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (esRegistro) {
                        Text(
                            tr("Mínimo 8 caracteres con letras y números.", "En az 8 karakter, harf ve rakam içermeli."),
                            color = gris,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = estado.confirmarContrasena,
                            onValueChange = viewModel::actualizarConfirmacion,
                            label = { Text(tr("Confirmar contraseña", "Şifreyi doğrula")) },
                            singleLine = true,
                            enabled = !estado.procesando,
                            isError = estado.errorConfirmacion != null,
                            supportingText = if (estado.errorConfirmacion != null) {
                                { Text(traducirError(estado.errorConfirmacion.orEmpty(), idiomaInterfaz), color = rojo) }
                            } else null,
                            visualTransformation = if (mostrarConfirmacion) {
                                VisualTransformation.None
                            } else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { mostrarConfirmacion = !mostrarConfirmacion }) {
                                    Text(
                                        if (mostrarConfirmacion) tr("Ocultar", "Gizle") else tr("Ver", "Göster"),
                                        color = turquesa,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            colors = coloresCampo,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.recordarSesion,
                            onCheckedChange = viewModel::actualizarRecordarSesion
                        )
                        Text(
                            tr("Mantener sesión iniciada en este celular", "Bu cihazda oturumu açık tut"),
                            color = texto,
                            fontSize = 13.sp
                        )
                    }

                    estado.mensajeGeneral?.let { mensaje ->
                        Text(traducirError(mensaje, idiomaInterfaz), color = rojo, fontSize = 13.sp)
                    }

                    Button(
                        onClick = viewModel::enviarFormulario,
                        enabled = !estado.procesando && !estado.registroCompletado,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = turquesa)
                    ) {
                        if (estado.procesando) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                if (esRegistro) tr("Crear cuenta →", "Hesap oluştur →")
                                else tr("Iniciar sesión →", "Giriş yap →"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (!esRegistro && estado.sesionGuardada) {
                        OutlinedButton(
                            onClick = viewModel::continuarSesionGuardada,
                            enabled = !estado.procesando,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(tr("Continuar sesión guardada", "Kayıtlı oturumla devam et"), color = turquesa)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (esRegistro) tr("¿Ya tienes cuenta?", "Zaten hesabın var mı?")
                    else tr("¿Primera vez aquí?", "İlk kez mi geliyorsun?"),
                    color = gris,
                    fontSize = 13.sp
                )
                TextButton(onClick = {
                    viewModel.seleccionarModo(
                        if (esRegistro) ModoAcceso.INICIAR_SESION
                        else ModoAcceso.REGISTRARSE
                    )
                }) {
                    Text(
                        if (esRegistro) tr("Ingresar", "Giriş yap")
                        else tr("Crear cuenta", "Hesap oluştur"),
                        color = turquesa,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(17.dp),
                colors = CardDefaults.cardColors(containerColor = menta)
            ) {
                Text(
                    tr(
                        "🔒 Versión académica: la cuenta y el progreso se guardan solo en este dispositivo. " +
                                "No hay recuperación automática tras desinstalar.",
                        "🔒 Akademik sürüm: hesap ve ilerleme yalnızca bu cihazda saklanır. " +
                                "Uygulamayı kaldırdıktan sonra otomatik kurtarma yoktur."
                    ),
                    modifier = Modifier.padding(13.dp),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = texto
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun traducirError(mensaje: String, idioma: Idioma): String {
    if (idioma != Idioma.TURCO) return mensaje
    return when (mensaje) {
        "Ingresa tu correo electrónico." -> "E-posta adresini gir."
        "Ingresa un correo válido." -> "Geçerli bir e-posta adresi gir."
        "Ingresa una contraseña." -> "Şifreni gir."
        "Usa 8 a 64 caracteres, incluyendo letras y números." -> "Harf ve rakam içeren 8-64 karakter kullan."
        "Las contraseñas no coinciden." -> "Şifreler eşleşmiyor."
        "Este correo ya está registrado en el dispositivo." -> "Bu e-posta bu cihazda zaten kayıtlı."
        "Correo o contraseña incorrectos, o acceso temporalmente bloqueado." -> "E-posta veya şifre yanlış ya da giriş geçici olarak engellendi."
        "No se pudo completar el acceso." -> "İşlem tamamlanamadı."
        "No se pudo leer el acceso local." -> "Yerel hesap bilgileri okunamadı."
        "No hay una sesión local disponible." -> "Bu cihazda kayıtlı oturum bulunamadı."
        "No se pudo continuar la sesión." -> "Oturum devam ettirilemedi."
        else -> "İşlem tamamlanamadı. Lütfen tekrar dene."
    }
}

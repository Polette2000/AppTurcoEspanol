package com.example.appturcoespanol.model

enum class ModoAcceso {
    REGISTRARSE,
    INICIAR_SESION
}

/** Estado del acceso académico local. Las contraseñas no se guardan en el estado persistente. */
data class RegistroAdultoUiState(
    val modo: ModoAcceso = ModoAcceso.REGISTRARSE,
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val recordarSesion: Boolean = false,
    val sesionGuardada: Boolean = false,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    val hayRegistro: Boolean? = null,
    val procesando: Boolean = false,
    val mensajeGeneral: String? = null,
    val registroCompletado: Boolean = false,
    val cuentaAutorizada: String = ""
)

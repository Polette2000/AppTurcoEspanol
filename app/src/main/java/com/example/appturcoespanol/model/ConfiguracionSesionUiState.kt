
package com.example.appturcoespanol.model

// Avatares ficticios disponibles
enum class Avatar(
    val nombre: String,
    val emoji: String
) {
    ZORRO("Zorro", "🦊"),
    PANDA("Panda", "🐼"),
    GATO("Gato", "🐱")
}

// Estado de la pantalla de configuracion
data class ConfiguracionSesionUiState(
    val avatarSeleccionado: Avatar? = null,
    val errorAvatar: String? = null,
    val confirmado: Boolean = false
)

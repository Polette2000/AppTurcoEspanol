
package com.example.appturcoespanol.model

// Idiomas disponibles en EsTurk
enum class Idioma(val nombreVisible: String) {
    TURCO("Turco / Türkçe"),
    ESPANOL("Español")
}

// Estado de la pantalla de configuración
data class ConfiguracionIdiomaUiState(
    val idiomaNativo: Idioma? = null,
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null
) {

    // El idioma de aprendizaje será el contrario
    // al idioma nativo seleccionado.
    val idiomaAprendizaje: Idioma?
        get() = when (idiomaNativo) {
            Idioma.TURCO -> Idioma.ESPANOL
            Idioma.ESPANOL -> Idioma.TURCO
            null -> null
        }
}

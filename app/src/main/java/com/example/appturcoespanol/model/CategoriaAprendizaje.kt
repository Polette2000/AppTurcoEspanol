
package com.example.appturcoespanol.model

enum class CategoriaAprendizaje(
    val nombre: String,
    val nombreTurco: String,
    val emoji: String,
    val descripcion: String
) {
    ANIMALES(
        "Animales",
        "Hayvanlar",
        "🐱",
        "Aprende los nombres de los animales"
    ),

    COLORES(
        "Colores",
        "Renkler",
        "🎨",
        "Descubre los colores en español"
    ),

    FRUTAS(
        "Frutas",
        "Meyveler",
        "🍎",
        "Aprende los nombres de las frutas"
    )
}

// Estado de la pantalla de categorias
data class CategoriasUiState(
    val categoriaSeleccionada: CategoriaAprendizaje? = null
)

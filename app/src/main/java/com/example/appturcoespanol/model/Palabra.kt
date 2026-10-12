
package com.example.appturcoespanol.model

data class Palabra(
    val id: Int,
    val espanol: String,
    val turco: String,
    val emoji: String,
    val categoria: CategoriaAprendizaje
)

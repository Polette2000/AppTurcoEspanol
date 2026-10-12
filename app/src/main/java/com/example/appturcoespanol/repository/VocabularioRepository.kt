
package com.example.appturcoespanol.repository

import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Palabra

object VocabularioRepository {

    private val palabras = listOf(

        // ANIMALES
        Palabra(
            1, "Gato", "Kedi", "🐱",
            CategoriaAprendizaje.ANIMALES
        ),
        Palabra(
            2, "Perro", "Köpek", "🐶",
            CategoriaAprendizaje.ANIMALES
        ),
        Palabra(
            3, "Pájaro", "Kuş", "🐦",
            CategoriaAprendizaje.ANIMALES
        ),

        // COLORES
        Palabra(
            4, "Rojo", "Kırmızı", "🔴",
            CategoriaAprendizaje.COLORES
        ),
        Palabra(
            5, "Azul", "Mavi", "🔵",
            CategoriaAprendizaje.COLORES
        ),
        Palabra(
            6, "Amarillo", "Sarı", "🟡",
            CategoriaAprendizaje.COLORES
        ),

        // FRUTAS
        Palabra(
            7, "Manzana", "Elma", "🍎",
            CategoriaAprendizaje.FRUTAS
        ),
        Palabra(
            8, "Plátano", "Muz", "🍌",
            CategoriaAprendizaje.FRUTAS
        ),
        Palabra(
            9, "Naranja", "Portakal", "🍊",
            CategoriaAprendizaje.FRUTAS
        )
    )

    fun obtenerPalabras(
        categoria: CategoriaAprendizaje
    ): List<Palabra> {

        return palabras.filter { palabra ->
            palabra.categoria == categoria
        }
    }
}

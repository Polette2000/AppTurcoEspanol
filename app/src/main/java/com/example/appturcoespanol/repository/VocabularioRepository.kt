package com.example.appturcoespanol.repository

import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Palabra

/** Vocabulario real español ↔ turco, separado por mundo. */
object VocabularioRepository {

    private val palabras = listOf(
        // 🦊 ZORRO: ANIMALES
        Palabra(1, "Gato", "Kedi", "🐱", CategoriaAprendizaje.ANIMALES),
        Palabra(2, "Perro", "Köpek", "🐶", CategoriaAprendizaje.ANIMALES),
        Palabra(3, "Pájaro", "Kuş", "🐦", CategoriaAprendizaje.ANIMALES),
        Palabra(4, "Pez", "Balık", "🐟", CategoriaAprendizaje.ANIMALES),

        // 🦊 ZORRO: FRUTAS
        Palabra(5, "Manzana", "Elma", "🍎", CategoriaAprendizaje.FRUTAS),
        Palabra(6, "Plátano", "Muz", "🍌", CategoriaAprendizaje.FRUTAS),
        Palabra(7, "Naranja", "Portakal", "🍊", CategoriaAprendizaje.FRUTAS),
        Palabra(8, "Fresa", "Çilek", "🍓", CategoriaAprendizaje.FRUTAS),

        // 🦊 ZORRO: NATURALEZA
        Palabra(9, "Árbol", "Ağaç", "🌳", CategoriaAprendizaje.NATURALEZA),
        Palabra(10, "Flor", "Çiçek", "🌸", CategoriaAprendizaje.NATURALEZA),
        Palabra(11, "Sol", "Güneş", "☀️", CategoriaAprendizaje.NATURALEZA),
        Palabra(12, "Lluvia", "Yağmur", "🌧️", CategoriaAprendizaje.NATURALEZA),

        // 🐼 PANDA: COLORES
        Palabra(13, "Rojo", "Kırmızı", "🔴", CategoriaAprendizaje.COLORES),
        Palabra(14, "Azul", "Mavi", "🔵", CategoriaAprendizaje.COLORES),
        Palabra(15, "Amarillo", "Sarı", "🟡", CategoriaAprendizaje.COLORES),
        Palabra(16, "Verde", "Yeşil", "🟢", CategoriaAprendizaje.COLORES),

        // 🐼 PANDA: FORMAS
        Palabra(17, "Triángulo", "Üçgen", "🔺", CategoriaAprendizaje.FORMAS),
        Palabra(18, "Cuadrado", "Kare", "🟦", CategoriaAprendizaje.FORMAS),
        Palabra(19, "Círculo", "Daire", "⚫", CategoriaAprendizaje.FORMAS),
        Palabra(20, "Estrella", "Yıldız", "⭐", CategoriaAprendizaje.FORMAS),

        // 🐼 PANDA: NÚMEROS
        Palabra(21, "Uno", "Bir", "1️⃣", CategoriaAprendizaje.NUMEROS),
        Palabra(22, "Dos", "İki", "2️⃣", CategoriaAprendizaje.NUMEROS),
        Palabra(23, "Tres", "Üç", "3️⃣", CategoriaAprendizaje.NUMEROS),
        Palabra(24, "Cuatro", "Dört", "4️⃣", CategoriaAprendizaje.NUMEROS),

        // 🐱 GATO: SALUDOS
        Palabra(25, "Hola", "Merhaba", "👋", CategoriaAprendizaje.SALUDOS),
        Palabra(26, "Adiós", "Hoşça kal", "🚶", CategoriaAprendizaje.SALUDOS),
        Palabra(27, "Gracias", "Teşekkürler", "🙏", CategoriaAprendizaje.SALUDOS),
        Palabra(28, "Por favor", "Lütfen", "🤲", CategoriaAprendizaje.SALUDOS),

        // 🐱 GATO: FAMILIA
        Palabra(29, "Madre", "Anne", "👩", CategoriaAprendizaje.FAMILIA),
        Palabra(30, "Padre", "Baba", "👨", CategoriaAprendizaje.FAMILIA),
        Palabra(31, "Hermana", "Kız kardeş", "👧", CategoriaAprendizaje.FAMILIA),
        Palabra(32, "Hermano", "Erkek kardeş", "👦", CategoriaAprendizaje.FAMILIA),

        // 🐱 GATO: EMOCIONES
        Palabra(33, "Feliz", "Mutlu", "😊", CategoriaAprendizaje.EMOCIONES),
        Palabra(34, "Triste", "Üzgün", "😢", CategoriaAprendizaje.EMOCIONES),
        Palabra(35, "Enojado", "Kızgın", "😠", CategoriaAprendizaje.EMOCIONES),
        Palabra(36, "Tranquilo", "Sakin", "😌", CategoriaAprendizaje.EMOCIONES)
    )

    fun obtenerPalabras(categoria: CategoriaAprendizaje): List<Palabra> =
        palabras.filter { it.categoria == categoria }
}

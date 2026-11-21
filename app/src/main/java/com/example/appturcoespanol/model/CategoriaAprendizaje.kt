package com.example.appturcoespanol.model

/** Cada mundo ofrece sus propias categorías y su propio vocabulario. */
enum class CategoriaAprendizaje(
    val nombre: String,
    val nombreTurco: String,
    val emoji: String,
    val descripcion: String,
    val descripcionTurco: String,
    val avatar: Avatar
) {
    // 🦊 MUNDO EXPLORADOR
    ANIMALES(
        "Animales", "Hayvanlar", "🐾",
        "Descubre animales de nuestro entorno.",
        "Çevremizdeki hayvanları keşfet.", Avatar.ZORRO
    ),
    FRUTAS(
        "Frutas", "Meyveler", "🍎",
        "Conoce frutas y sus nombres.",
        "Meyveleri ve adlarını öğren.", Avatar.ZORRO
    ),
    NATURALEZA(
        "Naturaleza", "Doğa", "🌳",
        "Explora árboles, flores y el clima.",
        "Ağaçları, çiçekleri ve havayı keşfet.", Avatar.ZORRO
    ),

    // 🐼 MUNDO DE LOS DESAFÍOS
    COLORES(
        "Colores", "Renkler", "🎨",
        "Reconoce y relaciona colores.",
        "Renkleri tanı ve eşleştir.", Avatar.PANDA
    ),
    FORMAS(
        "Formas", "Şekiller", "🔺",
        "Descubre figuras y formas.",
        "Geometrik şekilleri keşfet.", Avatar.PANDA
    ),
    NUMEROS(
        "Números", "Sayılar", "🔢",
        "Cuenta y reconoce los números.",
        "Sayıları say ve tanı.", Avatar.PANDA
    ),

    // 🐱 MUNDO DE LAS PALABRAS
    SALUDOS(
        "Saludos", "Selamlaşma", "👋",
        "Aprende expresiones para saludar.",
        "Selamlaşma ifadelerini öğren.", Avatar.GATO
    ),
    FAMILIA(
        "Familia", "Aile", "👨‍👩‍👧",
        "Aprende cómo llamar a tus familiares.",
        "Aile bireylerinin adlarını öğren.", Avatar.GATO
    ),
    EMOCIONES(
        "Emociones", "Duygular", "😊",
        "Descubre cómo expresar emociones.",
        "Duygularını nasıl ifade edeceğini öğren.", Avatar.GATO
    );

    companion object {
        /** Muestra exclusivamente las categorías del avatar seleccionado. */
        fun paraAvatar(avatar: Avatar): List<CategoriaAprendizaje> =
            entries.filter { it.avatar == avatar }
    }
}

// Se mantiene el estado actual para no romper CategoriasViewModel.
data class CategoriasUiState(
    val categoriaSeleccionada: CategoriaAprendizaje? = null
)

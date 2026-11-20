package com.example.appturcoespanol.model

/** Perfil local y ficticio. No requiere nombre real ni datos personales. */
data class PerfilInfantil(
    val id: String,
    val apodo: String,
    val idiomaNativo: Idioma,
    val avatar: Avatar
)

data class PerfilesUiState(
    val cargando: Boolean = true,
    val perfiles: List<PerfilInfantil> = emptyList(),
    val guardando: Boolean = false,
    val perfilCreadoId: String? = null,
    val error: String? = null
)

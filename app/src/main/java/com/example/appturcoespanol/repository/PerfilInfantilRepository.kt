package com.example.appturcoespanol.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.PerfilInfantil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID

private val Context.perfilesEsTurkDataStore by preferencesDataStore(
    name = "esturk_perfiles_infantiles"
)

/**
 * Perfiles ficticios separados por cuenta adulta. Solo datos locales:
 * NO existe sincronizacion con otros telefonos.
 */
class PerfilInfantilRepository(private val context: Context) {

    fun observarPerfiles(cuentaAdulta: String): Flow<List<PerfilInfantil>> {
        val clave = claveCuenta(cuentaAdulta)
        return context.perfilesEsTurkDataStore.data.map { preferencias ->
            leerPerfiles(preferencias[clave].orEmpty())
        }
    }

    suspend fun crearPerfil(
        cuentaAdulta: String,
        apodo: String,
        idiomaNativo: Idioma,
        avatar: Avatar
    ): PerfilInfantil {
        require(cuentaAdulta.isNotBlank()) { "Debes iniciar sesion como adulto." }
        val nombre = apodo.trim()
        require(nombre.length in 2..16) { "El apodo debe tener entre 2 y 16 caracteres." }
        require(nombre.all { it.isLetterOrDigit() || it == ' ' || it == '-' || it == '_' }) {
            "Usa letras, numeros, espacios, guion o guion bajo."
        }

        val nuevo = PerfilInfantil(
            id = UUID.randomUUID().toString(),
            apodo = nombre,
            idiomaNativo = idiomaNativo,
            avatar = avatar
        )
        val clave = claveCuenta(cuentaAdulta)
        context.perfilesEsTurkDataStore.edit { preferencias ->
            val existentes = leerPerfiles(preferencias[clave].orEmpty())
            require(existentes.size < 5) { "Se permiten hasta 5 perfiles por cuenta." }
            require(existentes.none { it.apodo.equals(nombre, ignoreCase = true) }) {
                "Ya existe un perfil con ese apodo."
            }
            preferencias[clave] = guardarPerfiles(existentes + nuevo)
        }
        return nuevo
    }

    private fun claveCuenta(cuenta: String) = stringPreferencesKey(
        "perfiles_${hash(cuenta.trim().lowercase(Locale.ROOT))}"
    )

    private fun hash(texto: String): String = MessageDigest.getInstance("SHA-256")
        .digest(texto.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun guardarPerfiles(perfiles: List<PerfilInfantil>): String {
        val lista = JSONArray()
        perfiles.forEach { perfil ->
            lista.put(JSONObject().apply {
                put("id", perfil.id)
                put("apodo", perfil.apodo)
                put("idioma", perfil.idiomaNativo.name)
                put("avatar", perfil.avatar.name)
            })
        }
        return lista.toString()
    }

    private fun leerPerfiles(texto: String): List<PerfilInfantil> {
        val lista = runCatching { JSONArray(texto) }.getOrElse { return emptyList() }
        return (0 until lista.length()).mapNotNull { indice ->
            val dato = lista.optJSONObject(indice) ?: return@mapNotNull null
            val id = dato.optString("id")
            val apodo = dato.optString("apodo")
            val idioma = runCatching { Idioma.valueOf(dato.optString("idioma")) }.getOrNull()
            val avatar = runCatching { Avatar.valueOf(dato.optString("avatar")) }.getOrNull()
            if (id.isBlank() || apodo.isBlank() || idioma == null || avatar == null) null
            else PerfilInfantil(id, apodo, idioma, avatar)
        }
    }
}

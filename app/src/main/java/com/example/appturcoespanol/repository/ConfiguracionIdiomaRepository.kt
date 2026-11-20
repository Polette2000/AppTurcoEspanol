
package com.example.appturcoespanol.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appturcoespanol.model.Idioma
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.Locale

// DataStore para las preferencias de idiomas
private val Context.idiomasDataStore by preferencesDataStore(
    name = "esturk_configuracion_idiomas"
)

class ConfiguracionIdiomaRepository(context: Context) {

    private val appContext = context.applicationContext

    // Genera una clave diferente para cada cuenta adulta
    private fun claveIdioma(cuentaAdulta: String) =
        stringPreferencesKey(
            "idioma_nativo_${identificador(cuentaAdulta)}"
        )

    // Recupera el idioma guardado
    fun observarIdiomaNativo(
        cuentaAdulta: String
    ): Flow<Idioma?> {

        val clave = claveIdioma(cuentaAdulta)

        return appContext.idiomasDataStore.data.map { preferencias ->

            preferencias[clave]?.let { valor ->
                Idioma.entries.firstOrNull {
                    it.name == valor
                }
            }
        }
    }

    // Guarda el idioma elegido por el adulto
    suspend fun guardarIdiomaNativo(
        cuentaAdulta: String,
        idioma: Idioma
    ) {

        require(cuentaAdulta.isNotBlank()) {
            "Debe existir una cuenta adulta activa"
        }

        appContext.idiomasDataStore.edit { preferencias ->
            preferencias[claveIdioma(cuentaAdulta)] = idioma.name
        }
    }

    // Genera un identificador estable a partir del alias ficticio.
    // Evita colocar el alias directamente en la clave de DataStore.
    private fun identificador(alias: String): String {

        val normalizado = alias.trim().lowercase(Locale.ROOT)

        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(normalizado.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { byte ->
            (byte.toInt() and 0xFF)
                .toString(16)
                .padStart(2, '0')
        }
    }
}

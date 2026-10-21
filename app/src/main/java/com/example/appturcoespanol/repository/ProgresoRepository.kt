
package com.example.appturcoespanol.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.ProgresoAprendizaje
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Una sola instancia de DataStore
private val Context.progresoDataStore by preferencesDataStore(
    name = "progreso_turcoespanol"
)

class ProgresoRepository(
    private val context: Context
) {

    // Clave de leccion completada
    private fun claveLeccion(
        avatar: Avatar,
        categoria: CategoriaAprendizaje
    ) = booleanPreferencesKey(
        "leccion_${avatar.name}_${categoria.name}"
    )

    // Clave de la mejor puntuacion
    private fun clavePuntaje(
        avatar: Avatar,
        categoria: CategoriaAprendizaje
    ) = intPreferencesKey(
        "puntaje_${avatar.name}_${categoria.name}"
    )

    // Recuperar el progreso almacenado
    fun observarProgreso(
        avatar: Avatar,
        categoria: CategoriaAprendizaje
    ): Flow<ProgresoAprendizaje> {

        return context.progresoDataStore.data.map { preferencias ->

            ProgresoAprendizaje(
                leccionCompletada =
                    preferencias[claveLeccion(avatar, categoria)]
                        ?: false,

                mejorPuntaje =
                    preferencias[clavePuntaje(avatar, categoria)]
                        ?: 0
            )
        }
    }

    // Guardar leccion completada
    suspend fun guardarLeccionCompletada(
        avatar: Avatar,
        categoria: CategoriaAprendizaje
    ) {

        context.progresoDataStore.edit { preferencias ->

            preferencias[claveLeccion(avatar, categoria)] = true
        }
    }

    // Conservar la mejor puntuacion
    suspend fun guardarMejorPuntaje(
        avatar: Avatar,
        categoria: CategoriaAprendizaje,
        puntaje: Int
    ) {

        require(puntaje >= 0) {
            "El puntaje no puede ser negativo"
        }

        context.progresoDataStore.edit { preferencias ->

            val clave = clavePuntaje(avatar, categoria)

            val puntajeAnterior = preferencias[clave] ?: 0

            preferencias[clave] = maxOf(
                puntajeAnterior,
                puntaje
            )
        }
    }

    // NUEVO: Eliminar el progreso de un solo avatar
    suspend fun reiniciarProgreso(
        avatar: Avatar
    ) {

        context.progresoDataStore.edit { preferencias ->

            CategoriaAprendizaje.entries.forEach { categoria ->

                // Eliminar leccion completada
                preferencias.remove(
                    claveLeccion(avatar, categoria)
                )

                // Eliminar mejor puntuacion
                preferencias.remove(
                    clavePuntaje(avatar, categoria)
                )
            }
        }
    }
}


package com.example.appturcoespanol.repository

import com.example.appturcoespanol.network.ApiCliente

class TraduccionRepository {

    suspend fun consultar(palabra: String): String {

        val respuesta = ApiCliente.servicio
            .obtenerTraduccion(palabra)

        return respuesta.traduccion
    }
}

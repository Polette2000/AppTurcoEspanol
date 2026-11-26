
package com.example.appturcoespanol.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TraduccionRespuesta(
    val palabra: String,
    val traduccion: String
)

interface TraduccionApi {

    @GET("api/traduccion")
    suspend fun obtenerTraduccion(
        @Query("palabra") palabra: String
    ): TraduccionRespuesta
}

object ApiCliente {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val servicio: TraduccionApi =
        retrofit.create(TraduccionApi::class.java)
}

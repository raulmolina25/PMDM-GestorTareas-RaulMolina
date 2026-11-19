// Paquete donde se encuentran los elementos de comunicación con la API.
package com.example.gestorpersonaldetareas.comunicaciones

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto único (singleton) que reutiliza una sola configuración de Retrofit.
object ClienteRetrofit {

    // URL base de la API. Debe terminar en "/".
    private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

    // Servicio listo para usar. by lazy: se crea la primera vez que se usa.
    // Gson (GsonConverterFactory) convierte el JSON en objetos Kotlin.
    val api: ApiTareas by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiTareas::class.java)
    }
}
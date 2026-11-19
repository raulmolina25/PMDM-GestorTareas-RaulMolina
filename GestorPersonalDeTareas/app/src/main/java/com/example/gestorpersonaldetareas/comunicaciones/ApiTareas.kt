// Paquete donde se encuentran los elementos de comunicación con la API.
package com.example.gestorpersonaldetareas.comunicaciones

import retrofit2.http.GET
import retrofit2.http.Query

// Interfaz que describe las peticiones HTTP a JSONPlaceholder.
// Retrofit genera automáticamente la implementación.
interface ApiTareas {

    // Petición GET a "todos?userId=..." (se añade a la URL base).
    // Es suspend: se llama desde una coroutine sin bloquear el hilo principal.
    // Gson convierte el JSON recibido en una lista de TareaEjemplo.
    @GET("todos")
    suspend fun obtenerTareas(@Query("userId") userId: Int): List<TareaEjemplo>
}
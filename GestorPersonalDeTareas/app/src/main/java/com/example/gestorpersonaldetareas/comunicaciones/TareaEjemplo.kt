// Paquete donde se encuentran los elementos de comunicación con la API.
package com.example.gestorpersonaldetareas.comunicaciones

// Tarea de ejemplo recibida de JSONPlaceholder.
// Los nombres de las propiedades coinciden con los campos del JSON,
// así Gson los rellena automáticamente. No es una entidad de Room:
// estos datos no se guardan en la base de datos.
data class TareaEjemplo(

    // Identificador del usuario en la API.
    val userId: Int,

    // Identificador de la tarea de ejemplo.
    val id: Int,

    // Título de la tarea.
    val title: String,

    // true = completada, false = pendiente.
    val completed: Boolean
)
// Paquete donde se encuentra el modelo de datos.
package com.example.gestorpersonaldetareas.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad de Room: cada objeto Tarea es una fila de la tabla "tareas".
// Al ser data class, Kotlin genera equals(), hashCode(), toString() y copy().
@Entity(tableName = "tareas")
data class Tarea(

    // Clave primaria. Con autoGenerate = true, Room asigna el id al insertar.
    // El valor 0 indica que la tarea es nueva y todavía no tiene id real.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Nombre de la tarea.
    val titulo: String,

    // Texto que explica qué hay que hacer. Puede estar vacío.
    val descripcion: String,

    // "Baja", "Media" o "Alta".
    val prioridad: String,

    // false = pendiente, true = completada.
    val completada: Boolean = false
)
// Paquete donde guardamos los archivos relacionados con Room.
package com.example.gestorpersonaldetareas.almacenamiento

// Permite indicar que una clase representa una base de datos de Room.
import androidx.room.Database

// Clase de la que debemos heredar para crear nuestra base de datos.
import androidx.room.RoomDatabase

// Importamos nuestra entidad Tarea, que representa la tabla "tareas".
import com.example.gestorpersonaldetareas.modelo.Tarea

// Indicamos a Room cómo estará formada nuestra base de datos.
@Database(

    // Especificamos las entidades que tendrá la base de datos.
    // En nuestro caso, solamente utilizamos la entidad Tarea.
    entities = [Tarea::class],

    // Primera versión de nuestra base de datos.
    version = 1,

    // No necesitamos exportar el esquema para esta práctica.
    exportSchema = false
)

// Declaramos una clase abstracta que hereda de RoomDatabase.
// Room generará automáticamente su implementación.
abstract class BaseDatosTareas : RoomDatabase() {

    // Método que permite acceder a las operaciones de TareaDao.
    // Room generará automáticamente su implementación.
    abstract fun tareaDao(): TareaDao
}
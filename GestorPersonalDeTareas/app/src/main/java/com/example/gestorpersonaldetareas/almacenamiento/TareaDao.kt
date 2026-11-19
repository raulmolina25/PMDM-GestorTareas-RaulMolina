// Paquete donde se encuentran los elementos de Room.
package com.example.gestorpersonaldetareas.almacenamiento

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestorpersonaldetareas.modelo.Tarea
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object): define las operaciones sobre la tabla "tareas".
// Room genera automáticamente la implementación de esta interfaz.
@Dao
interface TareaDao {

    // Devuelve todas las tareas, las más recientes primero.
    // Al devolver un Flow, emite una lista nueva cada vez que la tabla cambia.
    // No necesita suspend porque un Flow ya es asíncrono.
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    // Inserta una tarea nueva. Con id = 0, Room genera el siguiente id libre.
    @Insert
    suspend fun insertar(tarea: Tarea)

    // Actualiza la fila cuyo id coincide con el de la tarea recibida.
    // Devuelve el número de filas modificadas: 0 si el id no existe.
    @Update
    suspend fun actualizar(tarea: Tarea): Int

    // Elimina la fila cuyo id coincide con el de la tarea recibida.
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
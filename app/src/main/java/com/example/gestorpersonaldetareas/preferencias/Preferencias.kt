// Paquete donde se encuentra el acceso a las preferencias del usuario.
package com.example.gestorpersonaldetareas.preferencias

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Delegado que crea el DataStore de preferencias.
// Debe declararse una sola vez, a nivel de archivo, para que exista
// una única instancia asociada al archivo "preferencias".
// Al ser una propiedad de extensión de Context, se accede con contexto.almacenPreferencias.
private val Context.almacenPreferencias: DataStore<Preferences> by preferencesDataStore(
    name = "preferencias"
)

// Lee y escribe la preferencia "Mostrar solo pendientes".
// No conoce la interfaz: solo expone un Flow para leer y una función para guardar.
class Preferencias(private val contexto: Context) {

    // Clave con la que se guarda el valor dentro del archivo de preferencias.
    private val claveSoloPendientes = booleanPreferencesKey("solo_pendientes")

    // Flow que emite el valor guardado cada vez que cambia.
    // Si todavía no hay valor guardado, emite false (mostrar todas las tareas).
    val soloPendientes: Flow<Boolean> = contexto.almacenPreferencias.data
        .catch { error ->
            // Si falla la lectura por un problema de entrada/salida,
            // emitimos preferencias vacías para usar el valor predeterminado.
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                // Cualquier otro error no se oculta: se vuelve a lanzar.
                throw error
            }
        }
        .map { preferencias ->
            // Si la clave no existe todavía, usamos false.
            preferencias[claveSoloPendientes] ?: false
        }

    // Guarda el nuevo valor de la preferencia.
    // Es suspend porque edit es asíncrono: hay que llamarla desde una coroutine.
    suspend fun guardarSoloPendientes(valor: Boolean) {
        contexto.almacenPreferencias.edit { preferencias ->
            preferencias[claveSoloPendientes] = valor
        }
    }
}
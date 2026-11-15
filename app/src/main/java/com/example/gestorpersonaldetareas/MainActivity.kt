package com.example.gestorpersonaldetareas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.gestorpersonaldetareas.almacenamiento.BaseDatosTareas
import com.example.gestorpersonaldetareas.comunicaciones.ClienteRetrofit
import com.example.gestorpersonaldetareas.comunicaciones.TareaEjemplo
import com.example.gestorpersonaldetareas.interfaz.Pantalla
import com.example.gestorpersonaldetareas.interfaz.PantallaEjemplos
import com.example.gestorpersonaldetareas.interfaz.PantallaFormulario
import com.example.gestorpersonaldetareas.interfaz.PantallaPrincipal
import com.example.gestorpersonaldetareas.modelo.Tarea
import com.example.gestorpersonaldetareas.preferencias.Preferencias
import com.example.gestorpersonaldetareas.ui.theme.GestorPersonalDeTareasTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Activity principal de la aplicación.
class MainActivity : ComponentActivity() {

    // Instancia de la base de datos Room.
    // by lazy: se crea la primera vez que se usa y se reutiliza después.
    // "tareas_db" es el nombre del archivo SQLite en el dispositivo.
    private val baseDatos: BaseDatosTareas by lazy {
        Room.databaseBuilder(
            applicationContext,
            BaseDatosTareas::class.java,
            "tareas_db"
        ).build()
    }

    // Acceso a las preferencias del usuario guardadas con DataStore.
    // Se usa applicationContext para no retener una referencia a la Activity.
    // El DataStore en sí es único por archivo (lo garantiza el delegado
    // declarado en Preferencias.kt), aunque se cree esta clase varias veces.
    private val preferencias: Preferencias by lazy {
        Preferencias(applicationContext)
    }

    // Método que se ejecuta al iniciar la aplicación.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que la aplicación dibuje contenido detrás de las barras del sistema.
        enableEdgeToEdge()

        // Establecemos el contenido visual de la aplicación con Jetpack Compose.
        setContent {
            // Aplicamos el tema visual de nuestra aplicación.
            GestorPersonalDeTareasTheme {
                // Llamamos a la función que controla la navegación entre pantallas.
                // Le pasamos la base de datos (tareas) y las preferencias (filtro).
                AplicacionGestorTareas(
                    baseDatos = baseDatos,
                    preferencias = preferencias
                )
            }
        }
    }
}

// Punto único de guardado.
// Si la tarea es nueva (id == 0) la inserta; si ya existe, la actualiza.
// Devuelve true solo si la operación ha terminado correctamente.
// Al editar, también exige que se haya modificado al menos una fila:
// si la tarea ya no existe, actualizar() devuelve 0 y no se da por guardada.
private suspend fun guardarEnAlmacenamiento(
    tarea: Tarea,
    baseDatos: BaseDatosTareas
): Boolean {
    return try {
        if (tarea.id == 0) {
            baseDatos.tareaDao().insertar(tarea)
            true
        } else {
            baseDatos.tareaDao().actualizar(tarea) > 0
        }
    } catch (cancelacion: CancellationException) {
        // Una cancelación de la coroutine no es un error de almacenamiento:
        // se vuelve a lanzar para que la coroutine termine con normalidad.
        throw cancelacion
    } catch (error: Exception) {
        // Cualquier otro fallo se considera un guardado no completado.
        false
    }
}

// Función principal de Compose que controla qué pantalla se muestra.
@Composable
fun AplicacionGestorTareas(
    baseDatos: BaseDatosTareas,
    preferencias: Preferencias
) {

    // Contexto de Android. Lo necesitamos para abrir el selector de compartir.
    // Se obtiene directamente desde Compose, sin pasarlo desde MainActivity.
    val contexto = LocalContext.current

    // Guardamos cuál es la pantalla que se está mostrando actualmente.
    // Al iniciar la aplicación, comenzamos en la pantalla PRINCIPAL.
    // rememberSaveable permite conservar el estado ante cambios de configuración.
    var pantallaActual by rememberSaveable {
        mutableStateOf(Pantalla.PRINCIPAL)
    }

    // Identificador de la tarea que se está editando.
    // null significa que el formulario está creando una tarea nueva.
    var idTareaEnEdicion by rememberSaveable { mutableStateOf<Int?>(null) }

    // Indica si hay un guardado en curso. Mientras sea true, el formulario
    // deshabilita sus botones y campos, evitando registros duplicados.
    var guardando by remember { mutableStateOf(false) }

    // Mensaje de error si el guardado falla.
    var errorGuardado by remember { mutableStateOf<String?>(null) }

    // Tarea que el usuario quiere eliminar. Mientras no sea null se muestra
    // el diálogo de confirmación. null significa que no hay ninguna pendiente.
    var tareaPendienteEliminar by remember { mutableStateOf<Tarea?>(null) }

    // Tareas de ejemplo recibidas de la API. Son un conjunto distinto
    // de las tareas personales y no se guardan en Room.
    var ejemplos by remember { mutableStateOf<List<TareaEjemplo>>(emptyList()) }

    // true mientras la petición a la API está en curso.
    var cargandoEjemplos by remember { mutableStateOf(false) }

    // Mensaje de error de la consulta a la API. null si no hay error.
    var errorEjemplos by remember { mutableStateOf<String?>(null) }

    // Estado del Snackbar, que muestra mensajes de error en la pantalla principal.
    val snackbarHostState = remember { SnackbarHostState() }

    // Ámbito de coroutines para ejecutar las operaciones sin bloquear el hilo principal.
    val ambito = rememberCoroutineScope()

    // Muestra un mensaje de error en el Snackbar.
    // Se lanza en su propia coroutine porque showSnackbar espera hasta que
    // el mensaje desaparece y no debe bloquear la operación que lo origina.
    val mostrarError: (String) -> Unit = { mensaje ->
        ambito.launch {
            snackbarHostState.showSnackbar(mensaje)
        }
    }

    // Comparte una tarea con otra aplicación mediante un Intent implícito.
    // Solo abre el selector de aplicaciones: no sabemos si el usuario llega
    // a enviar algo, así que no se muestra ningún mensaje de "compartido".
    val compartirTarea: (Tarea) -> Unit = { tarea ->

        // Estado de la tarea en texto legible.
        val estado = if (tarea.completada) "Completada" else "Pendiente"

        // Construimos el texto línea a línea. La descripción solo se incluye
        // si no está vacía, para que el mensaje quede más limpio.
        val texto = buildString {
            appendLine("Título: ${tarea.titulo}")
            if (tarea.descripcion.isNotBlank()) {
                appendLine("Descripción: ${tarea.descripcion}")
            }
            appendLine("Prioridad: ${tarea.prioridad}")
            append("Estado: $estado")
        }

        // Intent implícito: pide a Android que algún componente comparta texto.
        val intentCompartir = Intent(Intent.ACTION_SEND).apply {
            // Texto que recibirá la aplicación elegida.
            putExtra(Intent.EXTRA_TEXT, texto)
            // Tipo de contenido: texto plano.
            type = "text/plain"
        }

        try {
            // El selector deja que el usuario decida con qué aplicación compartir.
            contexto.startActivity(Intent.createChooser(intentCompartir, "Compartir tarea"))
        } catch (error: ActivityNotFoundException) {
            // No hay ninguna aplicación capaz de gestionar el Intent.
            mostrarError("No se ha podido abrir el menú para compartir.")
        }
    }

    // Consulta las tareas de ejemplo a la API con Retrofit.
    val cargarEjemplos: () -> Unit = {
        // Si ya hay una petición en curso, ignoramos la nueva.
        if (!cargandoEjemplos) {
            cargandoEjemplos = true
            errorEjemplos = null
            ambito.launch {
                try {
                    // Petición GET /todos?userId=1 (suspend: no bloquea el hilo principal).
                    ejemplos = ClienteRetrofit.api.obtenerTareas(userId = 1)
                } catch (cancelacion: CancellationException) {
                    // La cancelación no es un error de red: se vuelve a lanzar.
                    throw cancelacion
                } catch (error: IOException) {
                    // Sin conexión, tiempo agotado o servidor inalcanzable.
                    errorEjemplos = "No se ha podido conectar. " +
                            "Comprueba tu conexión a Internet e inténtalo de nuevo."
                } catch (error: HttpException) {
                    // El servidor respondió con un código de error (404, 500...).
                    errorEjemplos = "El servidor ha respondido con un error " +
                            "(código ${error.code()}). Inténtalo de nuevo."
                } catch (error: Exception) {
                    // Cualquier otro fallo, por ejemplo una respuesta que no se puede leer.
                    errorEjemplos = "No se han podido cargar las tareas de ejemplo."
                } finally {
                    // Se ejecute lo que se ejecute, la petición ha terminado.
                    cargandoEjemplos = false
                }
            }
        }
    }

    // Valor del filtro "Mostrar solo pendientes" leído desde DataStore.
    // El valor inicial es null mientras DataStore todavía no ha emitido.
    // Esto sustituye al antiguo rememberSaveable: ahora la fuente de verdad
    // es el archivo de preferencias y el valor sobrevive al cierre de la app.
    val soloPendientesGuardado by preferencias.soloPendientes
        .collectAsState<Boolean, Boolean?>(initial = null)

    // Mientras DataStore no responde, se usa el valor predeterminado (false).
    val soloPendientes = soloPendientesGuardado ?: false

    // Función que se ejecuta al pulsar el Switch.
    // No cambiamos el valor a mano: lo guardamos en DataStore y la interfaz
    // se actualiza cuando el Flow emite el nuevo valor. Así, si la escritura
    // falla, el interruptor no aparenta un cambio que no se ha guardado.
    val cambiarSoloPendientes: (Boolean) -> Unit = { nuevoValor ->
        ambito.launch {
            try {
                preferencias.guardarSoloPendientes(nuevoValor)
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                mostrarError("No se ha podido guardar la preferencia")
            }
        }
    }

    // Flow de tareas que emite Room. remember(baseDatos) conserva la referencia
    // para no volver a crear el Flow en cada recomposición.
    val flujoTareas = remember(baseDatos) { baseDatos.tareaDao().obtenerTodas() }

    // Lista leída desde Room. El valor inicial es null (y no una lista vacía)
    // para distinguir "todavía cargando" de "cargada y realmente vacía".
    val tareasCargadas by flujoTareas.collectAsState<List<Tarea>, List<Tarea>?>(initial = null)

    // true cuando Room ya ha emitido la primera lista.
    val listaCargada = tareasCargadas != null

    // Lista completa de tareas. Mientras carga se trata como vacía.
    val tareas = tareasCargadas ?: emptyList()

    // Tareas que se enseñan en la pantalla principal, ya filtradas.
    val tareasVisibles = if (soloPendientes) {
        tareas.filter { !it.completada }
    } else {
        tareas
    }

    // Tarea que debe mostrar el formulario: la que tiene el id en edición,
    // o null si estamos creando una nueva o la lista aún no la contiene.
    val tareaEnEdicion = tareas.firstOrNull { it.id == idTareaEnEdicion }

    // Función que permite cambiar a otra pantalla.
    // Recibe como parámetro el destino al que queremos navegar.
    val navegarA: (Pantalla) -> Unit = { destino ->
        pantallaActual = destino
    }

    // Función que vuelve a la pantalla principal y limpia el estado del formulario.
    val cerrarFormulario: () -> Unit = {
        idTareaEnEdicion = null
        errorGuardado = null
        navegarA(Pantalla.PRINCIPAL)
    }

    // Función que guarda una tarea.
    // La tarea no se considera guardada hasta que la operación confirma que ha
    // terminado bien. Si falla, nos quedamos en el formulario y mostramos el error.
    val guardarTarea: (Tarea) -> Unit = { tarea ->
        // Si ya hay un guardado en curso, ignoramos la pulsación repetida.
        if (!guardando) {
            guardando = true
            errorGuardado = null
            ambito.launch {
                val guardada = guardarEnAlmacenamiento(tarea, baseDatos)
                guardando = false
                if (guardada) {
                    cerrarFormulario()
                } else {
                    errorGuardado = "No se ha podido guardar la tarea. Inténtalo de nuevo."
                }
            }
        }
    }

    // Controlamos el botón físico o del sistema para volver atrás.
    // Solo interceptamos la acción cuando no estamos en la pantalla principal.
    BackHandler(enabled = pantallaActual != Pantalla.PRINCIPAL) {
        // Mientras se guarda, no se permite salir del formulario.
        if (!guardando) {
            cerrarFormulario()
        }
    }

    // Comprobamos qué pantalla está seleccionada y mostramos la correspondiente.
    when (pantallaActual) {

        // Si estamos en la pantalla principal...
        Pantalla.PRINCIPAL -> PantallaPrincipal(
            tareas = tareasVisibles,
            soloPendientes = soloPendientes,

            // Al pulsar el Switch, guardamos el nuevo valor en DataStore.
            onCambiarSoloPendientes = cambiarSoloPendientes,

            // Crear: sin tarea en edición, el formulario aparece vacío.
            onCrearTarea = {
                idTareaEnEdicion = null
                errorGuardado = null
                navegarA(Pantalla.FORMULARIO)
            },

            // Ejemplos: navegamos a la pantalla y empezamos la consulta a la API.
            onVerEjemplos = {
                navegarA(Pantalla.EJEMPLOS)
                cargarEjemplos()
            },

            // Editar: guardamos el id de la tarea y abrimos el formulario.
            onEditarTarea = { tarea ->
                idTareaEnEdicion = tarea.id
                errorGuardado = null
                navegarA(Pantalla.FORMULARIO)
            },

            // Completar: creamos una copia de la tarea con el nuevo estado
            // y la actualizamos en Room. La lista se refresca sola porque
            // Room vuelve a emitir las tareas tras el cambio.
            // Si falla, o no se modifica ninguna fila, se avisa con un Snackbar.
            onCambiarCompletada = { tarea, completada ->
                ambito.launch {
                    try {
                        val filas = baseDatos.tareaDao()
                            .actualizar(tarea.copy(completada = completada))
                        if (filas == 0) {
                            mostrarError("No se ha podido actualizar la tarea")
                        }
                    } catch (cancelacion: CancellationException) {
                        throw cancelacion
                    } catch (error: Exception) {
                        mostrarError("No se ha podido actualizar la tarea")
                    }
                }
            },

            // Eliminar: no se borra nada todavía. Solo guardamos la tarea
            // para que se muestre el diálogo de confirmación.
            onEliminarTarea = { tarea -> tareaPendienteEliminar = tarea },

            // Compartir: abre el selector de aplicaciones con los datos de la tarea.
            onCompartirTarea = compartirTarea,
            snackbarHostState = snackbarHostState
        )

        // Si estamos en el formulario...
        Pantalla.FORMULARIO -> {
            if (idTareaEnEdicion != null && tareaEnEdicion == null) {
                // Estamos editando, pero la tarea no está en la lista.
                if (!listaCargada) {
                    // Room todavía no ha emitido: mostramos un indicador de carga
                    // para no enseñar un formulario vacío que parezca una tarea nueva.
                    PantallaCarga()
                } else {
                    // La lista ya cargó y la tarea no existe: avisamos y se puede volver.
                    PantallaProvisional(
                        titulo = "La tarea ya no existe",
                        botones = listOf("Volver" to cerrarFormulario)
                    )
                }
            } else {
                // Crear (tareaEnEdicion == null e id null) o editar (tarea encontrada).
                PantallaFormulario(
                    tareaEditar = tareaEnEdicion,
                    guardando = guardando,
                    errorGuardado = errorGuardado,
                    onGuardar = guardarTarea,
                    onCancelar = cerrarFormulario
                )
            }
        }

        // Si estamos en la pantalla de ejemplos, mostramos los datos de la API.
        Pantalla.EJEMPLOS -> PantallaEjemplos(
            ejemplos = ejemplos,
            cargando = cargandoEjemplos,
            error = errorEjemplos,
            onConsultar = cargarEjemplos,
            onVolver = { navegarA(Pantalla.PRINCIPAL) }
        )
    }

    // Diálogo de confirmación de eliminación.
    // Solo existe mientras haya una tarea pendiente de eliminar.
    val tareaAEliminar = tareaPendienteEliminar
    if (tareaAEliminar != null) {
        AlertDialog(
            // Tocar fuera del diálogo o pulsar atrás lo cierra sin eliminar.
            onDismissRequest = { tareaPendienteEliminar = null },
            title = { Text("¿Eliminar tarea?") },
            text = {
                Text("¿Seguro que quieres eliminar esta tarea? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ambito.launch {
                            try {
                                baseDatos.tareaDao().eliminar(tareaAEliminar)
                                // Solo se cierra el diálogo si Room ha terminado bien.
                                tareaPendienteEliminar = null
                            } catch (cancelacion: CancellationException) {
                                throw cancelacion
                            } catch (error: Exception) {
                                // No se anuncia que se haya eliminado: se informa del fallo.
                                tareaPendienteEliminar = null
                                mostrarError("No se ha podido eliminar la tarea")
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaPendienteEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Pantalla con un indicador de carga centrado.
// Se usa mientras Room todavía no ha entregado la primera lista de tareas.
@Composable
private fun PantallaCarga() {
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

// Pantalla sencilla con un título y botones.
// Se usa para el aviso "La tarea ya no existe".
@Composable
private fun PantallaProvisional(
    // Texto que aparecerá como título de la pantalla.
    titulo: String,

    // Lista de botones. Cada elemento contiene el texto y la acción que realizará.
    botones: List<Pair<String, () -> Unit>>
) {

    // Scaffold proporciona la estructura básica de una pantalla Material 3.
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->

        // Colocamos los elementos de la pantalla en una columna.
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Respetamos el espacio reservado por el Scaffold.
                .padding(padding)
                // Añadimos un margen interior de 16 dp.
                .padding(16.dp),

            // Separamos los elementos 16 dp y los centramos verticalmente.
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                Alignment.CenterVertically
            ),

            // Centramos horizontalmente los elementos.
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Mostramos el título de la pantalla.
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineSmall
            )

            // Recorremos la lista y creamos un botón por cada elemento.
            botones.forEach { (texto, accion) ->

                // Al pulsar el botón, ejecutamos la acción que tiene asociada.
                Button(onClick = accion) {
                    Text(texto)
                }
            }
        }
    }
}
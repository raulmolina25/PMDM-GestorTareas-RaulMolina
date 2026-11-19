// Paquete donde se encuentran las pantallas y componentes visuales.
package com.example.gestorpersonaldetareas.interfaz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.comunicaciones.TareaEjemplo

// Pantalla de tareas de ejemplo obtenidas de Internet.
// No conoce Retrofit: recibe el estado y avisa de las acciones del usuario.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEjemplos(
    // Tareas recibidas de la API.
    ejemplos: List<TareaEjemplo>,

    // true mientras la petición está en curso.
    cargando: Boolean,

    // Mensaje de error si la petición ha fallado. null si no hay error.
    error: String?,

    // Lanza de nuevo la consulta a la API.
    onConsultar: () -> Unit,

    // Vuelve a la pantalla principal.
    onVolver: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),

        // El título deja claro que son datos de Internet, no tareas personales.
        topBar = {
            TopAppBar(title = { Text("Tareas de ejemplo (Internet)") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Zona central: ocupa el espacio sobrante y cambia según el estado.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {

                    // Estado: operación en curso.
                    cargando -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Cargando tareas de ejemplo...")
                        }
                    }

                    // Estado: error. Se explica y se ofrece reintentar.
                    error != null -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(onClick = onConsultar) {
                                Text("Reintentar")
                            }
                        }
                    }

                    // Estado: consulta terminada pero sin datos
                    // (por ejemplo, tras girar el dispositivo).
                    ejemplos.isEmpty() -> {
                        Text(
                            text = "No hay tareas de ejemplo cargadas.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Estado: datos disponibles. Se muestran en una lista desplazable.
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Aviso para distinguir estos datos de las tareas personales.
                            item {
                                Text(
                                    text = "Datos de ejemplo de JSONPlaceholder. " +
                                            "No son tus tareas personales.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // key = id: Compose identifica cada fila por su id.
                            items(items = ejemplos, key = { it.id }) { ejemplo ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "#${ejemplo.id}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = ejemplo.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = if (ejemplo.completed) "Completada" else "Pendiente",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Barra inferior con los botones de volver y consultar.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVolver) {
                    Text("Volver")
                }

                // Deshabilitado mientras hay una petición en curso,
                // para evitar peticiones simultáneas.
                Button(
                    onClick = onConsultar,
                    enabled = !cargando
                ) {
                    Text("Consultar de nuevo")
                }
            }
        }
    }
}
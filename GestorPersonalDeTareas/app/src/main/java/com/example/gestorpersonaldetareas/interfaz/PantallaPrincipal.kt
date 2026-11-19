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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.modelo.Tarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    tareas: List<Tarea>,
    soloPendientes: Boolean,
    onCambiarSoloPendientes: (Boolean) -> Unit,
    onCrearTarea: () -> Unit,
    onVerEjemplos: () -> Unit,
    onEditarTarea: (Tarea) -> Unit,
    onCambiarCompletada: (Tarea, Boolean) -> Unit,
    onEliminarTarea: (Tarea) -> Unit,
    onCompartirTarea: (Tarea) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                actions = {
                    TextButton(onClick = onVerEjemplos) {
                        Text("Tareas de ejemplo")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearTarea) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear tarea"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Mostrar solo pendientes")
                Switch(
                    checked = soloPendientes,
                    onCheckedChange = onCambiarSoloPendientes
                )
            }

            if (tareas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (soloPendientes) {
                            "No hay tareas pendientes"
                        } else {
                            "No hay tareas para mostrar"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = tareas, key = { it.id }) { tarea ->
                        ElementoTarea(
                            tarea = tarea,
                            onEditar = { onEditarTarea(tarea) },
                            onCambiarCompletada = { completada ->
                                onCambiarCompletada(tarea, completada)
                            },
                            onEliminar = { onEliminarTarea(tarea) },
                            onCompartir = { onCompartirTarea(tarea) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ElementoTarea(
    tarea: Tarea,
    onEditar: () -> Unit,
    onCambiarCompletada: (Boolean) -> Unit,
    onEliminar: () -> Unit,
    onCompartir: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onCambiarCompletada
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Prioridad: ${tarea.prioridad}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (tarea.completada) "Completada" else "Pendiente",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEditar) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar tarea"
                )
            }
            IconButton(onClick = onCompartir) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir tarea"
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea"
                )
            }
        }
    }
}
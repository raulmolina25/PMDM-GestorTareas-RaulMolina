package com.example.gestorpersonaldetareas.interfaz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.modelo.Tarea

private val PRIORIDADES_VALIDAS = listOf("Baja", "Media", "Alta")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(
    tareaEditar: Tarea?,
    guardando: Boolean,
    errorGuardado: String?,
    onGuardar: (Tarea) -> Unit,
    onCancelar: () -> Unit
) {
    val esEdicion = tareaEditar != null

    var titulo by rememberSaveable(tareaEditar?.id) {
        mutableStateOf(tareaEditar?.titulo ?: "")
    }
    var descripcion by rememberSaveable(tareaEditar?.id) {
        mutableStateOf(tareaEditar?.descripcion ?: "")
    }
    var prioridad by rememberSaveable(tareaEditar?.id) {
        mutableStateOf(tareaEditar?.prioridad ?: "Media")
    }
    var menuAbierto by rememberSaveable(tareaEditar?.id) { mutableStateOf(false) }
    var errorTitulo by rememberSaveable(tareaEditar?.id) { mutableStateOf<String?>(null) }
    var errorPrioridad by rememberSaveable(tareaEditar?.id) { mutableStateOf<String?>(null) }

    fun validarYGuardar() {
        if (guardando) return

        val tituloLimpio = titulo.trim()

        errorTitulo = when {
            tituloLimpio.isEmpty() -> "El título no puede estar vacío"
            tituloLimpio.length < 3 -> "El título debe tener al menos 3 caracteres"
            else -> null
        }
        errorPrioridad = if (prioridad in PRIORIDADES_VALIDAS) {
            null
        } else {
            "Selecciona una prioridad válida"
        }

        if (errorTitulo == null && errorPrioridad == null) {
            menuAbierto = false
            onGuardar(
                Tarea(
                    id = tareaEditar?.id ?: 0,
                    titulo = tituloLimpio,
                    descripcion = descripcion.trim(),
                    prioridad = prioridad,
                    completada = tareaEditar?.completada ?: false
                )
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(if (esEdicion) "Editar tarea" else "Nueva tarea")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    errorTitulo = null
                },
                label = { Text("Título") },
                singleLine = true,
                enabled = !guardando,
                isError = errorTitulo != null,
                supportingText = {
                    errorTitulo?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                minLines = 3,
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuAbierto && !guardando,
                onExpandedChange = { if (!guardando) menuAbierto = it }
            ) {
                OutlinedTextField(
                    value = prioridad,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !guardando,
                    label = { Text("Prioridad") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = menuAbierto && !guardando
                        )
                    },
                    isError = errorPrioridad != null,
                    supportingText = {
                        errorPrioridad?.let { Text(it) }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuAbierto && !guardando,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    PRIORIDADES_VALIDAS.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                prioridad = opcion
                                errorPrioridad = null
                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            if (errorGuardado != null) {
                Text(
                    text = errorGuardado,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onCancelar,
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { validarYGuardar() },
                    enabled = !guardando
                ) {
                    Text(if (guardando) "Guardando..." else "Guardar")
                }
            }
        }
    }
}
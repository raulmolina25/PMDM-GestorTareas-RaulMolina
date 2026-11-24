# Gestor personal de tareas

## 1. Descripción de la aplicación

Aplicación Android desarrollada en Kotlin con Jetpack Compose y Material 3 para gestionar tareas personales. Permite crear, editar, eliminar, completar y compartir tareas, establecer prioridades y consultar tareas de ejemplo desde Internet.

Las tareas y las preferencias del usuario se almacenan de forma local, permitiendo conservar los datos después de cerrar la aplicación.

## 2. Funcionalidades implementadas

- Creación de tareas con título, descripción opcional y prioridad.
- Edición de tareas existentes.
- Eliminación de tareas mediante un diálogo de confirmación.
- Marcado de tareas como completadas o pendientes.
- Validación de los campos del formulario.
- Prioridades: Baja, Media y Alta.
- Almacenamiento persistente de tareas mediante Room.
- Filtrado de tareas pendientes.
- Almacenamiento de preferencias mediante DataStore.
- Consulta de tareas de ejemplo desde JSONPlaceholder mediante Retrofit.
- Indicador de carga, gestión de errores de conexión y opción de reintentar.
- Compartición de tareas mediante Intent.
- Conservación del estado de la navegación ante cambios de orientación.

## 3. Organización básica del proyecto

El proyecto utiliza una única `MainActivity` y navegación mediante estados. El código se divide en diferentes paquetes según su responsabilidad.

| Paquete o archivo | Responsabilidad |
|---|---|
| `MainActivity.kt` | Control de la navegación y conexión entre las distintas partes de la aplicación. |
| `modelo` | Definición del modelo de datos de las tareas. |
| `interfaz` | Pantallas, formularios y componentes visuales. |
| `almacenamiento` | Base de datos Room y operaciones CRUD. |
| `preferencias` | Gestión de las preferencias mediante DataStore. |
| `comunicaciones` | Configuración de Retrofit y consulta a la API. |
| `ui.theme` | Configuración del tema visual de la aplicación. |

## 4. Bibliotecas y tecnologías utilizadas

| Biblioteca o tecnología | Utilidad |
|---|---|
| Kotlin | Lenguaje de programación. |
| Jetpack Compose | Desarrollo de la interfaz gráfica. |
| Material 3 | Componentes y diseño visual. |
| Room | Almacenamiento local de tareas. |
| DataStore Preferences | Almacenamiento de preferencias. |
| Retrofit | Peticiones HTTP a la API. |
| Gson | Conversión de respuestas JSON a objetos Kotlin. |
| Kotlin Coroutines | Ejecución de operaciones asíncronas. |
| Kotlin Flow | Observación y actualización de datos. |

La API utilizada es JSONPlaceholder:

https://jsonplaceholder.typicode.com/todos?userId=1

## 5. Instrucciones para ejecutar la aplicación

1. Clonar o descargar el repositorio de GitHub.
2. Abrir el proyecto en Android Studio.
3. Esperar a que Gradle sincronice las dependencias.
4. Seleccionar un emulador o dispositivo Android compatible.
5. Pulsar el botón Run para ejecutar la aplicación.

La conexión a Internet solo es necesaria para consultar las tareas de ejemplo.

## 6. Configuración de los emuladores

Durante las pruebas se utilizan dos emuladores con diferentes configuraciones para comprobar la adaptación y el funcionamiento de la aplicación.

| Característica | Emulador 1 | Emulador 2 |
|---|---|---|
| Modelo | PENDIENTE | PENDIENTE |
| Versión de Android | PENDIENTE | PENDIENTE |
| Nivel de API | PENDIENTE | PENDIENTE |
| Resolución | PENDIENTE | PENDIENTE |
| Tamaño de pantalla | PENDIENTE | PENDIENTE |
| Memoria RAM | PENDIENTE | PENDIENTE |

## 7. Pruebas realizadas y resultados

| ID | Prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| P01 | Crear una tarea válida | La tarea aparece en el listado. | Pendiente |
| P02 | Introducir un título vacío | Se muestra un mensaje de error. | Pendiente |
| P03 | Introducir un título de menos de tres caracteres | No se permite guardar. | Pendiente |
| P04 | Crear una tarea sin descripción | La tarea se guarda correctamente. | Pendiente |
| P05 | Editar una tarea | Se actualizan sus datos. | Pendiente |
| P06 | Completar una tarea | Cambia su estado a completada. | Pendiente |
| P07 | Cancelar una eliminación | La tarea permanece en el listado. | Pendiente |
| P08 | Confirmar una eliminación | La tarea desaparece del listado. | Pendiente |
| P09 | Cerrar y volver a abrir la aplicación | Las tareas se conservan mediante Room. | Pendiente |
| P10 | Activar el filtro de pendientes | Solo se muestran las tareas pendientes. | Pendiente |
| P11 | Reiniciar la aplicación con el filtro activado | DataStore conserva la preferencia. | Pendiente |
| P12 | Consultar la API con Internet | Se muestran las tareas de ejemplo. | Pendiente |
| P13 | Consultar la API sin Internet | Se muestra un mensaje de error. | Pendiente |
| P14 | Reintentar la consulta recuperando Internet | Se cargan las tareas de ejemplo. | Pendiente |
| P15 | Compartir una tarea | Se abre el selector de aplicaciones. | Pendiente |
| P16 | Girar el dispositivo durante la creación de una tarea | Se conservan los datos del formulario. | Pendiente |
| P17 | Girar el dispositivo con los ejemplos descargados | Se conservan los ejemplos. | Pendiente |
| P18 | Girar durante la consulta inicial a la API | Se recupera automáticamente la consulta. | Pendiente |
| P19 | Ejecutar la aplicación en ambos emuladores | La interfaz se adapta correctamente. | Pendiente |

## 8. Capturas de pantalla

### Listado principal

![Listado principal](capturas/listado.png)

### Formulario de creación

![Formulario de creación](capturas/formulario.png)

### Consulta de tareas desde la API

![Consulta a la API](capturas/api.png)

### Confirmación de eliminación

![Confirmación de eliminación](capturas/eliminar.png)

### Filtro de tareas pendientes

![Filtro de pendientes](capturas/filtro.png)

### Compartir una tarea

![Compartir tarea](capturas/compartir.png)

## 9. Uso de inteligencia artificial

Durante el desarrollo del proyecto se ha utilizado ChatGPT como herramienta de apoyo para la planificación, el aprendizaje, la resolución de dudas y la revisión del código.

[Chat utilizado durante el desarrollo](URL_DEL_CHAT)

## 10. Incidencias conocidas y funcionalidades pendientes

Todas las funcionalidades principales previstas se encuentran implementadas.

Como limitación conocida, la consulta de tareas de ejemplo requiere conexión a Internet y depende de la disponibilidad de la API externa JSONPlaceholder.

Las incidencias adicionales que puedan detectarse durante las pruebas finales se documentarán en este apartado.

## 11. Autor

**Raúl Molina Cordones**

2.º de Desarrollo de Aplicaciones Multiplataforma (DAM).

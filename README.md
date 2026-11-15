# PMDM-GestorTareas-RaulMolina

# Gestor personal de tareas

Aplicación Android desarrollada en **Kotlin** utilizando **Jetpack Compose y Material 3**.

Este proyecto consiste en un gestor personal de tareas que permite organizar actividades, establecer prioridades, marcar tareas como completadas y consultar ejemplos de tareas obtenidos desde Internet.

La aplicación utiliza almacenamiento local para conservar las tareas y las preferencias del usuario.

## Funcionalidades

- **Crear tareas:** añadir nuevas tareas indicando un título, una descripción opcional y una prioridad.
- **Editar tareas:** modificar la información de las tareas existentes.
- **Completar tareas:** marcar las tareas como completadas o pendientes.
- **Eliminar tareas:** borrar tareas mediante un diálogo de confirmación.
- **Filtrar tareas:** mostrar únicamente las tareas pendientes.
- **Guardar tareas:** conservar las tareas aunque se cierre la aplicación.
- **Guardar preferencias:** recordar si el usuario tiene activado el filtro de tareas pendientes.
- **Consultar tareas de ejemplo:** obtener tareas desde una API pública mediante una conexión a Internet.
- **Compartir tareas:** enviar la información de una tarea a otras aplicaciones instaladas en el dispositivo.

## Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Kotlin | Lenguaje de programación utilizado para desarrollar la aplicación. |
| Android Studio | Entorno de desarrollo del proyecto. |
| Jetpack Compose | Creación de la interfaz gráfica. |
| Material 3 | Componentes y diseño visual de la aplicación. |
| Room | Base de datos local para almacenar las tareas. |
| DataStore | Almacenamiento de las preferencias del usuario. |
| Retrofit | Realización de peticiones HTTP a una API. |
| Gson | Conversión de las respuestas JSON a objetos Kotlin. |
| Coroutines y Flow | Ejecución de operaciones asíncronas y actualización de los datos. |
| Intent | Compartir información con otras aplicaciones Android. |

## Estructura del proyecto

El proyecto utiliza una única `MainActivity` para controlar la navegación entre las diferentes pantallas mediante estados.

El código está organizado en los siguientes paquetes:

```text
com.example.gestorpersonaldetareas
│
├── MainActivity.kt
│
├── modelo/
│   └── Tarea.kt
│
├── interfaz/
│   ├── Pantalla.kt
│   ├── PantallaPrincipal.kt
│   ├── PantallaFormulario.kt
│   └── PantallaEjemplos.kt
│
├── almacenamiento/
│   ├── TareaDao.kt
│   └── BaseDatosTareas.kt
│
├── preferencias/
│   └── Preferencias.kt
│
└── comunicaciones/
    ├── TareaEjemplo.kt
    ├── ApiTareas.kt
    └── ClienteRetrofit.kt
```

### Descripción de los paquetes

- **modelo:** contiene la clase `Tarea`, que representa las tareas de la aplicación.
- **interfaz:** contiene las pantallas desarrolladas con Jetpack Compose y el enumerado utilizado para la navegación.
- **almacenamiento:** contiene la configuración de Room y las operaciones para consultar, insertar, actualizar y eliminar tareas.
- **preferencias:** permite guardar y recuperar el filtro de tareas pendientes mediante DataStore.
- **comunicaciones:** contiene las clases necesarias para consultar tareas de ejemplo utilizando Retrofit.

La clase `MainActivity` se encarga de conectar las diferentes partes de la aplicación y controlar la navegación.

## Almacenamiento de datos

### Room

Se utiliza Room para almacenar las tareas de forma local.

Cada tarea contiene los siguientes datos:

| Campo | Descripción |
|---|---|
| id | Identificador único generado automáticamente. |
| titulo | Título de la tarea. |
| descripcion | Descripción opcional. |
| prioridad | Prioridad Baja, Media o Alta. |
| completada | Indica si la tarea está completada. |

Las operaciones de la base de datos se realizan mediante `TareaDao`.

### DataStore

Se utiliza DataStore para guardar la preferencia **Mostrar solo pendientes**.

De esta manera, cuando el usuario vuelve a abrir la aplicación, se recupera el valor que había seleccionado anteriormente.

## Consulta de tareas desde Internet

La aplicación utiliza Retrofit para consultar la API pública de JSONPlaceholder.

**API utilizada:** https://jsonplaceholder.typicode.com/

La petición realizada es:

```http
GET https://jsonplaceholder.typicode.com/todos?userId=1
```

Los resultados se muestran en una pantalla independiente de las tareas personales.

Durante la consulta se muestra un indicador de carga. Si ocurre algún error de conexión, se informa al usuario y se permite reintentar la operación.

Las tareas descargadas son únicamente ejemplos y no se almacenan en Room.

## Compartir tareas

La aplicación permite compartir una tarea mediante `Intent.ACTION_SEND`.

Al pulsar el botón de compartir, Android muestra un selector de aplicaciones compatibles.

El texto compartido incluye el título, la descripción si existe, la prioridad y el estado de la tarea.

## Ejecución del proyecto

Para ejecutar la aplicación desde Android Studio:

1. Clonar o descargar este repositorio.
2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar a que Gradle sincronice las dependencias.
4. Seleccionar un emulador o dispositivo Android compatible.
5. Pulsar **Run** para compilar y ejecutar la aplicación.

La aplicación necesita conexión a Internet únicamente para consultar las tareas de ejemplo. Las tareas personales y las preferencias se almacenan localmente.

## Pruebas de funcionamiento

Las principales comprobaciones previstas para la aplicación son:

- Crear, editar, completar y eliminar tareas.
- Validar que el título no esté vacío y tenga al menos tres caracteres.
- Comprobar que las tareas se conservan al cerrar y volver a abrir la aplicación.
- Activar y desactivar el filtro de tareas pendientes.
- Comprobar que DataStore conserva la preferencia seleccionada.
- Consultar las tareas de ejemplo desde Internet.
- Comprobar el mensaje de error cuando no existe conexión.
- Compartir tareas mediante otras aplicaciones.
- Comprobar el comportamiento de la aplicación al girar el dispositivo.

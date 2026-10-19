
TurcoEspañol Kids 🇹🇷 🇪🇸
Aplicación educativa para Android orientada a niñas y niños turcoparlantes menores de 10 años que están comenzando a aprender vocabulario en español. Es un proyecto académico desarrollado en Kotlin con Jetpack Compose para la asignatura DSY1105 – Desarrollo de Aplicaciones Móviles.

Estado: prototipo académico. El proyecto ha compilado correctamente en Android Studio según las verificaciones del equipo; las pruebas funcionales completas en un dispositivo Android todavía están pendientes.

Objetivo
Facilitar un primer acercamiento al español mediante palabras agrupadas por categorías, apoyo visual, pronunciación mediante voz sintetizada y actividades de práctica. El progreso se asocia a avatares ficticios, sin requerir nombres ni fotografías personales.

Funcionalidades implementadas en código
Bienvenida: acceso al recorrido principal de aprendizaje.
Configuración de sesión: selección de avatar ficticio y validación visual de la selección.
Categorías: Animales, Colores y Frutas, con sus nombres en español y turco.
Vocabulario: tarjetas de palabras con imágenes representadas por emojis, traducción y avance entre palabras.
Pronunciación: reproducción de palabras en español mediante TextToSpeech de Android.
Minijuego: preguntas de alternativas, validación de respuestas, retroalimentación visual y puntuación.
Progreso: registro local de lecciones completadas y mejor puntaje, diferenciado por avatar y categoría.
Reinicio del progreso: opción de borrar los resultados del avatar seleccionado, con confirmación previa.
Busca y fotografía: apertura de la cámara del sistema para fotografiar objetos asociados al vocabulario y mostrar una vista previa temporal.
Navegación y estados: rutas entre pantallas, gestión de estado y animaciones de retroalimentación.
Recursos nativos del dispositivo
Recurso	Integración	Finalidad
Voz sintetizada (TextToSpeech)	API TextToSpeech	Escuchar cómo se pronuncian palabras en español.
Cámara	ActivityResultContracts.TakePicturePreview()	Obtener y visualizar temporalmente una miniatura de un objeto.
La actividad de cámara no reconoce objetos automáticamente. La foto se utiliza como apoyo visual: la aplicación no la incorpora a DataStore ni crea un archivo permanente desde esta actividad. Se indica fotografiar objetos, no personas.

Tecnologías y arquitectura
Lenguaje: Kotlin.
Interfaz: Jetpack Compose y Material 3.
Navegación: Navigation Compose.
Arquitectura: MVVM (model, viewmodel, repository, ui, navigation).
Gestión de estado: StateFlow y observación compatible con el ciclo de vida.
Persistencia local: DataStore Preferences.
Concurrencia: corrutinas de Kotlin.
APIs del dispositivo: Android TextToSpeech y Activity Result API.
Colaboración: Git, GitHub y planificación del equipo en Trello.
Estructura principal
app/src/main/java/com/example/appturcoespanol/
├── model/          # Modelos y estados de interfaz
├── navigation/     # AppNavigation y rutas
├── repository/     # Vocabulario y progreso en DataStore
├── tts/            # TextToSpeechManager
├── ui/
│   ├── screens/    # Bienvenida, Configuración, Categorías,
│   │               # Vocabulario, Juego, Progreso y Cámara
│   └── theme/      # Tema visual
└── viewmodel/      # Lógica y estados de cada funcionalidad
Cómo abrir y ejecutar el proyecto
Clonar el repositorio desde GitHub o descargar su código fuente.
Abrir Android Studio y seleccionar Open.
Elegir la carpeta raíz del proyecto AppTurcoEspanol (donde están los archivos de Gradle).
Esperar a que finalice Gradle Sync y se descarguen las dependencias necesarias.
Ejecutar Build → Make Project para comprobar la compilación.
Conectar un dispositivo Android compatible con depuración USB habilitada, o configurar un emulador compatible.
Presionar Run para instalar e iniciar la aplicación.
Seleccionar un avatar, elegir una categoría y probar las actividades.
Nota: para probar la cámara se necesita una aplicación de cámara disponible en el dispositivo. Para la pronunciación, Android debe tener un motor TextToSpeech con voz en español disponible. La integración de la cámara usa la actividad de cámara del sistema; no requiere declarar el permiso CAMERA para este flujo.

Almacenamiento y privacidad
El avance de aprendizaje se guarda localmente mediante DataStore Preferences.
Los resultados se relacionan con el avatar ficticio y la categoría elegidos.
No es necesario ingresar datos personales para utilizar las actividades descritas.
La actividad de cámara está diseñada para fotografiar objetos, juguetes o dibujos, no personas.
La vista previa de la cámara permanece en memoria durante la actividad y puede eliminarse desde la pantalla; no se guarda en DataStore.
Este prototipo no incluye reconocimiento automático de imágenes ni envío de fotografías a un servidor.
Estado de las pruebas
Comprobación	Estado
Compilación del proyecto en Android Studio	Confirmada por el equipo.
Navegación completa entre pantallas	Pendiente de prueba en ejecución.
Reproducción de voz en español	Pendiente de prueba en dispositivo.
Captura y vista previa de cámara	Pendiente de prueba en dispositivo.
Conservación del progreso al cerrar y reabrir	Pendiente de prueba en dispositivo.
Reinicio de progreso sin afectar otros avatares	Pendiente de prueba en dispositivo.
Equipo de desarrollo
Integrante 1: [Nombre y apellido de Darling].
Integrante 2: [Nombre y apellido del otro integrante].
Sección / equipo: [Completar].
Docente: [Completar si corresponde].
Tablero Trello: [Agregar enlace del tablero].
El trabajo se organiza mediante ramas y commits en GitHub. Cada integrante debe registrar sus aportes y evidencias de colaboración.

Pendientes antes de la entrega
Realizar y documentar pruebas funcionales en un dispositivo Android.
Incorporar capturas reales de las pantallas, si la entrega las requiere.
Completar los datos del equipo y el enlace de Trello.
Documentar cualquier consulta pedagógica efectivamente realizada, sin atribuir entrevistas que aún no hayan ocurrido.
Verificar las condiciones y la fecha límite de entrega indicadas en la pauta oficial.
Proyecto académico — DSY1105, Desarrollo de Aplicaciones Móviles.



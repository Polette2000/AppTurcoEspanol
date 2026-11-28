# TurcoEspañol Kids 🇹🇷 🇪🇸

**Aplicación educativa para Android — EsTurk**

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles

**Estado:** Prototipo académico funcional en proceso de validación final para el EFT.

## 1. Descripción del proyecto

TurcoEspañol Kids es una aplicación educativa para dispositivos Android, orientada a niñas y niños turcoparlantes menores de 10 años que están comenzando a aprender vocabulario en español.

El proyecto se desarrolló utilizando Kotlin y Jetpack Compose, incorporando actividades interactivas, pronunciación de palabras, imágenes, juegos educativos y seguimiento del progreso.

La aplicación también permite consultar traducciones mediante una API REST propia desarrollada con Spring Boot.

## 2. Objetivo

Facilitar un primer acercamiento al idioma español mediante:

- Aprendizaje de vocabulario por categorías.
- Asociación de palabras en español y turco.
- Pronunciación mediante voz sintetizada.
- Juegos interactivos con retroalimentación.
- Seguimiento del progreso educativo.
- Actividades con recursos nativos de Android.

El progreso se asocia a avatares ficticios, sin necesidad de recopilar datos personales de los niños.

## 3. Funcionalidades implementadas

**Bienvenida:** pantalla inicial para acceder a la aplicación.

**Configuración de sesión:** selección de idioma y avatar infantil.

**Categorías:** aprendizaje de vocabulario mediante categorías como Animales, Colores y Frutas.

**Vocabulario:** tarjetas educativas con palabras, traducciones e imágenes representadas por emojis.

**Pronunciación:** reproducción de palabras en español mediante TextToSpeech.

**Minijuego:** preguntas con alternativas, comprobación de respuestas, retroalimentación visual y puntuación.

**Progreso:** almacenamiento local de lecciones completadas y mejores puntajes por avatar y categoría.

**Reinicio del progreso:** opción para reiniciar resultados, con confirmación previa.

**Busca y fotografía:** utilización de la cámara del dispositivo para capturar imágenes y mostrar una vista previa temporal.

**Consulta API REST:** comunicación con un servidor Spring Boot para obtener traducciones.

## 4. Recursos nativos de Android

### 4.1 TextToSpeech (TTS)

Se utiliza la API nativa TextToSpeech de Android para reproducir la pronunciación en español de las palabras del vocabulario.

**Estado:** funcionamiento comprobado en la aplicación.

### 4.2 Cámara

Se utiliza `ActivityResultContracts.TakePicturePreview()` para abrir la cámara del sistema y obtener una imagen en miniatura.

La fotografía se utiliza como apoyo visual dentro de la actividad. No se almacena permanentemente mediante esta funcionalidad.

**Estado:** captura y visualización comprobadas.

Estas dos funcionalidades permiten cumplir el requisito de incorporar al menos dos recursos nativos de Android.

## 5. Integración de API REST

La aplicación consume una API REST propia desarrollada con Java y Spring Boot.

**Tecnologías utilizadas:**

- Spring Boot para el servidor backend.
- API REST con respuestas JSON.
- Retrofit para las consultas HTTP desde Android.
- Kotlin para el procesamiento de las respuestas.

### Endpoint de traducción

**Método:** GET

**Ruta:** `/api/traduccion`

**Parámetro:** `palabra`

Ejemplo de consulta desde el computador:

`http://localhost:8080/api/traduccion?palabra=gato`

Respuesta comprobada:

```json
{
  "traduccion": "kedi",
  "palabra": "gato"
}
```

### Conexión desde Android

En el emulador Android se utiliza:

`http://10.0.2.2:8080/`

Esta dirección permite acceder al servidor Spring Boot que se encuentra ejecutándose en el computador anfitrión.

### Cómo ejecutar la API

1. Abrir el proyecto backend `api-turco-espanol` en Visual Studio Code.
2. Abrir una terminal en la carpeta donde está `pom.xml`.
3. Ejecutar `.\mvnw.cmd spring-boot:run`.
4. Comprobar que Spring Boot se inicie en el puerto 8080.
5. Probar el endpoint desde el navegador.
6. Mantener el servidor ejecutándose.
7. Abrir EsTurk en el emulador Android.
8. Entrar a Vocabulario y presionar **Consultar API REST**.

**Resultado:** se verificó la respuesta JSON desde Chrome y se observó la traducción online dentro de la aplicación Android.

**Limitación:** esta configuración requiere que el backend esté disponible en el computador anfitrión. No corresponde a una API desplegada públicamente en Internet.

## 6. Pruebas unitarias

La aplicación utiliza JUnit para realizar pruebas de la lógica de negocio.

**Archivo principal:** `LogicaNegocioTest.kt`

Las pruebas cubren aspectos relacionados con:

- Validación de respuestas correctas.
- Validación de respuestas incorrectas.
- Manejo de respuestas vacías.
- Cálculo de puntuaciones.
- Prevención de puntuaciones duplicadas.
- Finalización del juego.
- Disponibilidad del vocabulario.

### Ejecución

Desde Android Studio se puede abrir `LogicaNegocioTest.kt` y seleccionar **Run 'LogicaNegocioTest'**.

También se pueden ejecutar las pruebas mediante:

`.\gradlew.bat testDebugUnitTest`

### Resultado obtenido

**8 pruebas aprobadas y 0 fallidas.**

El resultado fue comprobado en Android Studio desde la rama `dev-darling`.

**Pendiente:** repetir o confirmar la ejecución en la versión definitiva de la rama `master` para documentar la entrega final.

## 7. Generación del APK firmado

El proyecto contempla la generación de un APK firmado en modo release para su instalación y distribución.

La firma está gestionada por una integrante del equipo.

### Procedimiento

1. Abrir el proyecto en Android Studio.
2. Seleccionar **Build → Generate Signed Bundle / APK**.
3. Elegir APK.
4. Seleccionar el almacén de claves existente.
5. Ingresar las credenciales de firma de forma segura.
6. Seleccionar la variante release.
7. Generar el APK.
8. Verificar su firma e instalación en un dispositivo compatible.

**Estado:** el equipo dispone de un almacén de claves, pero está pendiente documentar la verificación del APK release definitivo.

Por seguridad, el archivo de claves y sus contraseñas no deben publicarse en GitHub.

## 8. Tecnologías y arquitectura

- Lenguaje: Kotlin.
- Interfaz: Jetpack Compose y Material 3.
- Navegación: Navigation Compose.
- Arquitectura: MVVM.
- Gestión de estado: StateFlow.
- Persistencia local: DataStore Preferences.
- Concurrencia: corrutinas de Kotlin.
- Comunicación HTTP: Retrofit.
- Backend: Java y Spring Boot.
- Recursos nativos: TextToSpeech y Activity Result API.
- Pruebas unitarias: JUnit.
- Control de versiones: Git y GitHub.
- Gestión del trabajo: Trello.

### Estructura principal

`app/src/main/java/com/example/appturcoespanol/`

- `model/`: modelos de datos.
- `navigation/`: rutas y navegación.
- `network/`: comunicación con la API REST.
- `repository/`: acceso a datos, traducciones y progreso.
- `tts/`: gestión de TextToSpeech.
- `ui/screens/`: pantallas de la aplicación.
- `ui/theme/`: tema visual.
- `viewmodel/`: lógica y estados de la interfaz.

Las pruebas unitarias se encuentran en `app/src/test/`.

## 9. Instalación y ejecución

1. Clonar el repositorio desde GitHub.
2. Abrir la carpeta raíz de AppTurcoEspanol en Android Studio.
3. Esperar la sincronización de Gradle.
4. Ejecutar **Build → Make Project**.
5. Configurar un emulador Android o conectar un dispositivo compatible.
6. Presionar **Run**.
7. Seleccionar idioma y avatar.
8. Explorar las categorías y funcionalidades educativas.

Para utilizar la API REST local es necesario ejecutar también el backend Spring Boot.

La cámara requiere una aplicación de cámara disponible y TextToSpeech necesita un motor de voz compatible con español.

## 10. Almacenamiento y privacidad

La aplicación utiliza DataStore Preferences para almacenar localmente el progreso educativo.

Los resultados se relacionan con avatares ficticios y categorías de aprendizaje.

No es necesario ingresar nombres ni fotografías personales para utilizar las actividades descritas.

La actividad de cámara no realiza reconocimiento automático de imágenes ni envía fotografías al servidor.

La vista previa se mantiene temporalmente en la actividad y no se incorpora a DataStore.

## 11. Estado de las verificaciones

| Comprobación | Estado |
|---|---|
| Compilación en Android Studio | Confirmada |
| Pronunciación TextToSpeech | Comprobada |
| Captura y vista previa de cámara | Comprobada |
| Servidor Spring Boot | Comprobado |
| Respuesta JSON de la API | Comprobada |
| Traducción online en Android | Comprobada |
| 8 pruebas unitarias en dev-darling | Aprobadas |
| Pruebas unitarias en master final | Pendientes de confirmar |
| Navegación funcional completa | Pendiente de documentar |
| Persistencia del progreso | Pendiente de prueba documentada |
| Reinicio independiente por avatar | Pendiente de prueba documentada |
| APK firmado release | Pendiente de verificación final |

## 12. Contexto educativo y alineación con el EFT

TurcoEspañol Kids busca apoyar el aprendizaje inicial del español en niños turcoparlantes mediante un entorno visual e interactivo.

La solución integra recursos nativos del dispositivo, consumo de una API REST propia, lógica de negocio sometida a pruebas unitarias y una arquitectura que separa la interfaz, los datos y la lógica de aplicación.

El diseño utiliza avatares ficticios y almacenamiento local para reducir la necesidad de recopilar información personal.

La validación definitiva frente a todos los parámetros del EFT requiere contrastar la aplicación con la pauta oficial de evaluación.

## 13. Equipo de desarrollo

- **Patience Agunanna**
- **Ruth Honorio**
- **Darling Pinol**

**Docente:** Luis Becerra

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles

**Tablero Trello:** https://trello.com/b/XyAIiJD7/applicaci%C3%B3n

**Repositorio GitHub:** https://github.com/Polette2000/AppTurcoEspanol

El equipo utiliza GitHub para administrar ramas, commits e integración de avances.

## 14. Pendientes antes de la entrega

- Confirmar las pruebas unitarias en la versión final de `master`.
- Comprobar el APK release firmado y su instalación.
- Completar las pruebas documentadas de navegación, persistencia y reinicio de progreso.
- Incorporar capturas reales de las funcionalidades y resultados de pruebas.
- Contrastar el proyecto con los parámetros específicos del EFT.
- Verificar las instrucciones y la fecha de entrega de la asignatura.

---

**Proyecto académico — DSY1105, Desarrollo de Aplicaciones Móviles.**
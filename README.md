# 🇹🇷 🇪🇸 TurcoEspañol Kids — EsTurk

**Aplicación educativa para el aprendizaje de español y turco**

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC  
**Tipo de proyecto:** Aplicación móvil Android  
**Estado:** Prototipo académico funcional, en validación final para el EFT.

---

## 1. Descripción del proyecto

**TurcoEspañol Kids (EsTurk)** es una aplicación educativa para dispositivos Android, orientada principalmente a niñas y niños turcoparlantes menores de 10 años que están comenzando a aprender vocabulario básico en español.

La aplicación busca facilitar el aprendizaje mediante actividades interactivas, recursos visuales, pronunciación de palabras, juegos educativos y seguimiento del progreso.

El proyecto está desarrollado en **Kotlin con Jetpack Compose**, utilizando una arquitectura MVVM para separar la interfaz de usuario, los datos y la lógica de negocio.

También incorpora recursos nativos de Android, consumo de una API REST propia desarrollada con Spring Boot, pruebas unitarias y un proceso de generación de APK firmado.

## 2. Problema identificado

El aprendizaje de un nuevo idioma durante la infancia puede resultar complejo cuando no se cuenta con herramientas adaptadas a la edad, el idioma de origen y las necesidades educativas de los estudiantes.

En particular, los niños turcoparlantes que comienzan a aprender español necesitan recursos visuales, auditivos e interactivos que faciliten la comprensión de palabras básicas.

**EsTurk** propone una solución móvil que permite practicar vocabulario de manera sencilla, utilizando imágenes, pronunciación y actividades lúdicas.

## 3. Objetivo general

Desarrollar una aplicación móvil educativa para Android que facilite el aprendizaje inicial del vocabulario español en niños turcoparlantes mediante actividades interactivas, recursos nativos del dispositivo y herramientas de seguimiento del aprendizaje.

### Objetivos específicos

- Implementar una interfaz educativa e intuitiva mediante Jetpack Compose.
- Organizar el vocabulario en categorías temáticas.
- Incorporar pronunciación de palabras mediante TextToSpeech.
- Utilizar la cámara como recurso de apoyo visual.
- Integrar una API REST para consultar traducciones.
- Implementar actividades de juego que refuercen el aprendizaje.
- Registrar el progreso educativo mediante almacenamiento local.
- Aplicar pruebas unitarias sobre la lógica de negocio.
- Preparar una versión release firmada para distribución.

## 4. Público objetivo

La aplicación está dirigida a niñas y niños turcoparlantes menores de 10 años que están iniciando el aprendizaje del español.

Se utiliza una interfaz con colores, elementos visuales, emojis, avatares y botones de fácil identificación.

No es obligatorio proporcionar nombres reales ni fotografías personales para utilizar las actividades educativas.

## 5. Funcionalidades de la aplicación

### 5.1 Pantalla de bienvenida

Presenta la aplicación y permite iniciar el recorrido educativo.

El usuario puede seleccionar su idioma antes de comenzar.

### 5.2 Configuración de sesión

Permite seleccionar un avatar ficticio y establecer las preferencias iniciales de la sesión.

Se utiliza una validación visual para confirmar las opciones seleccionadas.

### 5.3 Categorías educativas

El vocabulario está organizado en categorías como:

- Animales.
- Colores.
- Frutas.

Cada categoría incluye palabras asociadas al aprendizaje de español y turco.

### 5.4 Vocabulario interactivo

Presenta tarjetas educativas con palabras, traducciones y representaciones visuales mediante emojis.

El usuario puede avanzar y retroceder entre las palabras.

También puede escuchar la pronunciación y consultar traducciones mediante la API REST.

### 5.5 Pronunciación de palabras

Se utiliza TextToSpeech para reproducir palabras mediante el motor de voz del dispositivo Android.

Esta funcionalidad proporciona apoyo auditivo al proceso de aprendizaje.

### 5.6 Juego educativo

Incluye preguntas de selección múltiple para reforzar el vocabulario aprendido.

El sistema permite:

- Presentar preguntas.
- Seleccionar respuestas.
- Comprobar respuestas correctas e incorrectas.
- Mostrar retroalimentación visual.
- Calcular puntuaciones.
- Finalizar la actividad.

### 5.7 Registro del progreso

La aplicación registra localmente información del aprendizaje, incluyendo lecciones completadas y mejores puntuaciones.

El progreso se organiza por avatar y categoría.

### 5.8 Reinicio del progreso

Permite reiniciar el progreso del avatar seleccionado mediante una confirmación previa.

### 5.9 Busca y fotografía

Permite utilizar la cámara del dispositivo para tomar fotografías de objetos relacionados con el vocabulario aprendido.

Las imágenes funcionan como apoyo visual dentro de la actividad.

La aplicación no realiza reconocimiento automático de objetos.

### 5.10 Consulta de API REST

Permite consultar traducciones a través de un servidor Spring Boot.

La respuesta recibida se muestra dentro de la aplicación como una traducción online.

---

## 6. Recursos nativos de Android

La aplicación integra dos recursos nativos principales.

### 6.1 TextToSpeech (TTS)

**Tecnología:** API TextToSpeech de Android.

**Finalidad:** reproducir palabras para apoyar la pronunciación y el aprendizaje.

**Implementación:** integración con el motor de voz disponible en el dispositivo.

**Prueba realizada:** reproducción de pronunciación desde la aplicación.

**Resultado:** funcionamiento confirmado durante las pruebas funcionales.

### 6.2 Cámara del dispositivo

**Tecnología:** Activity Result API.

Se utiliza:

`ActivityResultContracts.TakePicturePreview()`

**Finalidad:** permitir que los estudiantes fotografíen objetos relacionados con las categorías educativas.

**Implementación:** apertura de la aplicación de cámara del sistema y recepción de una vista previa de la imagen.

**Prueba realizada:** captura de fotografía y visualización dentro de EsTurk.

**Resultado:** funcionamiento confirmado durante las pruebas funcionales.

La imagen capturada mediante esta actividad es temporal y no se almacena permanentemente en DataStore.

Para este flujo, basado en la actividad externa de cámara, no se requiere solicitar directamente el permiso `CAMERA`.

---

## 7. Consumo de API REST

La aplicación integra una API REST propia desarrollada utilizando **Java y Spring Boot**.

Esta API proporciona traducciones para apoyar el aprendizaje de vocabulario.

### 7.1 Tecnologías utilizadas

- Java.
- Spring Boot.
- Maven.
- HTTP.
- JSON.
- Retrofit en Android.
- Kotlin y corrutinas.

### 7.2 Endpoint implementado

**Método HTTP:** GET

**Ruta:**

`/api/traduccion`

**Parámetro de consulta:**

`palabra`

### 7.3 Ejemplo de solicitud

Desde el navegador del computador:

`http://localhost:8080/api/traduccion?palabra=gato`

### 7.4 Respuesta JSON obtenida

Durante las pruebas se obtuvo:

```json
{
  "traduccion": "kedi",
  "palabra": "gato"
}
```

La respuesta corresponde a la traducción de la palabra española "gato" al turco "kedi".

### 7.5 Configuración en Android

El cliente Retrofit utiliza la dirección:

```kotlin
.baseUrl("http://10.0.2.2:8080/")
```

La dirección `10.0.2.2` permite que el emulador Android acceda al servidor que se ejecuta en el computador anfitrión.

Esta dirección no corresponde a un servidor público y requiere que el backend se encuentre ejecutándose localmente.

### 7.6 Ejecución del backend

Para ejecutar el servidor:

1. Abrir Visual Studio Code.
2. Abrir el proyecto `api-turco-espanol`.
3. Ubicarse en la carpeta que contiene `pom.xml`.
4. Abrir una terminal.
5. Ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

6. Comprobar que Spring Boot se inicia en el puerto `8080`.
7. Mantener el servidor ejecutándose mientras se utiliza la aplicación.

### 7.7 Pruebas realizadas

**Prueba 1 — Inicio del servidor**

Se ejecutó Spring Boot desde Visual Studio Code.

Resultado: servidor iniciado correctamente en el puerto 8080.

**Prueba 2 — Consulta desde navegador**

Se utilizó Chrome para realizar una petición GET.

Resultado: respuesta JSON correcta con la traducción de `gato` a `kedi`.

**Prueba 3 — Consumo desde Android**

Se ejecutó EsTurk en el emulador Android y se utilizó el botón **Consultar API REST**.

Resultado: la aplicación mostró el mensaje **Traducción online: kedi**.

**Conclusión:** se comprobó el funcionamiento del backend y su integración con la aplicación móvil.

---

## 8. Arquitectura del proyecto

La aplicación utiliza el patrón arquitectónico **MVVM (Model–View–ViewModel)**.

Esta arquitectura permite separar las responsabilidades de la aplicación y facilitar su mantenimiento.

### Model

Contiene las estructuras de datos y los modelos utilizados por las funcionalidades.

Incluye entidades relacionadas con vocabulario, preguntas, categorías, usuarios ficticios y estados.

### View

Corresponde a las pantallas construidas mediante Jetpack Compose.

Gestiona la presentación de información y la interacción con el usuario.

### ViewModel

Administra los estados y coordina la lógica necesaria para las funcionalidades de la interfaz.

### Repository

Gestiona el acceso a información, recursos de vocabulario, persistencia y consultas a servicios externos.

### Network

Contiene los componentes relacionados con la comunicación HTTP y el consumo de la API REST.

## 9. Tecnologías utilizadas

| Tecnología | Finalidad |
|---|---|
| Kotlin | Desarrollo de la aplicación Android |
| Jetpack Compose | Construcción de interfaces |
| Material 3 | Componentes visuales |
| Navigation Compose | Navegación entre pantallas |
| MVVM | Organización de la arquitectura |
| StateFlow | Gestión de estados |
| DataStore Preferences | Persistencia local |
| Corrutinas | Operaciones asíncronas |
| Retrofit | Consumo de la API REST |
| Spring Boot | Servidor backend |
| Java | Implementación del backend |
| Maven | Gestión del proyecto Spring Boot |
| JUnit | Pruebas unitarias |
| TextToSpeech | Pronunciación |
| Activity Result API | Integración de cámara |
| Git y GitHub | Control de versiones |
| Trello | Organización del equipo |

## 10. Estructura del proyecto

La aplicación está organizada principalmente de la siguiente manera:

```text
AppTurcoEspanol/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/appturcoespanol/
│       │   │       ├── model/
│       │   │       ├── navigation/
│       │   │       ├── network/
│       │   │       ├── repository/
│       │   │       ├── tts/
│       │   │       ├── ui/
│       │   │       │   ├── screens/
│       │   │       │   └── theme/
│       │   │       └── viewmodel/
│       │   └── AndroidManifest.xml
│       ├── test/
│       │   └── java/
│       │       └── com/example/appturcoespanol/
│       │           ├── ExampleUnitTest.kt
│       │           └── LogicaNegocioTest.kt
│       └── androidTest/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew.bat
└── README.md
```

El backend Spring Boot se mantiene como proyecto independiente, con su propio código fuente, controlador REST y configuración Maven.

---

## 11. Pruebas unitarias

Se implementaron pruebas unitarias mediante **JUnit** para verificar comportamientos relevantes de la lógica de negocio.

### Archivo principal

`LogicaNegocioTest.kt`

### Funcionalidades evaluadas

Las pruebas abarcan:

- Respuestas correctas.
- Respuestas incorrectas.
- Respuestas sin seleccionar.
- Cálculo de puntuaciones.
- Control de respuestas repetidas.
- Finalización de actividades.
- Comportamiento de la lógica de juego.
- Disponibilidad del vocabulario.

### Ejecución desde Android Studio

1. Abrir el proyecto Android.
2. Ir a `app/src/test/java/com/example/appturcoespanol/`.
3. Abrir `LogicaNegocioTest.kt`.
4. Presionar el botón Run junto a la clase.
5. Revisar los resultados.

También se pueden ejecutar desde la terminal de la raíz del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest
```

### Resultados obtenidos

| Indicador | Resultado |
|---|---|
| Pruebas ejecutadas | 8 |
| Pruebas aprobadas | 8 |
| Pruebas fallidas | 0 |
| Tiempo registrado | 37,7 segundos |
| Rama comprobada | dev-darling |

**Resultado:** las ocho pruebas unitarias finalizaron correctamente.

La validación de la versión definitiva en `master` debe confirmarse si esta es la rama seleccionada para la entrega.

---

## 12. Pruebas funcionales

Se realizaron verificaciones de las funcionalidades principales.

| Funcionalidad | Resultado |
|---|---|
| Compilación en Android Studio | Correcta |
| TextToSpeech | Comprobado |
| Cámara y vista previa | Comprobado |
| Inicio de Spring Boot | Comprobado |
| Consulta REST desde Chrome | Comprobado |
| Consumo REST desde Android | Comprobado |
| Pruebas JUnit en dev-darling | 8 aprobadas |
| Navegación completa | Pendiente de documentar |
| Persistencia tras reiniciar la aplicación | Pendiente de documentar |
| Reinicio independiente del progreso | Pendiente de documentar |
| Instalación de APK release firmado | Pendiente de verificar |

Las capturas de estas pruebas deben conservarse como evidencia de la entrega.

---

## 13. Generación de APK firmado (release)

La distribución de la aplicación requiere generar una versión APK en modo release firmada digitalmente.

### Procedimiento de generación

1. Abrir AppTurcoEspanol en Android Studio.
2. Seleccionar **Build → Generate Signed Bundle / APK**.
3. Elegir la opción **APK**.
4. Presionar **Next**.
5. Seleccionar un almacén de claves existente.
6. Introducir las credenciales de firma.
7. Seleccionar la variante **release**.
8. Generar el APK firmado.

### Gestión de la firma

El equipo cuenta con un archivo de almacén de claves proporcionado por una de sus integrantes.

Las contraseñas y claves privadas deben mantenerse protegidas y no deben publicarse en GitHub.

### Verificación pendiente

Para cerrar este requisito se debe confirmar:

- Que existe el archivo APK release definitivo.
- Que la firma digital es válida.
- Que el APK puede instalarse.
- Que la aplicación funciona después de instalarla.

La existencia del almacén de claves no constituye por sí sola evidencia de que el APK final ya haya sido firmado.

---

## 14. Instalación y ejecución de la aplicación

### Requisitos

- Android Studio.
- Android SDK compatible con el proyecto.
- JDK compatible con la configuración de Gradle.
- Emulador Android o dispositivo físico compatible.
- Conexión de red para las consultas REST.
- Backend Spring Boot ejecutándose para utilizar la API local.

### Instrucciones

1. Clonar el repositorio:

```bash
git clone https://github.com/Polette2000/AppTurcoEspanol.git
```

2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar la sincronización de Gradle.
4. Ejecutar **Build → Make Project**.
5. Seleccionar un emulador o dispositivo Android.
6. Presionar **Run**.
7. Elegir el idioma inicial.
8. Configurar un avatar.
9. Seleccionar una categoría.
10. Explorar vocabulario, juegos, pronunciación y cámara.

Para probar las traducciones online, ejecutar previamente Spring Boot.

En un teléfono físico, la dirección del backend debe configurarse para utilizar una IP accesible desde ese dispositivo o un servidor desplegado.

---

## 15. Persistencia y privacidad

La aplicación utiliza DataStore Preferences para almacenar localmente el progreso educativo.

La información se relaciona con avatares ficticios y categorías de aprendizaje.

No se requiere registrar nombres reales de menores de edad.

La actividad de cámara está orientada a fotografiar objetos, juguetes o dibujos.

La vista previa de la fotografía es temporal y no se almacena como archivo permanente desde la funcionalidad descrita.

La cámara no realiza reconocimiento automático de objetos ni envía fotografías al backend.

Estas decisiones buscan reducir la recopilación innecesaria de información personal.

---

## 16. Alineación con los requisitos del EFT

La aplicación fue desarrollada considerando los siguientes requisitos técnicos de la evaluación final transversal.

| Requisito EFT | Implementación | Estado |
|---|---|---|
| Dos o más recursos nativos | Cámara y TextToSpeech | Comprobado |
| Consumo de API externa | API REST propia con Spring Boot | Comprobado |
| Pruebas unitarias | JUnit y lógica de negocio | 8 aprobadas |
| APK firmado release | Proceso de firma con almacén de claves | Pendiente de verificación final |
| Documentación técnica | README del proyecto | Documentado |
| Contexto definido | Aplicación educativa de español y turco | Definido; falta contrastar con pauta completa |

### Justificación del contexto

EsTurk aborda una necesidad educativa mediante una solución móvil adaptada al aprendizaje inicial de vocabulario.

Integra interacción visual, reproducción de pronunciación, actividades de práctica y seguimiento de avances.

Los recursos nativos permiten enriquecer la experiencia de aprendizaje y la API REST complementa las consultas de traducción.

Las pruebas unitarias ayudan a verificar el funcionamiento de la lógica de negocio.

La aprobación definitiva de todos los aspectos del EFT depende de la pauta oficial y de completar las verificaciones pendientes.

---

## 17. Gestión y colaboración del equipo

El proyecto utiliza Git y GitHub para el control de versiones.

Se trabaja mediante ramas de desarrollo y commits para identificar los cambios realizados.

Entre las ramas utilizadas se encuentran:

- `master`
- `dev-darling`
- `dev-ruth`
- `dev-polette`

Los cambios de las ramas individuales pueden incorporarse a la rama principal mediante Pull Requests y revisión de código.

### Herramientas colaborativas

- GitHub: repositorio y control de versiones.
- Trello: seguimiento y organización de tareas.
- Android Studio: desarrollo de la aplicación móvil.
- Visual Studio Code: desarrollo y ejecución de la API REST.

## 18. Equipo de desarrollo

**Integrantes:**

- Patience Agunanna.
- Ruth Honorio.
- Darling Pinol.

**Docente:** Luis Becerra.

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles.

**Institución:** Duoc UC.

### Enlaces del proyecto

**Repositorio GitHub:**

https://github.com/Polette2000/AppTurcoEspanol

**Tablero Trello:**

https://trello.com/b/XyAIiJD7/applicaci%C3%B3n

---

## 19. Evidencias de la entrega

Se recomienda organizar las evidencias en las siguientes categorías:

| Evidencia | Contenido |
|---|---|
| Evidencia 1 | TextToSpeech funcionando |
| Evidencia 2 | Cámara y vista previa |
| Evidencia 3 | Spring Boot ejecutándose |
| Evidencia 4 | Respuesta JSON desde Chrome |
| Evidencia 5 | Traducción online en Android |
| Evidencia 6 | Resultado de las 8 pruebas JUnit |
| Evidencia 7 | APK firmado e instalación |
| Evidencia 8 | Pantallas y recorrido de la aplicación |
| Evidencia 9 | Repositorio GitHub y colaboración |

Las evidencias deben corresponder a ejecuciones reales del proyecto.

## 20. Pendientes antes de la entrega final

- Verificar la versión release firmada y su instalación.
- Confirmar las pruebas unitarias sobre la rama definitiva `master`, si corresponde.
- Comprobar que las modificaciones de las ramas de desarrollo estén integradas en la versión a entregar.
- Documentar las pruebas funcionales restantes.
- Incorporar las capturas exigidas por la pauta.
- Revisar las condiciones específicas del EFT.
- Verificar las instrucciones y fecha límite de entrega.

---

**TurcoEspañol Kids — EsTurk**  
**Proyecto académico de Desarrollo de Aplicaciones Móviles**  
**Duoc UC — DSY1105**
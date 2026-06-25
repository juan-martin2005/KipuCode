### Desarrollo de una aplicación móvil de aprendizaje guiado en programación para fortalecer las habilidades técnicas de los estudiantes de la Facultad de Ingeniería de la Universidad Privada del Norte, Chorrillos, 2026.
---
# Capítulo I: Planteamiento del Problema

## 1. Descripción del Problema

Muchos estudiantes universitarios que empiezan en el mundo del desarrollo de software lo llegan a abandonar tempranamente debido a metodologías de enseñanza tradicionales que no se adaptan a su ritmo de aprendizaje. Esta falta de personalización y la ausencia de una guía práctica generan frustración y desmotivación, dificultando la adquisición de una base lógica sólida, fundamental para su desarrollo académico y profesional.

---

## 2. Planteamiento del Problema

### Pregunta Inicial

¿De qué manera la implementación de una aplicación móvil de aprendizaje guiado en programación influirá en el desarrollo de habilidades técnicas en estudiantes de la Facultad de Ingeniería de la Universidad Privada del Norte, sede Chorrillos, durante el año 2026?

---

## 3. Objetivos

### 3.1 Objetivo General

Implementar una aplicación móvil de aprendizaje guiado en programación orientada a la adquisición de fundamentos técnicos esenciales en estudiantes de la Facultad de Ingeniería de la Universidad Privada del Norte, sede Chorrillos, Lima, 2026.

---

### 3.2 Objetivos Específicos

1. Estructurar una arquitectura de software móvil basada en Clean Architecture compatible con el ecosistema moderno de Android.
    
2. Diseñar interfaces de usuario dinámicas y declarativas aplicando principios de diseño moderno orientados a la experiencia del usuario (UX).
    
3. Implementar un modelo de persistencia de datos mixto mediante una base de datos relacional local (Room/SQLite) para funcionamiento offline y una base de datos NoSQL en la nube (Firebase) para la sincronización y gestión de la progresión del usuario.
    
4. Integrar módulos de contenido interactivo teórico y práctico que permitan construir rutas de aprendizaje progresivas y validar el conocimiento adquirido por el estudiante.

---

## 4. Justificación

### 4.1 Justificación Técnica

El desarrollo del proyecto migró desde un enfoque inicial basado en interfaces XML y lógica desarrollada en Java hacia una arquitectura moderna basada en Kotlin y Jetpack Compose.

Este cambio permitirá:

- Desarrollo declarativo.    
- Reducción de código repetitivo.    
- Mayor mantenibilidad.    
- Mejor rendimiento y escalabilidad.

#### Bases de Datos

##### Persistencia Local (**SQLite** / Room)

- Almacenamiento ligero.    
- Funcionamiento sin conexión a Internet.    
- Persistencia de información del usuario.    

##### Persistencia en la Nube (**Firebase** / NoSQL)

- Autenticación de usuarios.    
- Almacenamiento remoto.    
- Sincronización de información.    
- Reducción de costos de infraestructura backend.    

#### Entorno de Desarrollo

##### **Android Studio**

|Requisito|Especificación|
|---|---|
|Sistema Operativo|Windows 10 (64 bits)|
|Memoria RAM|8 GB (16 GB recomendado para emulador)|
|Procesador|Intel Core i5 8va Gen o AMD Ryzen equivalente|
|Espacio en Disco|8 GB (16 GB recomendado con emulador)|
|GPU|Opcional / 4 GB VRAM para emulación avanzada|

##### **SQLite**

|Requisito|Especificación|
|---|---|
|Sistema Operativo|Windows, Linux, macOS, Android, iOS|
|RAM|Menor a 1 MB|
|Procesador|Arquitecturas de 32 y 64 bits|
|Espacio|Menor a 50 MB|

---

### 4.2 Justificación Social

El proyecto tiene un impacto positivo al brindar acceso a la educación tecnológica, permitiendo reducir las barreras iniciales en estudiantes de ingeniería y carreras afines mediante una herramienta accesible desde dispositivos móviles, alineándose activamente con el **Objetivo de Desarrollo Sostenible 4 (ODS 4): Educación de Calidad** de la Agenda 2030 de la ONU.

---

### 4.3 Justificación Financiera

La aplicación utiliza tecnologías gratuitas o de bajo costo durante sus primeras etapas de desarrollo.

#### Costos Estimados

| Concepto                                      |           Costo |
| --------------------------------------------- | --------------: |
| Desarrollo de Software (UI/UX y Lógica)       |     S/ 6,000.00 |
| Licencia Google Play Store (Opcional / No se desplegará en esta fase) |        S/ 90.00 |
| Infraestructura Inicial Firebase (Spark Plan) |         S/ 0.00 |
| **Costo Total Estimado**                      | **S/ 6,090.00** |

> **Nota:** La publicación en Google Play Store no forma parte del alcance actual del proyecto. Por otro lado, el plan Spark (gratuito) de Firebase cubre holgadamente la etapa inicial de pruebas y escalado.

#### Sostenibilidad

La sostenibilidad financiera se plantea mediante un modelo Freemium:

- Acceso gratuito al contenido principal.
- Monetización mediante anuncios.
- Posible incorporación de suscripciones premium al superar los 10,000 usuarios activos.

Los ingresos obtenidos permitirán:

- Cubrir costos de infraestructura.
- Escalar servicios Firebase.
- Financiar nuevas funcionalidades.
- Garantizar el mantenimiento continuo de la aplicación.

---
# Capítulo II: Marco Teórico y Metodología

## 1. Marco Teórico

### Mobile Learning

Metodología de enseñanza y aprendizaje basada en el uso de dispositivos móviles que permite acceder al contenido educativo desde cualquier lugar y momento.

### Kotlin

Lenguaje de programación de tipado estático recomendado oficialmente por Google para el desarrollo Android desde 2019.

Características principales:

- Null Safety.
- Kotlin Coroutines.
- Interoperabilidad con Java.

### Jetpack Compose

Framework declarativo para la construcción de interfaces nativas Android mediante Kotlin.

Beneficios:

- Menor cantidad de código.
- Mayor productividad.
- Mejor mantenibilidad.

### Clean Architecture y MVVM

#### **Clean Architecture**

Organiza el sistema en capas independientes:

- Domain
- Data
- UI
#### **MVVM**

Permite separar:

- Modelo (Model)
- Vista (View)
- Lógica de Presentación (ViewModel)

Beneficios:

- Escalabilidad.
- Mantenibilidad.
- Facilidad para pruebas.
### Room Database (SQLite)

Biblioteca oficial de persistencia para Android que abstrae SQLite y facilita el acceso seguro a la base de datos local.

### Firebase

Plataforma en la nube utilizada para:

- Firebase Authentication.
- Cloud Firestore.
- Sincronización de datos.

---

## 2. Antecedentes

**PENDIENTE**

---

## 3. Metodología de Desarrollo

Para el desarrollo de la aplicación se utilizará la metodología ágil Scrum adaptada a un entorno de desarrollo de **pocos colaboradores** (equipo reducido).

### Product Backlog

Lista priorizada de funcionalidades:

- Registro y Login de usuarios
- Módulos de contenido teórico (lecciones)
- Sistema de ejercicios interactivos
- Base de datos local (Offline-first)
- Sincronización con la nube (Firebase)

### Sprints

Ciclos iterativos de 1 a 2 semanas orientados a la entrega continua de incrementos funcionales del software.

### Herramientas de Gestión

- GitHub como plataforma principal para control de versiones Git y seguimiento de tareas.

## 4. Cronograma de actividades.

| Sprint                              | Actividades Principales                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Duración           |           Fechas            |
| ----------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ | :-------------------------: |
| **Sprint 1: Planificación y Base**  | • Levantamiento de requerimientos.<br><br>• Diseño UI/UX (Referencias de Pinterest).<br><br>• Configuración inicial del proyecto en Android Studio.                                                                                                                                                                                                                                                                                                                                         | **Semanas 1 - 2**  | **21/04/2026 - 04/05/2026** |
| **Sprint 2: Lógica y Persistencia** | • Diseño e implementación de la base de datos local con Room.<br><br>• Creación de entidades y modelos de datos (Usuarios, Cursos y Lecciones).<br><br>• Desarrollo de pantallas principales (Onboarding - Login - Register).<br><br>• Refactorización Java/XML -> Kotlin/Jetpack Compose.<br><br>• Desarrollo de pantallas base (Splash - Onboarding - Register - Login - ForgotPassword - Navbar - Home).<br><br>• Integración de Firebase Authentication.                              | **Semanas 3 - 5**  | **05/05/2026 - 25/05/2026** |
| **Sprint 3: Cloud y Funcionalidad** | • Integración de Cloud Firestore.<br><br>• Configuración de base de datos local y entidades para ejercicios, opciones de bloques y repetición espaciada.<br><br>• Sincronización offline-first robusta para progreso de usuario, lecciones y ejercicios (resolviendo dependencias de borrado en cascada y evitando pérdida de datos locales).<br><br>• Implementación de flujos de ejercicios prácticos interactivos (opciones múltiples, flashcards) y visualización de contenido teórico con markdown. | **Semanas 6 - 8**  | **26/05/2026 - 15/06/2026** |
| **Sprint 4: Pruebas y Despliegue**  | • Implementación del sistema de registro de experiencia (XP) por lección, rachas (Streak) de estudio diarias de usuario y lógica de reintentos en ejercicios.<br><br>• Optimización de la navegación general de pantallas y reorganización de componentes de la interfaz de usuario en Compose.<br><br>• Pruebas de usabilidad con usuarios beta y resolución de bugs (ej. permisos de red, consistencia de datos).<br><br>• Generación del APK final del aplicativo.                       | **Semanas 9 - 10** | **16/06/2026 - 27/06/2026** |

---
# Capítulo III

## 1. Requerimientos

### 1.1 Requerimientos Funcionales

Los requerimientos funcionales describen las funcionalidades que el sistema debe proporcionar para satisfacer las necesidades de los usuarios.

| Código   | Requerimiento                                                                                                                                                        |
| -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RF01** | El sistema debe permitir el registro e inicio de sesión de los estudiantes mediante Firebase Authentication, sincronizando la información básica del usuario.        |
| **RF02** | El sistema debe mostrar la lista de cursos disponibles, incluyendo título, descripción y estado de avance.                                                           |
| **RF03** | El sistema debe permitir la navegación entre cursos y lecciones, mostrando contenido teórico y ejercicios prácticos organizados secuencialmente.                     |
| **RF04** | El sistema debe registrar el progreso del estudiante, actualizando estados de completado, experiencia acumulada (XP) y rachas de estudio (Streak).                   |
| **RF05** | El sistema debe funcionar bajo un enfoque Offline First, almacenando cambios localmente y sincronizándolos con Firebase Firestore cuando exista conexión a Internet. |

---

### 1.2 Requerimientos No Funcionales

Los requerimientos no funcionales definen las características de calidad, rendimiento y restricciones técnicas del sistema.

| Código    | Requerimiento                                                                                                                                            |
| --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RNF01** | La interfaz de usuario debe estar desarrollada con Jetpack Compose, garantizando una experiencia moderna, accesible y responsiva.                        |
| **RNF02** | La aplicación implementará los principios de Clean Architecture para asegurar mantenibilidad, escalabilidad y separación de responsabilidades.           |
| **RNF03** | Las operaciones de base de datos y red deben ejecutarse de manera asíncrona utilizando Kotlin Coroutines y Flow, evitando bloqueos en el hilo principal. |
| **RNF04** | La persistencia local debe optimizar el almacenamiento mediante SQLite y Room, minimizando el consumo de recursos del dispositivo.                       |

---

## 2. Metodología de Desarrollo

Para el desarrollo de KipuCode se adoptó una metodología ágil inspirada en Scrum, adaptada a un entorno de desarrollo de **pocos colaboradores**. 
El proceso se dividió en las siguientes fases:
### 2.1 Diseño de Interfaces
- Elaboración de prototipos y referencias visuales.
- Definición de componentes reutilizables.
- Creación del sistema de diseño utilizando:
    - Colores
    - Tipografías
    - Espaciados
    - Componentes Compose
### 2.2 Modelado del Dominio
Definición de entidades independientes:
- `UserDomain`
- `CourseDomain` (y `CourseWithLessonsDomain`)
- `LessonDomain`
- `ExerciseDomain` (y `BlockOptionDomain`)
- `UserProgressDomain`
- `SyncQueue`

Estas entidades representan las reglas de negocio fundamentales de la aplicación.

### 2.3 Persistencia de Datos
Implementación de la capa de datos mediante:
- Room Database
- DAO (Data Access Objects)
- Mappers entre entidades locales y modelos de dominio
- Repositorios para abstraer el origen de los datos

### 2.4 Gestión de Estado
Implementación del patrón MVVM mediante:
- ViewModels
- StateFlow

Los ViewModels gestionan la lógica de presentación y exponen estados reactivos consumidos por la interfaz de usuario.

---
## 3. Diagrama de Clases (Arquitectura de Software)

La aplicación sigue una arquitectura multicapa basada en Clean Architecture e Inyección de Dependencias.

### Capa de Dominio (Domain Layer)

#### Modelos
- `UserDomain`
- `CourseDomain`
- `CourseWithLessonsDomain`
- `LessonDomain`
- `ExerciseDomain`
- `BlockOptionDomain`
- `UserProgressDomain`
- `SyncQueue`
- `Response` (Utilidad para estados de carga/éxito/error)

#### Casos de Uso
Cada clase de caso de uso encapsula responsabilidades específicas del negocio:
- `CourseUseCase` (Carga y listado de cursos)
- `LessonUseCase` (Gestión de lecciones y contenido markdown)
- `ExerciseUseCase` (Obtención de ejercicios y procesamiento de respuestas)
- `AuthUseCases` (Flujos de autenticación e inicio de sesión)
- `UserUseCase` (Estadísticas del usuario: XP, rachas, nivel)

#### Contratos de Repositorio
Definen las operaciones disponibles sin conocer su implementación física:
- `AuthRepository`
- `UserRepository`
- `CourseRepository`
- `LessonRepository`
- `ExerciseRepository`
- `BlockOptionRepository`
- `UserProgressRepository`
- `SyncQueueRepository`

---

### Capa de Datos (Data Layer)

#### Base de Datos Local
- `AppDatabase`
- `UserEntity`
- `CourseEntity`
- `LessonEntity`
- `ExerciseEntity`
- `BlockOptionEntity`
- `UserProgressEntity`
- `FlashcardProgressEntity`
- `SyncQueueEntity`

#### DAOs (Data Access Objects)
- `UserDao`
- `CourseDao`
- `LessonDao`
- `ExerciseDao`
- `BlockOptionDao`
- `UserProgressDao`
- `SyncQueueDao`

#### Fuentes de Datos Remotas
- `AuthRemoteDataSource` (Firebase Authentication)
- `UserRemoteDataSource` (Cloud Firestore)
- `CourseRemoteDataSource` (Cloud Firestore)
- `LessonRemoteDataSource` (Cloud Firestore)
- `ExerciseRemoteDataSource` (Cloud Firestore)

#### Repositorios
- `AuthRepositoryImpl`
- `UserRepositoryImpl`
- `CourseRepositoryImpl`
- `LessonRepositoryImpl`
- `ExerciseRepositoryImpl`
- `UserProgressRepositoryImpl`

#### Mappers
Responsables de transformar:
- `UserMapper` (UserEntity / UserDto ↔ UserDomain)
- `CourseMapper` (CourseEntity / CourseDto ↔ CourseDomain)
- `LessonMapper` (LessonEntity / LessonDto ↔ LessonDomain)
- `ExerciseMapper` (ExerciseEntity / ExerciseDto / BlockOptionEntity ↔ ExerciseDomain / BlockOptionDomain)
- `UserProgressMapper` (UserProgressEntity / UserProgressDto ↔ UserProgressDomain)

---

### Capa de Presentación (UI Layer)

#### ViewModels
- `AuthViewModel` (Flujos de acceso y recuperación)
- `UserViewModel` (Estado del perfil del usuario, rachas y XP)
- `CoursesViewModel` (Estado y listado de cursos disponibles)
- `LessonViewModel` (Estado de la lección seleccionada)
- `ExerciseViewModel` (Manejo del flujo de ejercicios interactivos y reintentos)

#### Pantallas
- `SplashScreen` (Carga inicial)
- `OnboardingScreen` (Guía de introducción)
- `LoginScreen` (Inicio de sesión)
- `RegisterScreen` (Registro de cuenta)
- `ForgotPassword` (Recuperación de credenciales)
- `HomeScreen` (Progreso del estudiante)
- `ExploreScreen` (Exploración de temarios)
- `ProfileScreen` (Visualización de XP, nivel y rachas)
- `FeedbackScreen` (Envío de comentarios)
- `LessonScreen` (Contenido teórico formateado con markdown)
- `CodeScreen` (Visualización interactiva de lecciones de programación)
- `ExerciseScreen` (Resolución de dinámicas prácticas)

#### Componentes Reutilizables
- `Navbar` (Navegación inferior del sistema)
- `Buttons` (Botón principal interactivo de la app)
- `KipuForm` y `KipucodeText` (Campos de entrada dinámicos)
- `UserProfileCard` (Resumen del perfil de usuario)
- `HomeCard`, `LessonsCard`, `HeadlineHome` (Estructuras de las lecciones y cursos en la pantalla principal)
- `MultipleChoicesCard`, `CustomeDialog` (Componentes interactivos de selección y diálogos)
- `Choices`, `FlashCard`, `OptionsCard`, `ContentMarkdown` (Componentes especializados en los ejercicios y renderizado de lecciones)

---

## 4. Diseño de la Base de Datos

La persistencia local se implementó mediante Room Database, utilizando SQLite.

### Entidades Principales

|Tabla|Descripción|
|---|---|
|`users`|Información general del estudiante (incluye XP y rachas).|
|`courses`|Cursos disponibles dentro de la plataforma.|
|`lessons`|Lecciones pertenecientes a cada curso.|
|`exercises`|Ejercicios teóricos/prácticos de cada lección.|
|`block_options`|Opciones de bloques de código y respuestas para ejercicios.|
|`user_progress`|Registro de avance del usuario en cada lección y ejercicio.|
|`flashcard_progress`|Seguimiento del progreso de repetición espaciada en flashcards.|
|`sync_queue`|Cola de sincronización para operaciones offline-first pendientes.|

### Relaciones

```text
User (1) ──────── (N) UserProgress

Course (1) ────── (N) Lesson

Lesson (1) ────── (N) Exercise

Exercise (1) ──── (N) BlockOption
```

El diseño permite mantener la información disponible localmente y sincronizarla posteriormente con Firebase.

---

## 5. Entorno

| Tecnología                | Descripción              |
| ------------------------- | ------------------------ |
| Lenguaje                  | Kotlin                   |
| IDE                       | Android Studio           |
| Framework UI              | Jetpack Compose          |
| Renderizado de Markdown   | `multiplatform-markdown-renderer` (con coloreado de sintaxis para lecciones) |
| Base de Datos Local       | Room Database (SQLite)   |
| Base de Datos Remota      | Cloud Firestore (NoSQL)  |
| Autenticación             | Firebase Authentication  |
| Arquitectura Asíncrona    | Kotlin Coroutines y Flow |
| Inyección de Dependencias | Hilt                     |
| Control de Versiones      | Git y GitHub             |

---

## 6. Codificación

Durante el desarrollo se siguieron las recomendaciones oficiales de Google para Android.

### Inmutabilidad

Se prioriza el uso de:

```kotlin
val
data class
```

con el fin de reducir efectos secundarios y mejorar la previsibilidad del código.

### Single Source of Truth (SSOT)

Room actúa como fuente principal de datos, mientras que la interfaz observa los cambios mediante Flow y StateFlow.

### Separación de Responsabilidades

Los componentes de interfaz son principalmente Stateless y reciben:

- Datos
- Eventos
- Callbacks

desde sus respectivos ViewModels.

### Manejo de Errores

La aplicación utiliza clases de respuesta controladas para representar los distintos estados del sistema:

```text
Loading
Success
Error
```

Esto permite mostrar mensajes adecuados al usuario sin comprometer la estabilidad de la aplicación.

# MiInventario

**Aplicación multiplataforma para organizar productos de aseo y cocina del hogar.** Permite registrar cantidades disponibles y un mínimo de reposición. Está desarrollada con Kotlin Multiplatform, Compose Multiplatform y SQLDelight, con SQLite local para Android y Desktop JVM. Funciona sin conexión a internet durante su uso: no tiene servidor, autenticación ni sincronización entre dispositivos.

> **Estado actual:** RF1 (registro de productos) está implementado. El equipo ha comprobado manualmente el registro y la persistencia en Desktop; el APK Android compila, pero aquí no se afirma que la persistencia Android haya sido verificada en un dispositivo. RF2–RF7 siguen pendientes. La pantalla Inicio aún muestra el estado vacío y no consulta el repositorio para listar productos.

## Inicio rápido

Desde la raíz del repositorio, con JDK y Android SDK configurados:

```bash
./scripts/dev.sh
```

El menú permite ejecutar pruebas, verificar las compilaciones, generar el APK, iniciar Desktop o instalar el APK en un dispositivo Android. También se pueden usar directamente las tareas Gradle de la sección [Ejecución y pruebas](#ejecución-y-pruebas). La primera compilación puede necesitar internet para descargar dependencias; la aplicación no lo necesita para guardar productos.

## Tecnologías y módulos

- **Kotlin Multiplatform (KMP):** comparte modelo, validaciones, acceso a datos e interfaz.
- **Compose Multiplatform / Material 3:** interfaz común para Android y Desktop.
- **SQLDelight 2.3.2 y SQLite:** generación de API Kotlin desde SQL y almacenamiento local.
- **Gradle con Kotlin DSL y Gradle Wrapper:** configuración y compilación reproducible.
- **Git y GitHub:** colaboración mediante ramas y Pull Requests.

Las versiones exactas están en `gradle/libs.versions.toml` y `gradle/wrapper/gradle-wrapper.properties`. El proyecto configura una toolchain Java 21 en `gradle/gradle-daemon-jvm.properties`. No es necesario instalar Gradle globalmente ni cambiar las versiones por cuenta propia.

### Estructura real

```text
MiInventario/
├── androidApp/
│   └── src/main/kotlin/co/edu/poli/miinventario/MainActivity.kt
├── desktopApp/
│   └── src/main/kotlin/co/edu/poli/miinventario/main.kt
├── shared/
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/co/edu/poli/miinventario/
│       │   │   ├── App.kt
│       │   │   ├── product/             # Product, validación y ProductRepository
│       │   │   ├── data/                # SqlDelightProductRepository
│       │   │   └── ui/                  # pantallas, estado del formulario y tema
│       │   └── sqldelight/co/edu/poli/miinventario/database/Product.sq
│       ├── androidMain/kotlin/co/edu/poli/miinventario/data/
│       │   └── AndroidDatabaseFactory.kt
│       ├── jvmMain/kotlin/co/edu/poli/miinventario/data/
│       │   └── DesktopDatabaseFactory.kt
│       ├── commonTest/                  # pruebas de validación de productos
│       ├── jvmTest/                     # pruebas host JVM existentes
│       └── androidHostTest/             # pruebas host Android existentes
├── scripts/dev.sh
├── gradle/libs.versions.toml
├── gradlew
├── gradlew.bat
└── README.md
```

- `androidApp` configura el APK. `MainActivity` crea el repositorio con la fábrica Android y llama a `App(repository)`.
- `desktopApp` contiene la ventana JVM. Su función `main` crea el repositorio con la fábrica Desktop y llama a la misma interfaz `App(repository)`.
- `shared/src/commonMain` contiene el modelo `Product`, sus reglas, la interfaz `ProductRepository`, su implementación SQLDelight y las pantallas Compose.
- `shared/src/androidMain` y `shared/src/jvmMain` solo contienen la creación de la base según la plataforma.
- `Product.sq` define la tabla y las consultas; Gradle/SQLDelight generan la API Kotlin durante la compilación.

Esta es una **separación práctica de responsabilidades**, no una implementación completa de Clean Architecture, MVVM o DDD. `App.kt` conecta Inicio y Registro mediante estado Compose y callbacks; las pantallas no ejecutan SQL directamente.

### Flujo de componentes

```mermaid
flowchart LR
    Android["Android<br/>androidApp"] --> Shared["Código compartido<br/>shared/src/commonMain"]
    Desktop["Desktop JVM<br/>desktopApp"] --> Shared
    Shared --> Repository["ProductRepository<br/>SqlDelightProductRepository"]
    Repository --> SQLDelight["SQLDelight<br/>Product.sq + API generada"]
    SQLDelight --> AndroidSQLite["SQLite local Android<br/>almacenamiento privado"]
    SQLDelight --> DesktopSQLite["SQLite local Desktop<br/>~/.miinventario/miinventario.db"]
```

Las dos bases son independientes. No existe sincronización Android–Desktop.

## Datos y persistencia

`Product.sq` define una tabla `product` con `id` de texto como clave primaria, nombre, categoría, cantidad disponible, unidad y cantidad mínima. También define `insertProduct` y `selectAllProducts`; esta última consulta ordena por nombre. SQLDelight genera `InventoryDatabase` y `productQueries` a partir de ese archivo. **No se deben añadir manualmente a Git los archivos generados** dentro de `build/`.

`Product` genera un identificador UUID al construirse. `SqlDelightProductRepository.save` inserta los campos mediante la consulta generada. `getAll` recupera filas y las convierte nuevamente a `Product`. La categoría y la unidad se guardan por el nombre de sus enumeraciones. El esquema no impone unicidad al nombre: pueden existir productos distintos con el mismo nombre.

En Android, `AndroidDatabaseFactory` usa `AndroidSqliteDriver`, el esquema generado y el `Context` de la aplicación para crear o abrir `miinventario.db` en el almacenamiento privado de esa aplicación. En Desktop, `DesktopDatabaseFactory` crea `~/.miinventario/` si hace falta y abre `~/.miinventario/miinventario.db` mediante el driver SQLite para JVM.

**Versión del esquema:** en Desktop se lee `PRAGMA user_version`. Si vale `0`, se crea el esquema y se escribe la versión generada por SQLDelight; si coincide, se abre la base; si es distinta, se informa un error. En Android la apertura/creación se delega al driver con `InventoryDatabase.Schema`. No hay archivos de migración `.sqm` ni un sistema completo de migraciones definido todavía: un cambio futuro de esquema requiere planificar cómo conservar las bases existentes.

## Requerimientos funcionales

| Código | Alcance | Estado actual |
|---|---|---|
| **RF1** | Registrar nombre, categoría, cantidad, unidad y mínimo. | **Implementado.** Formulario, validación, escritura local, confirmación, limpieza tras guardar y deshabilitación temporal de controles durante la operación. Registro y persistencia comprobados manualmente en Desktop por el equipo. Persistencia Android pendiente de comprobar en un dispositivo. |
| **RF2** | Consultar/listar productos registrados. | **Pendiente en la interfaz.** El repositorio ya ofrece `getAll()`, pero Inicio no lo usa ni muestra registros. |
| **RF3** | Filtrar por categoría Aseo o Cocina. | Pendiente. |
| **RF4** | Editar datos de un producto. | Pendiente. |
| **RF5** | Eliminar con confirmación previa. | Pendiente. |
| **RF6** | Actualizar la cantidad disponible. | Pendiente. |
| **RF7** | Consultar productos con cantidad igual o inferior al mínimo. | Pendiente. El mínimo ya se registra en RF1. |

Las cinco pantallas previstas son **Inicio / Inventario**, **Registrar producto**, **Detalle del producto**, **Editar producto** y **Productos por reponer**. Hoy existen Inicio y Registro; las demás están pendientes. Al guardar desde Registro, `RegisterProductScreen` usa el producto normalizado por `ProductValidator`, llama al repositorio, muestra una confirmación y limpia el formulario. Si falla la operación, muestra un error. La pantalla Inicio todavía presenta un estado vacío fijo, incluso si ya hay registros; mostrar el listado corresponde a RF2.

Las reglas actuales de RF1 rechazan nombre en blanco, cantidades negativas o no finitas y fracciones para `und`. Admiten `0` tanto para la cantidad como para el mínimo y decimales para `kg`, `lb` y `L`. No se exige un nombre único.

## Preparar el entorno

Se recomienda Android Studio o IntelliJ IDEA con soporte para el proyecto, un JDK compatible con la toolchain Java 21 configurada, Git y Android SDK con sus licencias aceptadas. Para instalar un APK en un dispositivo físico se necesitan Android SDK Platform-Tools (`adb`), depuración USB y un dispositivo autorizado. Para usar `scripts/dev.sh` se necesita Bash; en Windows puede emplearse Git Bash.

- **Windows:** configurar Android Studio, el SDK y Git. El emulador puede necesitar Windows Hypervisor Platform; Git Bash permite usar el script del proyecto.
- **Linux:** configurar Android Studio y el SDK; en Ubuntu/Debian se puede instalar Git con `sudo apt update` y `sudo apt install git`. La aceleración del emulador suele usar KVM.
- **macOS:** configurar Android Studio, el SDK y Git. Para ejecutar Desktop, usar un JDK compatible con la arquitectura del equipo.

El JDK integrado en Android Studio puede servir si coincide con la configuración del proyecto. Se necesita internet para obtener dependencias en la primera sincronización.

Clonar mediante HTTPS (método sencillo para quien no usa claves SSH):

```bash
git clone https://github.com/DJAngel973/MiInventario.git
cd MiInventario
```

Si ya se configuró una clave pública en GitHub, también se puede usar SSH:

```bash
git clone git@github.com:DJAngel973/MiInventario.git
cd MiInventario
```

Véase [Conectarse a GitHub mediante SSH](https://docs.github.com/es/authentication/connecting-to-github-with-ssh). Abrir la carpeta raíz en Android Studio o IntelliJ IDEA, esperar la sincronización de Gradle y comprobar que se reconocen `androidApp`, `desktopApp` y `shared`. Android Studio permite revisar el SDK en **Settings → Languages & Frameworks → Android SDK**.

Para ejecutar Android, usar un teléfono físico o crear e iniciar un dispositivo virtual en **Tools → Device Manager**; seleccionar una imagen de sistema compatible. El emulador requiere recursos adicionales y aceleración disponible (por ejemplo, WHPX en Windows o KVM en Linux). **No hace falta configurar KVM ni ejecutar un emulador para compilar el APK.** Desktop permite trabajar con la interfaz compartida, pero no sustituye una prueba final en Android.

## Ejecución y pruebas

Ejecutar desde la raíz del proyecto. En Linux/macOS se usa `./gradlew`; en PowerShell de Windows, `.\gradlew.bat`.

| Acción | Linux/macOS | Windows PowerShell |
|---|---|---|
| Pruebas JVM compartidas | `./gradlew :shared:jvmTest` | `.\gradlew.bat :shared:jvmTest` |
| Pruebas host Android existentes | `./gradlew :shared:testAndroidHostTest` | `.\gradlew.bat :shared:testAndroidHostTest` |
| Generar APK debug | `./gradlew :androidApp:assembleDebug` | `.\gradlew.bat :androidApp:assembleDebug` |
| Compilar Desktop | `./gradlew :desktopApp:compileKotlin` | `.\gradlew.bat :desktopApp:compileKotlin` |
| Ejecutar Desktop | `./gradlew :desktopApp:run` | `.\gradlew.bat :desktopApp:run` |

La tarea `:desktopApp:run` permanece activa mientras la ventana está abierta; eso no indica un bloqueo de compilación. `commonTest` contiene pruebas de validación de productos; `jvmTest` y `androidHostTest` aún incluyen comprobaciones básicas de plantilla. No hay pruebas automatizadas de persistencia ni una verificación instrumental en dispositivo Android documentadas aquí.

El APK de depuración generado se encuentra en `androidApp/build/outputs/apk/debug/androidApp-debug.apk`. Sirve para desarrollo, no equivale a una versión de producción firmada para publicación. Para instalarlo en un dispositivo autorizado:

```bash
adb devices
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

`adb devices` debe mostrar el dispositivo con estado `device`. La opción `-r` reinstala la aplicación; **normalmente conserva los datos** si el identificador de aplicación y la firma siguen siendo compatibles. No se debe asumir que los conservará tras una desinstalación, cambio de firma o limpieza de datos.

## Script de desarrollo

La forma recomendada de acceder al menú es:

```bash
./scripts/dev.sh
```

En Windows se necesita Git Bash u otro entorno Bash compatible. El script detecta `OSTYPE` y, en Git Bash/Cygwin, intenta usar `gradlew.bat`. Las opciones implementadas en `scripts/dev.sh` son:

| Opción | Acción real |
|---|---|
| **1** | Ejecuta `:shared:jvmTest`. |
| **2** | Verificación general: `:shared:jvmTest`, `:androidApp:assembleDebug` y `:desktopApp:compileKotlin`. |
| **3** | Genera el APK debug y comprueba que el archivo existe. |
| **4** | Inicia `:desktopApp:run`. El menú espera mientras la ventana esté abierta. |
| **5** | Comprueba que `adb` exista y que haya exactamente un dispositivo autorizado; genera el APK actualizado y ejecuta `adb install -r`. |
| **0** | Sale del menú. |

La opción 2 **no** ejecuta `:shared:testAndroidHostTest`, pruebas instrumentadas Android ni pruebas manuales de la interfaz o de persistencia. Estas comprobaciones deben hacerse aparte cuando correspondan.

## Colaboración y seguridad

El equipo usa Git y GitHub. Flujo recomendado: actualizar la rama principal, crear una rama para el requerimiento asignado, implementar, ejecutar pruebas, realizar commits descriptivos y proponer un Pull Request. Se recomienda no trabajar directamente sobre la rama principal ni editar simultáneamente los mismos archivos sin coordinación. Cada integrante necesita permisos adecuados para publicar en el repositorio compartido.

Por ejemplo, para comenzar una funcionalidad en una rama propia: `git switch -c feature/registro-productos`.

Para continuar desarrollando:

- Mantener en `shared/src/commonMain` la lógica que no depende de Android o Desktop; colocar el código específico en `shared/src/androidMain` o `shared/src/jvmMain`.
- Respetar los paquetes actuales: `product` para modelo/contrato/validación, `data` para persistencia y `ui` para pantallas, estado de formulario y tema.
- Acceder a productos mediante `ProductRepository`; las pantallas no deberían ejecutar SQL.
- Crear una rama por funcionalidad y ejecutar las pruebas pertinentes antes de proponer cambios.
- Al modificar `Product.sq`, revisar el impacto sobre datos locales existentes y definir una migración antes de distribuir una versión incompatible.

No publicar directorios `build/` o `.gradle/`, bases de datos locales, `local.properties`, configuraciones personales del IDE, contraseñas, tokens ni claves privadas. Revisar `.gitignore` antes de publicar. Sí deben permanecer versionados el Gradle Wrapper, los scripts y el catálogo de versiones, necesarios para reproducir la compilación.

## Referencias

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [SQLDelight](https://sqldelight.github.io/sqldelight/)
- [Android Studio](https://developer.android.com/studio)
- [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- [Git](https://git-scm.com/doc)

**Nota:** MiInventario es un proyecto académico en desarrollo. Los requerimientos pendientes describen el alcance previsto, no funcionalidades disponibles.

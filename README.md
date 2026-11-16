# MiInventario

**Aplicación multiplataforma para el control de productos de aseo y cocina del hogar**

## 1. Descripción

MiInventario es una aplicación orientada a la organización de productos domésticos de aseo y cocina. Su objetivo es permitir que los usuarios registren sus productos, consulten las cantidades disponibles e identifiquen aquellos que necesitan reposición.

El proyecto utiliza **Kotlin Multiplatform (KMP)** para compartir lógica de negocio y **Compose Multiplatform** para desarrollar interfaces reutilizables.

La aplicación está diseñada para funcionar sin conexión permanente a internet y almacenar los datos localmente.

**Estado actual:** proyecto base creado. Se ha verificado la ejecución de la aplicación de ejemplo en Desktop y la compilación inicial del APK Android. Las funcionalidades de inventario están pendientes de implementación.

## 2. Tecnologías

- Kotlin y Kotlin Multiplatform.
- Compose Multiplatform.
- Gradle con Kotlin DSL.
- Android SDK.
- Android Studio como entorno de desarrollo recomendado.
- Git y GitHub para control de versiones y colaboración.
- Persistencia local: pendiente de seleccionar e implementar.

## 3. Requerimientos funcionales

| Código | Funcionalidad |
|---|---|
| RF1 | Registrar productos con nombre, categoría, cantidad, unidad de medida y cantidad mínima. |
| RF2 | Consultar los productos registrados. |
| RF3 | Filtrar productos por categoría: aseo o cocina. |
| RF4 | Modificar los datos de un producto. |
| RF5 | Eliminar productos con confirmación previa. |
| RF6 | Actualizar la cantidad disponible de un producto. |
| RF7 | Consultar productos cuya cantidad sea igual o inferior al mínimo configurado. |

## 4. Pantallas previstas

1. **Inicio / Inventario:** listado de productos y filtros por categoría.
2. **Registrar producto:** formulario para agregar productos.
3. **Detalle del producto:** consulta, actualización de cantidad y eliminación.
4. **Editar producto:** modificación de datos registrados.
5. **Productos por reponer:** listado de artículos con existencias iguales o inferiores al mínimo.

## 5. Estructura del proyecto

```text
MiInventario/
├── androidApp/
├── desktopApp/
├── shared/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

- `androidApp/`: configuración y punto de entrada de Android.
- `desktopApp/`: aplicación ejecutable en escritorio.
- `shared/`: código compartido de Kotlin Multiplatform y Compose.
- `gradle/`: configuración y herramientas del Gradle Wrapper.

La estructura interna podrá ampliarse durante la implementación.

## 6. Requisitos para desarrollar

Se recomienda contar con:

- Windows, Linux o macOS compatible con Android Studio.
- Android Studio estable con soporte para Kotlin Multiplatform.
- Android SDK y sus licencias aceptadas.
- JDK compatible con la versión de Gradle y Android Gradle Plugin utilizada por el proyecto.
- Git.
- Conexión a internet para descargar las dependencias iniciales.

**Importante:** las versiones exactas de Gradle, Kotlin y Android Gradle Plugin están definidas en los archivos del repositorio. No es necesario instalar Gradle globalmente ni actualizar sus versiones por cuenta propia.

Se recomienda utilizar el JDK integrado en Android Studio para Gradle, siempre que sea compatible con la configuración del proyecto.

## 7. Configuración del entorno

### 7.1. Windows

1. Descargar e instalar Android Studio desde https://developer.android.com/studio.
2. Abrir Android Studio y completar su asistente de configuración.
3. Instalar los componentes del Android SDK solicitados por el proyecto.
4. Comprobar en **Settings → Languages & Frameworks → Android SDK** que el SDK esté configurado.
5. Instalar Git desde https://git-scm.com/downloads.
6. Clonar el repositorio siguiendo la sección 8.
7. Abrir la carpeta raíz `MiInventario` desde Android Studio.
8. Esperar a que finalice Gradle Sync.

Para ejecutar el emulador Android se requiere virtualización y aceleración compatibles con Windows. No es obligatorio utilizar un emulador para compilar el APK.

### 7.2. Linux (Ubuntu y distribuciones compatibles)

1. Instalar Android Studio desde https://developer.android.com/studio.
2. Configurar el Android SDK desde Android Studio.
3. Instalar Git utilizando el gestor de paquetes de la distribución.
4. Clonar el repositorio.
5. Abrir la carpeta `MiInventario` en Android Studio.
6. Esperar a que Gradle descargue las dependencias.

Para Ubuntu o Debian, Git puede instalarse mediante:

```bash
sudo apt update
sudo apt install git
```

El emulador Android acelerado en Linux normalmente utiliza KVM. Si KVM no está disponible, se puede desarrollar utilizando Compose Desktop y compilar el APK sin ejecutar un emulador.

No es necesario modificar KVM para compilar la aplicación.

### 7.3. macOS

1. Instalar Android Studio desde https://developer.android.com/studio.
2. Completar la configuración del Android SDK.
3. Instalar Git si no está disponible.
4. Clonar el repositorio.
5. Abrir la carpeta `MiInventario` desde Android Studio.
6. Esperar a que finalice Gradle Sync.

La ejecución de Desktop depende de disponer de un JDK compatible con la arquitectura del equipo. La ejecución Android requiere un dispositivo físico o un emulador compatible.

## 8. Clonar el repositorio


### 8.1. Clonar mediante HTTPS (recomendado)

Este método es el más sencillo para quienes todavía no han configurado SSH en GitHub.

En Windows, Linux o macOS, abrir una terminal y ejecutar:

```bash
git clone https://github.com/DJAngel973/MiInventario.git
cd MiInventario
```

### 8.2. Clonar mediante SSH (opcional)

Si ya se tiene configurada una clave SSH en GitHub, se puede utilizar:

```bash
git clone git@github.com:DJAngel973/MiInventario.git
cd MiInventario
```

Para este método es necesario haber registrado previamente la clave pública SSH en la cuenta de GitHub.

Documentación oficial: [Conectarse a GitHub mediante SSH](https://docs.github.com/es/authentication/connecting-to-github-with-ssh).

### 8.3. Abrir el proyecto en Android Studio

Después de clonar el repositorio:

1. Abrir Android Studio.
2. Seleccionar **File → Open**.
3. Seleccionar la carpeta raíz `MiInventario`.
4. Esperar a que finalice la sincronización de Gradle (Gradle Sync).
5. Comprobar que Android Studio reconoce los módulos `androidApp`, `desktopApp` y `shared`.
6. Verificar que Android SDK esté configurado correctamente.

El proyecto incluye Gradle Wrapper, por lo que no es necesario instalar Gradle globalmente.

Para ejecutar la aplicación en Android, continuar con la sección de configuración del emulador. Para ejecutar Desktop o generar el APK, consultar las secciones correspondientes.

### 8.4. Colaboración y seguridad

Clonar el repositorio permite consultar y modificar el código localmente. Para publicar cambios directamente en el repositorio compartido, cada integrante deberá contar con los permisos correspondientes en GitHub.

Se recomienda trabajar mediante ramas independientes y Pull Requests.

No se deben publicar contraseñas, tokens, claves privadas, archivos `local.properties` ni configuraciones personales del entorno de desarrollo.

## 9. Configurar y ejecutar el emulador Android

El proyecto puede ejecutarse en un dispositivo Android físico o en un dispositivo virtual creado mediante Android Studio.

### 9.1. Crear un dispositivo virtual

1. Abrir el proyecto MiInventario en Android Studio.
2. Esperar a que finalice la sincronización de Gradle.
3. Abrir **Tools → Device Manager**.
4. Seleccionar **Create Virtual Device** o el botón para agregar un dispositivo.
5. Elegir un perfil de teléfono, por ejemplo, un dispositivo Pixel.
6. Seleccionar una imagen de sistema Android compatible con el proyecto y con la arquitectura del computador.
7. Descargar la imagen si todavía no está instalada.
8. Finalizar la creación del dispositivo virtual.
9. Iniciar el emulador desde Device Manager.

**Requisitos importantes:**

- La aceleración del emulador depende del sistema operativo y de la virtualización disponible.
- Windows normalmente utiliza Windows Hypervisor Platform (WHPX).
- Linux utiliza KVM para la aceleración.
- macOS utiliza la aceleración compatible con su arquitectura.
- El emulador necesita memoria RAM y espacio de almacenamiento adicionales.

La instalación de Android Studio no garantiza que el emulador funcione automáticamente en todos los computadores.

### 9.2. Ejecutar MiInventario en el emulador

1. Abrir MiInventario en Android Studio.
2. Seleccionar el dispositivo virtual iniciado en la barra superior.
3. Seleccionar la configuración de ejecución `androidApp`.
4. Pulsar **Run** (triángulo verde).
5. Esperar a que Android Studio compile, instale y ejecute la aplicación.

Si no aparece la configuración `androidApp`, verificar que Gradle Sync haya terminado y que el módulo Android se haya importado correctamente.

### 9.3. Comprobar que Android detecta el emulador

Desde una terminal con Android SDK Platform-Tools configurado:

```bash
adb devices
```

Un emulador iniciado correctamente debería aparecer en la lista de dispositivos conectados con estado `device`.

### 9.4. Alternativa sin emulador

Cuando no sea posible utilizar Android Emulator, se puede ejecutar la interfaz compartida en Desktop:

**Linux y macOS:**

```bash
./gradlew :desktopApp:run
```

**Windows (PowerShell):**

```powershell
.\gradlew.bat :desktopApp:run
```

Desktop facilita el desarrollo y las pruebas del código compartido, pero no reemplaza la validación final en Android.

Para probar el APK también puede utilizarse un teléfono Android físico compatible.

## 10. Generar el APK Android

En Linux o macOS:

```bash
./gradlew :androidApp:assembleDebug
```

En Windows:

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

Si la compilación termina correctamente, el APK de depuración se encontrará normalmente en:

```text
androidApp/build/outputs/apk/debug/
```

El APK de depuración sirve para pruebas y entregas de desarrollo. No equivale a una versión de producción firmada para distribución pública.

Para ejecutarlo será necesario un dispositivo Android compatible o un emulador configurado.

## 11. Ejecutar las pruebas

La plantilla inicial incluye tareas de pruebas para el código compartido.

En Linux o macOS:

```bash
./gradlew :shared:jvmTest
```

En Windows:

```powershell
.\gradlew.bat :shared:jvmTest
```

Las pruebas se ampliarán conforme se implementen los requerimientos funcionales.

## 12. Colaboración mediante Git

El equipo utilizará Git y GitHub para mantener un historial de cambios y facilitar el trabajo desde diferentes sistemas operativos.

Flujo recomendado:

1. Clonar el repositorio.
2. Actualizar la rama principal antes de comenzar.
3. Crear una rama para la funcionalidad asignada.
4. Implementar y probar los cambios.
5. Crear commits con mensajes descriptivos.
6. Publicar la rama en GitHub.
7. Crear un Pull Request para revisión e integración.

Ejemplo:

```bash
git switch -c feature/registro-productos
```

Se recomienda no trabajar directamente sobre la rama principal y no modificar simultáneamente los mismos archivos sin coordinación.

## 13. Archivos que no deben publicarse

El repositorio debe excluir los archivos generados por las compilaciones y los datos específicos de cada computador.

Ejemplos:

- Directorios `build/`.
- Directorios `.gradle/`.
- Archivo `local.properties`.
- Configuraciones personales del IDE.
- Claves privadas, contraseñas y tokens.
- Archivos temporales de ejecución.

El archivo `.gitignore` debe revisarse antes de publicar el proyecto.

No deben excluirse los archivos necesarios para reproducir la compilación, como el Gradle Wrapper, los scripts de construcción y el catálogo de versiones.

##  14. Referencias y documentación

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [Android Studio](https://developer.android.com/studio)
- [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- [Git](https://git-scm.com/doc)

---

**Nota:** proyecto académico en desarrollo. Las funcionalidades descritas corresponden al alcance previsto y no deben interpretarse como implementadas hasta que hayan sido desarrolladas y verificadas.

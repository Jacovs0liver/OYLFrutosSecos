# OYL Frutos Secos

**Estudiante:** Oliver Edgardo Jacobs Suazo  
**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105)  
**Sección:** 004D · Duoc UC

Aplicación Android para consultar frutos secos, realizar pedidos y administrar productos e inventario. Los datos se guardan localmente en el teléfono.

## Funcionalidades

- Registro e inicio de sesión con roles de cliente y administrador.
- Catálogo con fotografías, búsqueda y categorías.
- Carrito y pago simulado, sin cobro real.
- Pedidos con seguimiento de estado y descuento de existencias.
- Gestión de productos e inventario.
- Formularios validados, cámara, lector QR, mapas y animaciones.
- Diseño Dark Minimal.

## Tecnologías

Kotlin, Jetpack Compose, Material 3, ViewModel con StateFlow y Room sobre SQLite. Lectura QR con Google Code Scanner y mapas con osmdroid.

## Ejecutar en Android Studio

1. Clonar o descargar el repositorio y abrir la carpeta que contiene `settings.gradle.kts`.
2. Usar el JDK integrado de Android Studio e instalar Android SDK 36.
3. Sincronizar Gradle con conexión a internet.
4. Conectar un teléfono con Android 8.0 o superior y depuración USB habilitada, o iniciar un emulador.
5. Seleccionar el dispositivo y ejecutar el módulo `app` con **Run**.

Para utilizar el lector QR, el dispositivo necesita Servicios de Google Play.

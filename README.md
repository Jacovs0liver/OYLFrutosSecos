# OYL Frutos Secos

Aplicación Android para consultar productos, realizar pedidos y administrar inventario.

**Estudiante:** Oliver Edgardo Jacobs Suazo  
**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105), sección 004D  
**Profesor:** Cristián Armando Gómez Vega — Duoc UC

## Funcionalidades

- Registro e inicio de sesión de clientes y administrador.
- Catálogo, carrito y confirmación de pedidos con pago simulado.
- Gestión de productos, inventario y estados de pedidos.
- Almacenamiento local con Room sobre SQLite.
- Cámara, lectura QR, mapas y animaciones.

## Tecnologías

Kotlin, Jetpack Compose, Material 3 y Room. El proyecto separa pantallas, ViewModel, repositorio y DAO.

## Ejecutar el proyecto

1. Descargar o clonar el repositorio y abrirlo en Android Studio.
2. Configurar JDK 21 y Android SDK 36.
3. Sincronizar Gradle con conexión a Internet.
4. Conectar un teléfono con Android 8.0 o superior y ejecutar `app`.

Para compilar y ejecutar las pruebas unitarias en Windows:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

## Acceso de prueba

Administrador: usuario `admin`, contraseña `123`. Para ingresar como cliente, crear una cuenta desde Registro con datos de prueba.

## Alcance

El pago es una simulación sin cobro real. Los datos se guardan en el teléfono y no se sincronizan entre dispositivos. El lector QR requiere servicios de Google Play.

La versión entregada se compiló y probó en un HONOR RBN-NX3. Esta copia usa la descarga oficial de Gradle 8.14.3 y todavía no se ha recompilado desde GitHub. Las pruebas instrumentadas siguen pendientes.

La carpeta `documentacion` contiene material de apoyo y registros de revisiones anteriores.

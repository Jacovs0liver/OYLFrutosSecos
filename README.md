# OYL Frutos Secos

Aplicación Android para consultar productos, preparar un carrito y realizar pedidos. El administrador puede gestionar productos, fotografías, inventario y estados de los pedidos.

**Oliver Edgardo Jacobs Suazo** · DSY1105 · sección 004D · Duoc UC. Profesor: Cristián Armando Gómez Vega.

## Qué incluye

- Registro, inicio de sesión y perfiles de cliente y administrador.
- Catálogo con fotografías, búsqueda y categorías.
- Carrito por cliente y compra simulada: aceptar guarda el pedido y descuenta existencias; rechazar conserva el carrito sin registrar una compra.
- Gestión de productos, movimientos de inventario y seguimiento de pedidos.
- Formularios validados, cámara, lector QR, mapas y animaciones.
- Diseño Dark Minimal con Material 3.

Está desarrollada en Kotlin y Jetpack Compose. El ViewModel maneja el estado con StateFlow, el repositorio aplica las reglas y los DAO acceden a Room sobre SQLite. Los ejemplos de clases se adaptaron al caso OYL.

No hay cobros reales, backend ni sincronización entre teléfonos. Los mapas descargan sus zonas por internet; el lector QR requiere Servicios de Google Play y puede necesitar una descarga inicial. Los avisos son locales, no push. Los productos y puntos de retiro iniciales son de demostración; las fotografías incluidas fueron generadas para el proyecto.

## Ejecutar

1. Clonar este repositorio o extraer el ZIP y abrir la carpeta que contiene `settings.gradle.kts` en Android Studio.
2. Usar el JDK integrado e instalar Android SDK 36 si se solicita. El teléfono debe tener Android 8.0 o superior.
3. Sincronizar con internet. El wrapper descarga Gradle 8.14.3 y las dependencias del proyecto.
4. Desde PowerShell, en la carpeta del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

5. Conectar el teléfono con depuración USB y ejecutar `app` después de una compilación correcta.

Para mantener los datos de una instalación anterior, no desinstalar ni borrar el almacenamiento; la actualización debe conservar la firma.

Administrador de prueba: rol **Administrador**, alias `admin`, contraseña `123`. Los clientes se crean desde Registro.

## Validación y defensa

La versión final compiló en Windows con `testDebugUnitTest assembleDebug` (`BUILD SUCCESSFUL`, 47 tareas ejecutadas) y fue instalada y probada por Oliver en su HONOR RBN-NX3. Confirmó aceptar y rechazar compras, el descuento de stock y la persistencia al cerrar y abrir.

Las pruebas instrumentadas no se han confirmado. La revisión posterior del código no cambió Kotlin, recursos, dependencias ni pruebas; el intento de recompilar en el entorno de revisión se bloqueó al descargar Gradle por falta de acceso de red.

- [Pruebas y resultados](documentacion/Pruebas.md)
- [Guía práctica de defensa](documentacion/Guion_breve_y_respuestas.md)

Los demás documentos conservan notas anteriores y pueden describir estados históricos. La entrega original de AVA y su video permanecen sin cambios.

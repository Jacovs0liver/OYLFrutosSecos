# OYL Frutos Secos

Aplicación Android académica de Oliver Edgardo Jacobs Suazo.
DSY1105, sección 004D, Duoc UC. Profesor: Cristián Armando Gómez Vega.

## Funciones
- Registro e inicio de sesión de clientes y acceso de administrador.
- Catálogo, carrito y pago académico simulado, sin cobro real.
- Pedidos y cambios de estado; inventario y aviso de stock bajo (5 unidades o menos).
- Persistencia local con Room sobre SQLite.
- Cámara, lector QR, mapa y animaciones.

## Organización
Kotlin, Jetpack Compose y Material 3. La interfaz utiliza un ViewModel, repositorio y DAO.
El código está en `app/src/main/java/cl/oyl/frutossecos`.

## Ejecutar
1. Clonar o descargar este repositorio y abrir su raíz en Android Studio.
2. Usar JDK 21 y Android SDK 36. Mantener las versiones de dependencias del proyecto.
3. Sincronizar con conexión a Internet. El wrapper descarga Gradle 8.14.3.
4. Conectar un teléfono Android compatible (mínimo API 26) y ejecutar `app`.

En Windows:
```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Administrador de demostración: alias `admin`, contraseña `123`.
Los clientes se crean desde Registro. Utilizar datos ficticios.
El lector QR requiere servicios de Google Play.

## Verificaciones y alcance
El estudiante ejecutó la compilación y pruebas unitarias en su PC y confirmó funcionamiento en HONOR RBN-NX3: conservación de productos y pedidos, compra y descuento de stock, QR, mapas, roles y validaciones. No se ha ejecutado una nueva compilación en este entorno ni se certifica la ejecución de las pruebas instrumentadas.

Proyecto académico local: no existe cobro real, backend ni sincronización de inventario entre dispositivos. Los avisos locales no son notificaciones push.

## Procedencia de esta publicación
Importación posterior a la entrega de AVA, realizada a partir de `Oliver_Jacobs_OYL.zip`. No representa un historial de desarrollo previo ni acredita trabajo colaborativo.
Se conservan los archivos fuente de la aplicación. Se excluyen cachés, compilados, configuración local, video y la distribución binaria de Gradle. Se adapta únicamente la URL del wrapper a la descarga oficial de la misma versión, junto con este README y las exclusiones de Git.
El ZIP y el video entregados en AVA permanecen sin cambios.

Los documentos de `documentacion/` se conservan como registros históricos: pueden contener pendientes y estados anteriores. Este README aclara el estado de publicación.
La planificación en Trello y la revisión del profesor siguen pendientes.

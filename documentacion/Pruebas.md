# Validación de OYL EP2

Base: OYL_EP2_Final.zip, SHA-256 `a1d121dcae9a49701bf91876ad5c32986712477971e8d822e35219e03a40ac4d` antes de actualizar documentación y exclusiones de publicación. En este cierre no se modificó código Kotlin, recursos Android, dependencias ni pruebas.

APROBADO indica evidencia concreta y su procedencia. FALLÓ distingue un fallo del entorno de un fallo de la aplicación. NO VERIFICADO significa que no se ejecutó esa comprobación o no se dispone del resultado.

| Comprobación | Estado | Evidencia y límite |
|---|---|---|
| Compilación y tarea de pruebas unitarias de la versión final en Windows | APROBADO | Salida aportada por Oliver: `testDebugUnitTest assembleDebug`, BUILD SUCCESSFUL en 1 min 5 s, 47 tareas ejecutadas. No se adjuntó el informe XML para auditar cada caso por separado. |
| Instalación, colores, aceptar/rechazar compra y persistencia en HONOR | APROBADO | Confirmación del estudiante después del recorrido de prueba final. La compra 20 → 19 también quedó confirmada expresamente en OYL_Dark anterior. No es una nueva ejecución del revisor. |
| Registro, login, roles, formularios, QR, mapas y estados de pedidos | APROBADO en pruebas manuales previas comunicadas | Se conserva la lógica revisada. No se repitieron esas pruebas físicas durante este cierre. |
| Cámara y permisos en el teléfono durante este cierre | NO VERIFICADO | Implementación revisada; falta resultado específico de captura, guardado y reapertura en esta revisión. |
| Animaciones y todas las rutas en ejecución durante este cierre | NO VERIFICADO | Existen y se revisaron estáticamente. No hay una prueba automática de todas las rutas. |
| Nueva ejecución de Gradle en el entorno de revisión | FALLÓ: entorno | `bash gradlew testDebugUnitTest assembleDebug` terminó con `java.net.SocketException: Network is unreachable` al descargar Gradle 8.14.3. No llegó a compilar ni ejecutar tests. |
| Pruebas instrumentadas | NO VERIFICADO | No hay SDK, adb, emulador ni teléfono conectado en este entorno. |
| XML y referencias a imágenes | APROBADO: revisión estática | XML parseados correctamente y todos los drawables referenciados presentes. Esto no sustituye la compilación de recursos de Android. |
| Integridad del ZIP y comparación con GitHub | APROBADO | ZIP sin errores CRC. Comparación por SHA de blobs: lógica, esquema de datos, ViewModel y pruebas coinciden con la base publicada; cambios del ZIP final en interfaz y fotografías. |

## Pruebas automatizadas existentes

`app/src/test/java/cl/oyl/frutossecos/ValidacionesTest.kt`: seis tests JUnit para registro vacío/válido, precio decimal y stock negativo, límites de cantidad, secuencia de estados y contraseñas con sal.

`app/src/androidTest/java/cl/oyl/frutossecos/PersistenciaTest.kt`: cuatro tests con Room en memoria: carritos separados, prevención de sobreventa, cambio de estado sin segundo descuento y salida de stock inválida. Estas pruebas en memoria no comprueban persistencia tras cerrar el proceso.

`app/src/androidTest/java/cl/oyl/frutossecos/ProductoQrTest.kt`: dos tests para JSON válido y rechazo de datos incompletos o inválidos.

## Ejecutar en Windows

Desde la carpeta raíz del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
Start-Process .\app\build\reports\tests\testDebugUnitTest\index.html
```

Esperado: BUILD SUCCESSFUL y seis tests aprobados, cero fallidos en el informe. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Con un dispositivo de pruebas conectado o un emulador:

```powershell
.\gradlew.bat connectedDebugAndroidTest
Start-Process .\app\build\reports\androidTests\connected\debug\index.html
```

Esperado: BUILD SUCCESSFUL y seis tests instrumentados aprobados. Si la ruta del informe cambia, abrir el archivo `index.html` dentro de `app/build/reports/androidTests/connected`. Gradle instala la app de prueba y el APK instrumentado; usar la misma firma si se elige el HONOR con datos existentes. Las pruebas actuales crean una base en memoria separada de `oyl.db`.

## Pruebas manuales importantes

1. Anotar el stock actual; rechazar una compra: no aparece pedido nuevo, el stock no cambia y el carrito sigue completo.
2. Aceptar una compra de una unidad: exactamente un pedido, stock menos uno y carrito vacío. Avanzar estado no descuenta de nuevo.
3. Cerrar y volver a abrir: pedido y stock permanecen. Cambiar de cliente: cada uno ve su carrito y pedidos.
4. Crear un producto vacío: aparecen errores. Como cliente no aparecen controles administrativos.
5. En perfil, tomar y guardar una foto, cerrar y abrir para verla. En edición de producto, tomar foto y pulsar Guardar producto.
6. Escanear un QR conocido; cancelar otro escaneo y probar un texto inválido. Revisar mapa y permiso de ubicación denegado. Sin permiso deben seguir visibles los puntos de retiro.
7. Tocar la marca en bienvenida; expandir detalle de un pedido. Probar atrás sin salir accidentalmente de la demostración.

## Cobertura que conviene ampliar después

Sin cambiar dependencias: tests instrumentados del repositorio para registro duplicado, contraseña/rol incorrectos y operaciones administrativas rechazadas al cliente; base Room en archivo temporal para cierre/reapertura; rollback ante un fallo en mitad de una compra de varios productos; aislamiento de pedidos y avisos; producto inactivo y carrito vacío. El rechazo por botón y la doble pulsación requieren prueba de interfaz; hoy se comprueban manualmente. No se agregaron tests sin poder compilarlos ni se instalaron dependencias nuevas.

## Relación con el profesor

Clases 043 y 063: datos del producto, inventario, pedidos y sus cuatro estados; pago emulado con aceptar/rechazar; SQLite local; push para el siguiente control. Clase 055: formularios reactivos y separación de responsabilidades. Esa transcripción menciona sección 003 y museo: se usa para la técnica, no para trasladar reglas de otro caso a OYL.

Ejemplos contrastados: 025 login Compose; 100 Scaffold y barras; 101 Room, entidades y DAO; 102 ViewModel y StateFlow; 103 cámara y utilidades; mapas con MapView; 105 Code Scanner y JSONObject; 106 ObjectAnimator y AnimatorSet. Se comprobaron mecanismos compartidos, no identidad literal de toda la aplicación.

La rúbrica incluye planificación real en Trello o similar. No hay evidencia disponible para dar ese requisito por cumplido. La actualización actual de GitHub fue solicitada por Oliver, quien indicó autorización del profesor. No se fabrican tareas ni actividad previa. El documento 059 no está entre los archivos disponibles de este cierre: no se certifica una comparación visual exhaustiva de sus 15 mockups.

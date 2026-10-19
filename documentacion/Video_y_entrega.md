# Video individual y entrega

## Antes de grabar

Confirmar que el proyecto compila, pasa las pruebas y funciona en el teléfono. Completar los integrantes, sección, número de equipo y enlaces de GitHub/Trello en README. Tomar las fotos de demostración y revisar las quince pantallas. Cada integrante debe poder explicar el código y su participación real.

## Guía de explicación individual

1. Presentarte, indicar asignatura, sección, equipo y problema de OYL: ordenar catálogo, stock y pedidos.
2. Mostrar README y explicar el alcance local de Evaluación 2 respaldado por clase 063.
3. Recorrer las carpetas: entidades y DAO en `data`, validaciones y claves en `domain`, estado en `OylViewModel`, pantallas y componentes en `ui`.
4. Mostrar registro e ingreso. Provocar un error para demostrar validación por campo y explicar dónde se decide.
5. Demostrar dos clientes con carritos e historiales separados. Explicar la relación mediante `usuarioId`.
6. Mostrar pago simulado rechazado y aprobado. Explicar `OylDao.comprar`: transacción, comprobación de stock, detalle histórico, movimiento y vaciado del carrito del comprador solamente al aprobar.
7. Como administrador, demostrar foto, edición de producto, entrada/salida de stock y actualización del pedido.
8. Mostrar QR y mapa en el teléfono. Explicar captura por FileProvider, Code Scanner, permisos de ubicación y ciclo de vida del MapView.
9. Mostrar animaciones, perfil, sesión persistente y avisos locales. Aclarar que backend y push pertenecen a la etapa posterior.
10. Mostrar resultados reales de pruebas y actividad real del equipo en GitHub/Trello. Explicar lo que hiciste tú y cualquier limitación pendiente.

La duración se ajusta a las indicaciones de AVA. Los correos revisados exigen explicación detallada, pero no fijan una duración concreta.

## ZIP individual para AVA

La última instrucción por correo del 15 de octubre pide que cada estudiante suba un único ZIP a Evaluación 2 de AVA con el código fuente del proyecto grupal y su video individual.

Estructura sugerida:

- `OYLFrutosSecos/`: el mismo proyecto fuente revisado y probado.
- `Video_Individual_Nombre_Apellido.mp4`: tu grabación real explicando código y ejecución.

No distribuir `local.properties`, cachés `.gradle`, directorios `build`, contraseñas reales ni claves privadas de firma. El ZIP incluye la distribución local de Gradle que requiere su wrapper; los cachés de dependencias no se incluyen.

## APK y defensa

Instalar y comprobar primero la APK debug. Para preparar una APK release firmada, usar Generate Signed Bundle / APK en Android Studio, crear o utilizar la clave del equipo y guardarla de forma privada. No inventar ni incluir una clave de producción en el repositorio.

La defensa requiere asistencia de todo el equipo, app instalada en el teléfono, proyecto abierto en Android Studio y grabación del feedback del profesor. Después se aplican las correcciones indicadas y se presenta la versión corregida según el plazo comunicado.

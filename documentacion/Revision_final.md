# Revisión para Evaluación 2 — 18 de octubre de 2026

## Estado real

Base: OYLFrutosSecos(2).zip, 137.518.583 bytes, 47 archivos. Original conservado intacto. Se trabajó en una copia. Esta revisión está preparada para recompilar; no se declara validada para entregar antes de probarla.

Oliver reportó compilación e instalación en HONOR RBN-NX3, persistencia de pedidos, carrito, compra simulada, QR, mapa, roles, descuento de 25 a 24, avance de estados y validaciones. Son pruebas del proyecto anterior comunicadas por el usuario, no ejecuciones de esta revisión por el asistente.

La documentación anterior conserva el historial. Esta revisión prevalece para distinguir lo comprobado ahora de lo afirmado anteriormente. No se revisaron nuevamente Gmail, cronograma ni videos completos en esta sesión. No se certifica el cumplimiento completo de la rúbrica ni el cotejo visual de las 15 pantallas con sus diagramas.

## Comparación directa de fuentes disponibles

| Fuente abierta | Coincidencia comprobada | Adaptación o diferencia |
|---|---|---|
| 025, MainActivity.kt | OutlinedTextField, PasswordVisualTransformation, callbacks | El ejemplo usa correo/clave fijos y rememberSaveable. OYL usa alias, roles, ViewModel, usuarios y sesión en Room. |
| Barras_completo, 100_TopAppBarScreen.kt y demás barras | Scaffold, TopAppBar y distribución Compose | OYL integra sus rutas, historial, NavigationBar, carrito y avisos. No se presenta toda su navegación como copia del ejemplo. |
| Camara_completo, 103_TomarFotoActivity.kt y util/103_Imagenes.kt | TakePicture, URI y FileProvider | Integración de la foto con el producto y manejo desde componentes OYL. |
| Mapas_completo, 103_2VistaMapa.kt | remember MapView, MAPNIK, AndroidView y observador de ciclo de vida | Puntos de retiro, permisos y ubicación OYL. El ejemplo usa LocalLifecycleOwner; OYL obtiene lifecycle de ComponentActivity. No son archivos idénticos. |
| 105 Fuente Ejemplo QR, MainActivity.kt | GmsBarcodeScanning, formato QR, listeners y JSONObject | El ejemplo interpreta videos con título/descripción/URL; OYL interpreta productos y consulta catálogo/inventario. |
| Transcripciones 043, 055 y 063 | Formularios, recursos nativos y persistencia; 063 explica SQLite/Room y ausencia de backend en esta etapa | Las transcripciones respaldan técnicas y alcance, no demuestran identidad de cada línea de código. |

Los ocho ejemplos están disponibles y sus ZIP superaron la comprobación de integridad. Se leyeron directamente 101 SQLite, 102 Formularios y 106 Animaciones. Véase Comparacion_verificada.md para las coincidencias y diferencias. Cámara y Barras se revisaron desde sus ZIP completos alternativos.

Versiones verificadas contra Mapas/QR: AGP 8.13.0, Kotlin 2.2.10, coreKtx 1.17.0, activityCompose 1.13.0, Compose BOM 2026.02.01, osmdroid 6.1.20 y Code Scanner 16.1.0. Room 2.8.5 y kapt coinciden con app/build.gradle.kts del ejemplo 101 recibido. No se cambió ningún archivo de dependencias.

Código propio ya existente: entidades del negocio, compra transaccional, reglas de roles, historial de rutas, mapa de campos y hash PBKDF2. No se retiró porque afectaría compatibilidad y funcionamiento del proyecto probado.

## Stock bajo: regla exacta

- Inicio e Inventario cuentan productos activos cuyo stock es <= 5. Cero está incluido; 6 no.
- OylDao.comprar inserta aviso al administrador cuando el stock resultante de una compra es <= 5. Puede avisar en varias compras sucesivas, no solo al cruzar el umbral.
- insertarAviso lo guarda únicamente si el usuario destinatario tiene notificaciones habilitadas.
- moverStock registra el movimiento manual, pero no crea aviso de stock bajo. El contador sí refleja el nuevo valor al recargar datos.
- Son avisos locales de la app, no notificaciones push.
- El umbral 5 pertenece a esta implementación; no se ha localizado una instrucción docente que exija ese número.

## Cambios de esta revisión

Solo se editaron tres archivos Kotlin de interfaz:

1. Pago.kt: tarjeta con total destacado; “Confirmar pedido”; aviso de compra simulada sin cobro; resultado “Pedido recibido”; ruta alternativa claramente identificada como ejemplo de pago rechazado.
2. Administracion.kt: explicación visible del umbral de stock bajo.
3. Comercio.kt: disponibilidad en lenguaje comercial y uso de “pedido” en la confirmación.

Se conservaron callbacks, reglas, base oyl.db versión 1, applicationId, dependencias, recursos y Gradle incluido. Se mantiene la paleta clara verde/crema. No se incorporó el rediseño oscuro ni fotografías inventadas. No se añadieron comentarios Kotlin.

## Comprobaciones y límites

- Integridad del ZIP de entrada: correcta.
- Comparación de archivos: cambios restringidos a la interfaz indicada y documentación.
- Intento real: bash gradlew --offline testDebugUnitTest assembleDebug. Gradle 8.14.3 arranca desde el instalador incluido, pero falla al resolver com.android.application 8.13.0, ausente en la caché del entorno. No llegó a compilar Kotlin ni ejecutar los seis tests. Ver compilacion_revision.log.
- No se ejecutaron pruebas instrumentadas, ni se renderizaron las pantallas en emulador. La apariencia debe comprobarse en el teléfono.

## Siguiente paso en Windows

1. Conserva tu carpeta actual. Extrae OYLFrutosSecos_ENTREGA.zip en otra carpeta.
2. Abre en Android Studio la carpeta OYLFrutosSecos que contiene settings.gradle.kts. Conserva el JDK que ya te permitió compilar; no aceptes actualizaciones de dependencias.
3. En la terminal del proyecto: `.\gradlew.bat testDebugUnitTest assembleDebug`.
4. Si muestra BUILD SUCCESSFUL, conecta el HONOR y pulsa Run. No desinstales ni borres datos. Actualizar sobre la instalación anterior requiere la misma firma; si aparece conflicto, conserva los datos y revisa el mensaje antes de actuar.
5. Revisa total y textos de pago con tu tamaño de fuente; ejecuta el ejemplo rechazado y comprueba que carrito/stock no cambian. Luego confirma una compra y comprueba pedido, descuento y persistencia al reabrir.
6. Comprueba cámara/galería, QR, mapa, roles y avance de pedido. Para el umbral: un producto activo en 6 no cuenta; en 5 y 0 sí. Anota resultados reales.
7. Graba solo después de esa comprobación. El ZIP de código todavía no contiene tu video individual.

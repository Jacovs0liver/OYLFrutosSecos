# Comparación directa de los ejemplos recibidos

Revisión del 18 de octubre de 2026. Los ocho ZIP disponibles se abrieron y pasaron testzip. Esta revisión se basa en los archivos adjuntos; no certifica que el Drive no tenga actualizaciones posteriores.

| Ejemplo | Coincidencia comprobada en OYL | Adaptación o diferencia |
|---|---|---|
| 025 Login | Campos Compose, contraseña oculta y callbacks | El original usa correo/clave fijos y rememberSaveable. OYL usa alias, roles, Room y ViewModel. |
| 100 Barras (completo) | Scaffold, Material 3 y barras | Rutas y acceso según rol son propios de OYL. |
| 101 SQLite | RoomDatabase, databaseBuilder con applicationContext, Entity, DAO, Query e Insert; Room 2.8.5, kapt y correctErrorTypes | El ejemplo usa Activity/XML y ExecutorService con personas. OYL integra Compose, Dispatchers.IO, instancia única y nueve entidades; añade transacciones comerciales. No copia el botón que elimina la base. |
| 102 Formularios | CampoFormulario(valor, error), esValido, estado inmutable, ViewModel, MutableStateFlow/asStateFlow y collectAsStateWithLifecycle | El ejemplo tiene campos fijos nombre/correo/edad. OYL usa un mapa de campos reutilizable y reglas del negocio; no es copia literal del formulario. |
| 103 Cámara (completo) | Contratos de actividad, URI, FileProvider y captura/selección de imagen | Se integra en productos y perfil en vez de actividades de demostración separadas. |
| 103 Mapas (completo) | osmdroid, MapView, AndroidView, permisos y ciclo de vida | Puntos de retiro y tarjetas son datos y presentación de OYL. |
| 105 QR | GmsBarcodeScanning, FORMAT_QR_CODE, startScan y callbacks | JSON de producto: nombre, descripción, precio e ID; el ejemplo original usa datos de video. |
| 106 Animaciones | AnimatorSet.playTogether, ObjectAnimator SCALE_X/SCALE_Y, 1f–1.33f–1f y 700 ms | OYL anima su marca dentro de AndroidView y cancela al salir; no reproduce las cinco tarjetas del ejemplo. AnimatedVisibility es otra adaptación existente. |

## Versiones

Barras/Cámara/Mapas/QR comparten AGP 8.13.0, Kotlin 2.2.10, core 1.17.0, activity-compose 1.13.0, BOM 2026.02.01 y SDK 36/min 26. OYL conserva esos valores. Mapas confirma osmdroid 6.1.20; QR confirma Code Scanner 16.1.0. SQLite confirma Room 2.8.5. Formularios confirma lifecycle-runtime-compose 2.8.7.

Los originales no tienen una configuración única: Login/Formularios usan BOM 2024.12.01 y SDK 35; Formularios usa JVM 17 y Animaciones JVM 1.8. OYL conserva JVM 11 y la configuración ya probada por el usuario. No se actualizaron dependencias para uniformar ejemplos distintos.

## Qué se conserva y por qué

Pedidos, roles, stock, historial de navegación, mapa de campos y PBKDF2 son adaptaciones existentes. No se presentan como código literal del profesor. Cambiarlos solo para imitar una demostración aislada introduciría riesgo en la app ya probada. El esquema oyl.db versión 1 y applicationId se conservan.

Esta última revisión documental no modifica ningún archivo de la aplicación respecto a la revisión EA2. Frente al ZIP OYLFrutosSecos(2), se conservan los cambios de presentación de Pago.kt, Comercio.kt y Administracion.kt ya descritos en Revision_final.md.

## Validación pendiente

El usuario reportó pruebas correctas de la versión anterior en HONOR RBN-NX3. Eso no certifica la revisión de textos. La compilación de este entorno está bloqueada por la falta del plugin Android en caché. Ejecutar en Windows: .\gradlew.bat testDebugUnitTest assembleDebug. Comprobar la compra y persistencia antes de grabar. No desinstalar la app existente.

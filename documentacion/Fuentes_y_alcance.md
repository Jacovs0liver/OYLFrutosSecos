> Estado actualizado: consulta `Revision_final.md`. Las verificaciones históricas de este documento no certifican la compilación de la revisión actual. La comparación directa de 101/102/106 está documentada en Comparacion_verificada.md.

# Fuentes y alcance

## Orden de referencia

1. Correos del profesor del 13 y 15 de octubre de 2026, incluido el anuncio de wireframes del 13 por la noche, que Gmail registra en UTC como 14 de octubre.
2. Explicaciones de las clases, disponibles en las transcripciones 043, 055 y 063; para cambios de alcance se prioriza la explicación más reciente del mismo tema.
3. Código original del profesor para implementar los componentes enseñados.
4. `059 SEC004D Pantallas Caso OYL Frutos Secos.pdf` y `036 SEC004D Caso OYL Frutos Secos.pdf`, como complemento del negocio y de las pantallas.
5. Pauta `057 Control 2 Grupal Caso Semestral.pdf`, como referencia complementaria. Ante una diferencia se aplica la instrucción explícita más reciente del profesor, no se reemplaza por una lectura aislada de la pauta.

Carpeta de referencia: https://drive.google.com/drive/folders/1Fsqo2eAAKyZhiRe-OC5zjRClHpfc9GQc

## Decisiones respaldadas

La transcripción 063 dice: «no hay base de datos centralizada [...] no hay backend» y explica que los datos se trabajarán en SQLite mientras tanto. En la misma clase pospone las notificaciones push para el tercer control. Por eso esta implementación resuelve el flujo comercial local y registra avisos locales; las funciones futuras del caso semestral no se simulan como servicios reales.

El correo del 15 de octubre exige un ZIP por estudiante en Evaluación 2 de AVA, con el proyecto grupal y un video individual explicando código y ejecución. La aplicación debe estar instalada en un teléfono y el código abierto en Android Studio durante la defensa. Todo el grupo debe asistir. Se graba el feedback del profesor y se entrega una versión corregida después.

La pauta 057 también requiere GitHub y planificación colaborativa real. Los enlaces y la actividad del equipo no se pueden sustituir por una estructura de carpetas ni por commits inventados.

## Uso de los ejemplos de Cristián Gómez Vega

| Ejemplo | Patrón adaptado a OYL |
|---|---|
| 025 Login | Campos OutlinedTextField, PasswordVisualTransformation y callbacks; adaptado a alias, roles y estado MVVM del 102 |
| 100 Barras | Scaffold, barra superior, navegación inferior y composición de pantallas |
| 101 SQLite | RoomDatabase, Entity y DAO; se amplía con relaciones y transacciones |
| 102 Formularios | Campos, validaciones y mensajes de error |
| 103 Cámara | Captura nativa, URI, FileProvider y presentación de imágenes |
| 103 Mapas | osmdroid, MapView dentro de AndroidView, ciclo de vida y permisos |
| 105 QR | GmsBarcodeScanning y validación de un contenido JSON |
| 106 Animaciones | Latido con AnimatorSet/ObjectAnimator; escala 1 → 1,33 → 1 y duración 700 ms, integrado mediante AndroidView |

La primera implementación añadió ZXing para generar QR dentro de la app. Se retiró esa dependencia al contrastarla con la clase 063: el profesor demuestra un generador externo en modo Texto y usa la aplicación para leer el JSON.

El QR no debe limitarse al ID. La clase 063 pide nombre, descripción y precio, y recomienda añadir ID. El lector muestra esos datos y permite abrir la ficha o el inventario del producto registrado. La compra utiliza los valores de SQLite para que un precio antiguo del QR no cambie el precio vigente.

La clase 063 pide también las pantallas de pago exitoso y fallido para una pasarela simulada. El pago aprobado crea el pedido y rebaja stock; el rechazado conserva el carrito sin crear pedido ni modificar existencias.

En la misma clase el profesor permite cambiar la parte gráfica. Los colores, textos comerciales y distribución de esta adaptación son decisiones de OYL; no se atribuyen al profesor ni se presentan como una copia visual comprobada de sus diagramas.

Se mantienen Gradle 8.14.3, AGP 8.13.0, Kotlin 2.2.10, compile/target SDK 36, min SDK 26, Room 2.8.5, osmdroid 6.1.20 y Code Scanner 16.1.0 de los ejemplos recibidos. Esta es una adaptación al negocio, no una copia literal del código docente ni una promesa de identidad con cada implementación de sus ejemplos.

## Pantallas

| N.º | Pantalla | Archivo y función |
|---|---|---|
| 1 | Bienvenida | Acceso.kt · Bienvenida |
| 2 | Registro | Acceso.kt · Registro |
| 3 | Login | Acceso.kt · Login |
| 4 | Dashboard | Comercio.kt · Inicio |
| 5 | Mapa | RecursosNativos.kt · Mapa |
| 6 | Catálogo | Comercio.kt · Catalogo |
| 7 | Detalle | Comercio.kt · Detalle |
| 8 | QR | RecursosNativos.kt · Escaner |
| 9 | Carrito | Comercio.kt · Carrito |
| 10 | Confirmación | Comercio.kt · Confirmacion |
| 11 | Productos | Administracion.kt · GestionProducto |
| 12 | Inventario | Administracion.kt · Inventario |
| 13 | Pedidos | Pedidos.kt · Pedidos |
| 14 | Avisos | Pedidos.kt · Avisos |
| 15 | Perfil | Acceso.kt · Perfil |
| Extra, clase 063 | Pago simulado | Pago.kt · Pago |
| Extra, clase 063 | Pago exitoso | Pago.kt · PagoExitoso |
| Extra, clase 063 | Pago fallido | Pago.kt · PagoFallido |

## Pendientes de verificación y participación

Completar el recorrido visual en el teléfono; recompilar la limpieza de textos con el SDK de Android; ejecutar pruebas instrumentadas; recorrer las pantallas, incluidos ambos resultados de pago; comprobar captura/QR/mapa en un teléfono real; añadir fotos de demostración; confirmar integrantes y grupo; integrar el trabajo real del equipo en GitHub/Trello; firmar la APK cuando corresponda y grabar el video individual.

## Cronograma localizado

El cronograma está dentro de `000 Acerca de Aplicaciones Móviles.pdf`, del Drive del profesor. EA2 comprende semanas 6–12 y evalúa Compose/MVVM, diseño, navegación, formularios, estado, persistencia y recursos nativos. EA3 comprende semanas 13–18 e incorpora Retrofit/REST, pruebas unitarias y firma de APK. Las últimas instrucciones de correo se aplican a la entrega: código grupal y video individual en un único ZIP por estudiante en Evaluación 2 de AVA.

Enlace del documento: https://drive.google.com/file/d/1H6xEl7K5F2xlc6lhJfUCXuxS2mO7SbyC/view

## Comparación del ejemplo 025 recibido

Se abrió íntegramente `025 Fuente miapploggin.zip`: 51.252.867 bytes, 980 entradas y prueba de integridad ZIP correcta. Su código Kotlin de aplicación está en MainActivity.kt y ui/theme/Theme.kt. El ejemplo valida el correo fijo atorres@gmail.com y clave 123, y usa rememberSaveable para alternar LoginScreen y WelcomeScreen. No implementa varios clientes, roles ni sesión en SQLite.

En OYL se conserva el patrón de pantalla Compose con campos, contraseña oculta, botón y callbacks. El estado se eleva al ViewModel según el ejemplo 102, posterior al 025. Se cambia correo fijo por alias y selección de rol de acuerdo con la clase 063 y el PDF 059. La validación de usuarios registrados y la persistencia son adaptaciones del negocio; no se presentan como funciones existentes en el 025.

## Diferencias propias que siguen existiendo

El mapa reutilizable de campos de formulario, las relaciones entre entidades OYL, las transacciones de stock, el historial de navegación y el hash PBKDF2 son código propio. PBKDF2 no aparece en los ocho ejemplos y no se atribuye al profesor. Se conserva la compatibilidad con las cuentas ya registradas en el teléfono. No se afirma que la app completa sea una copia literal de ocho ejemplos que resuelven problemas separados.

En esta revisión no se aplicó el rediseño oscuro ni se añadieron funcionalidades de Evaluación 3. La fuente de referencia para cada uno de los siete componentes sigue siendo la tabla anterior, más 025 para el login. Los videos completos no se han visto; las explicaciones de las tres clases se revisaron mediante las transcripciones facilitadas.

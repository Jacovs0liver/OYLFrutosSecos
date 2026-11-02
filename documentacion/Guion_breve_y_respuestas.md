# Defensa individual — OYL Frutos Secos

Oliver Edgardo Jacobs Suazo · DSY1105 · sección 004D · profesor Cristián Armando Gómez Vega.

## Antes de que llegue el profesor

OYL debe estar instalado y funcionando. Deja Android Studio abierto en `C:\Users\jacov\Desktop\OYLDARK\OYLFrutosSecos` si esa sigue siendo tu carpeta final. Ten el teléfono cargado, las credenciales de cliente y el QR que ya probaste. Administrador: rol Administrador, alias `admin`, clave `123`.

Con **Ctrl + Shift + N**, abre por nombre: `MainActivity.kt`, `OylApp.kt`, `OylViewModel.kt`, `OylRepository.kt`, `OylDao.kt` y `OylDatabase.kt`. No necesitas tener abiertas las 18 rutas a la vez: el profesor puede pedirte navegar a cualquiera. Graba el feedback como indicó en clase, avisándole al comenzar.

## Recorrido corto en el HONOR

1. **Inicio de sesión → Cliente → Explorar catálogo.** «OYL permite consultar productos, hacer pedidos y administrar las existencias». Muestra el stock actual de almendras; no memorices un número anterior.
2. **Almendras → agregar una unidad → carrito → Continuar con mi pedido → Continuar al pago.** «Aquí reviso la cantidad, el total y si quiero retiro o despacho».
3. **Rechazar compra → Volver a mi carrito.** «Este pago es simulado. Si lo rechazo, no se registra el pedido y el carrito se mantiene». Comprueba que el stock no cambió.
4. **Volver al pago → Aceptar compra una sola vez → Ver mis pedidos.** «Al aceptar se guarda el pedido, se descuenta una unidad y se vacía mi carrito. No se cobra dinero real».
5. **Mi perfil → Cerrar sesión → Administrador → Pedidos.** Muestra el pedido recién creado y cambia su estado. «El administrador puede confirmar, preparar y marcar como entregado. Cambiar el estado no descuenta otra vez».
6. **Inicio → Gestionar inventario → almendras.** «Aquí está el stock después de la compra. También puedo registrar entradas y salidas con un motivo».
7. **Inicio → Crear producto → Guardar producto vacío.** «Los datos se validan antes de guardar y los errores aparecen junto al campo». Vuelve sin crear nada.
8. **Cámara, QR y mapa:** Mi perfil → Tomar fotografía; pestaña QR → Escanear producto; pestaña Mapa → Ver en el mapa. Explica solo lo que estés mostrando. La cámara del formulario de producto requiere además Guardar producto para asociar la foto.
9. **Persistencia:** cierra desde recientes y abre de nuevo. «El pedido y el inventario se mantienen porque están guardados en SQLite». Para la animación, expande el detalle de un pedido; para la marca, cierra sesión y toca el logo en bienvenida.

No digas que cerrar desde recientes equivale a una prueba automatizada de muerte del proceso: es una comprobación manual de reapertura.

## Una compra completa, del botón a SQLite

Todos los archivos Kotlin siguientes están bajo `app/src/main/java/cl/oyl/frutossecos/`. Las líneas corresponden al código final revisado; Ctrl + F por el nombre de función sigue siendo la forma más rápida de encontrarlo.

| Paso | Archivo y función | Línea importante y explicación |
|---|---|---|
| Agregar | `ui/screens/Comercio.kt` → `Detalle`, línea 87 | El botón llama a `vm.agregar(p.id, cantidad)`. El total mostrado multiplica precio por cantidad. |
| Actualizar carrito | `ui/OylViewModel.kt` → `agregar`, línea 168 | Suma la cantidad existente y llama a `repo.cantidad`. El repositorio obtiene el cliente actual y el DAO valida el stock. |
| Preparar pago | `Comercio.kt` → `Confirmacion`, línea 150; `OylViewModel.prepararPago`, línea 173 | Valida modalidad/contacto, conserva esos datos y abre `Ruta.Pago`. Todavía no crea un pedido. |
| Aceptar | `ui/screens/Pago.kt` → `Pago`, línea 12; botón en línea 23 | `onClick = vm::comprar` llama a la función del ViewModel. `!s.ocupado` evita nuevas acciones mientras está procesando. |
| Coordinar | `ui/OylViewModel.kt` → `comprar`, línea 179 | Ejecuta `repo.comprar(...)`, conserva el ID devuelto y pasa a `PagoExitoso` si terminó correctamente. |
| Reglas | `data/OylRepository.kt` → `comprar`, línea 104 | `validarConfirmacion` comprueba modalidad, contacto y carrito; `cliente()` exige sesión con rol Cliente. |
| Transacción | `data/OylDao.kt` → `comprar`, línea 53 | `@Transaction` engloba validación, pedido, líneas, descuento, movimientos, vaciado y avisos. Si una operación lanza una excepción, se revierte la transacción. |
| Evitar sobreventa | `OylDao.kt` → `descontar`, línea 25 | SQL: `UPDATE productos SET stock=stock-:cantidad WHERE id=:id AND activo=1 AND stock>=:cantidad`. Solo descuenta si hay stock; `check(... == 1)` exige una fila actualizada. |
| Conservar historial | `OylDao.comprar` | `LineaPedido` copia nombre, precio y cantidad. Un cambio posterior en el precio del catálogo no altera esa línea del pedido. |
| Actualizar pantalla | `OylViewModel.ejecutar`, línea 78; `datos`, línea 63 | Ejecuta trabajo en `Dispatchers.IO`, vuelve a consultar la base y actualiza StateFlow. `OylApp` lo observa con `collectAsStateWithLifecycle`, línea 22. |
| Guardado físico | `data/OylDatabase.kt` → `obtener`, línea 13 | `Room.databaseBuilder(..., "oyl.db")` crea/abre la base local. No es una lista temporal ni una base en memoria. |

**Cómo decirlo:** «El botón llama al ViewModel. El repositorio comprueba las reglas y el DAO hace la compra en una transacción de Room. Se guarda el pedido, se descuenta el stock y se vacía el carrito. Después el ViewModel consulta los datos y actualiza la pantalla».

**Rechazar:** `Pago.kt`, línea 26, llama solo a `vm.abrir(Ruta.PagoFallido)`. No llama al repositorio ni al DAO. El rechazo emula el resultado de una pasarela; no es un pedido que el administrador cancela después.

Si falla una validación real de la compra, `ejecutar` captura la excepción y muestra un mensaje; no debe confundirse con pulsar el botón de rechazo simulado.

## Dónde está cada función y qué explicar

| Funcionalidad | Archivos, clase/función y línea de inicio | Qué mostrar o explicar | Relación con el profesor |
|---|---|---|---|
| Inicio | `MainActivity.kt`: clase `MainActivity`, `onCreate` 13 | `ViewModelProvider` obtiene el ViewModel; `setContent { OylApp(modelo) }` carga Compose. También configura el user-agent de osmdroid. | 025 usa Activity, setContent y composables. |
| Navegación | `ui/OylApp.kt`: `OylApp` 21; `OylViewModel.kt`: `abrir` 100 y `volver` 108 | `Scaffold`, barra superior/inferior, `when(ruta)` y historial. No usa NavController; es navegación manejada por estado. | 100 aporta estructura de barras; las rutas y restricciones son adaptación OYL. |
| Registro/login/roles | `ui/screens/Acceso.kt`: `Login` 38, `LoginScreen` 56, `Registro` 99; `OylRepository`: `ingresar` 69, `registrar` 80, `admin` 67 | Mostrar campo vacío y sesión correcta. El rol se comprueba junto al alias y contraseña; elegir Administrador no otorga permisos. | 025 enseña login Compose con credenciales fijas. OYL agrega usuarios SQLite, roles y hash con sal. |
| Validaciones | `ui/OylViewModel.kt`: `cambiarCampo` 132; `domain/Validaciones.kt`: `perfil` 7, `registro` 15, `producto` 21; `ui/components/Componentes.kt`: `CampoFormularioTexto` 80 | `CampoFormulario` guarda valor/error; `isError` y `supportingText` muestran errores. El repositorio valida de nuevo antes de guardar. | 102 y clase 055: ViewModel, StateFlow y estado de formulario. Reglas de productos y perfil adaptadas a OYL. |
| Catálogo/detalle | `ui/screens/Comercio.kt`: `Catalogo` 52, `TarjetaProducto` 73, `Detalle` 87 | Buscar, filtrar, abrir producto. `LazyVerticalGrid` muestra los resultados; cliente solo recibe productos activos. | Caso OYL explicado en 043/063; implementación propia con Compose. |
| Entidades | `data/Entidades.kt`: `Usuario` 9, `Producto` 24, `Carrito` 43, `Pedido` 50, `LineaPedido` 61 | `@Entity` define tablas, claves e índices. Carrito usa clave compuesta usuario/producto. `ItemCarrito` es un resultado de consulta, no otra tabla. | 101 enseña entidad/DAO/base; OYL amplía el modelo a nueve entidades. |
| Inventario | `ui/screens/Administracion.kt`: `Inventario` 48; `OylRepository.moverStock` 124; `OylDao.moverStock` 83 | Entrada/salida con motivo. Rechaza stock fuera de 0–100000 y registra el movimiento junto al ajuste. Stock bajo: activos con cinco o menos, incluido cero. | Caso 043/063; reglas comerciales OYL sobre Room 101. |
| Productos | `Administracion.kt`: `GestionProducto` 15; `OylRepository.guardarProducto` 115 | Crear/editar, validar precio entero en CLP. En edición el stock se conserva y se modifica por Inventario. | Formularios 102 adaptados al producto. |
| Pedidos | `ui/screens/Pedidos.kt`: `Pedidos` 17; `OylViewModel.avanzarPedido` 188; `OylRepository.avanzarPedido` 135; `OylDao.avanzarPedido` 75 | Estados: Pendiente de Validación → Confirmado → Preparado → Entregado. `Validaciones.siguienteEstado`, línea 33, devuelve null al terminar. | Estados del caso en 043/063; persistencia 101. |
| Aislamiento | `OylViewModel.datos` 63; `OylDao.carrito` 21, `pedidos` 26, `lineas` 28, `avisos` 36 | El cliente consulta por su ID. El administrador consulta todos los pedidos. Los productos e inventario son compartidos en ese teléfono. | Adaptación multiusuario solicitada en 063. |
| Cámara | `ui/components/Componentes.kt`: `BotonFoto` 167, función local `abrirCamara` 189; `util/Imagenes.kt`: `uriCompartible` 27, `guardarEnGaleria` 31, `cargarImagenReducida` 102 | `TakePicture` entrega una URI de FileProvider a la app de cámara. La foto se guarda en galería y su URI en el perfil/producto. Android 8/9 solicita permiso de almacenamiento; desde Android 10 usa MediaStore. | 103 usa TakePicture, FileProvider y utilidades de imagen. OYL integra las fotos a usuarios/productos. |
| QR | `ui/screens/RecursosNativos.kt`: `Escaner` 35; `OylViewModel.leerQr` 193; `domain/ProductoQr.kt`: `textoAProducto` 13 | Code Scanner obtiene el texto; JSONObject extrae nombre, descripción y precio. ID opcional. Cliente abre detalle y administrador inventario. No modifica precios desde el QR. | 105 y clase 063; enlace con catálogo e inventario adaptado. |
| Mapas | `RecursosNativos.kt`: `Mapa` 74, función local `ubicar` 93 | osmdroid/MapView, marcadores de puntos guardados y ubicación opcional. `DisposableEffect` maneja observador/cierre. Si se deniega ubicación, conserva mapa y marcadores. | Ejemplo Mapas_completo: MapView y ciclo de vida; puntos OYL adaptados. |
| Animaciones | `ui/components/MarcaAnimada.kt`: `MarcaAnimada` 16; `OylApp` línea 70; `Pedidos.kt` líneas 32 y 39 | Marca: ObjectAnimator modifica SCALE_X/SCALE_Y coordinados con AnimatorSet. Crossfade cambia pantallas. AnimatedVisibility y animateContentSize animan detalle del pedido. | Marca basada en 106; transiciones Compose añadidas a OYL. |
| Tema/fotos | `OylApp.kt`: `darkColorScheme` 24; `Componentes.kt`: `Foto` 129 | MaterialTheme distribuye colores. Texto verde claro y botones verde profundo. Foto prioriza URI; si está vacía usa el drawable del producto. | Personalización gráfica permitida en 063; recursos y tema propios. |
| Avisos | `Pedidos.kt`: `Avisos` 57; `OylDao.insertarAviso` 33 | Mensajes locales de pedidos y stock, filtrados por usuario y preferencia de avisos. No son push. | Push se pospone al siguiente control en 063. |

## Preguntas posibles y respuestas cortas

**¿Qué es MVVM aquí?** «Las pantallas son la vista. OylViewModel coordina las acciones y el estado. El modelo incluye las entidades, el repositorio y el acceso a datos».

**¿Compose y Material 3 son lo mismo?** «Compose construye la interfaz con funciones Kotlin. Material 3 aporta componentes como botones, campos y barras, además del tema visual».

**¿Qué hace StateFlow?** «Mantiene el estado observable. Cuando el ViewModel lo actualiza, Compose puede recomponer las partes que leen esos datos».

**¿Room es SQLite?** «Room es una biblioteca que facilita trabajar con SQLite. Las entidades representan las tablas; el DAO declara consultas y operaciones; OylDatabase configura la base».

**¿Por qué una transacción?** «Para que el pedido y el inventario queden consistentes. Si algo falla, no debe quedar medio pedido ni un descuento sin compra».

**¿Cómo evitas vender más de lo disponible?** «Valido cantidades y vuelvo a comprobar el stock dentro de la compra. El UPDATE solo descuenta si hay suficientes unidades y compruebo que se actualizó una fila».

**¿Y si aprieto dos veces?** «El ViewModel bloquea operaciones mientras está ocupado y el botón se deshabilita. Después de comprar, el carrito queda vacío y el DAO también rechaza un carrito vacío». No afirmes que probaste una carrera concurrente si no la ejecutaste.

**¿Dónde está la contraseña?** «Guardo un hash PBKDF2 y una sal individual, no el texto original. Al entrar calculo y comparo». Archivo `domain/Claves.kt`: `crearSal`, `calcular`, `coincide`.

**¿Elegir Administrador en el login da acceso?** «No. El repositorio verifica que la cuenta tenga ese rol y las operaciones de administración vuelven a exigirlo».

**¿Dónde validas y por qué no en el botón?** «En el ViewModel y en las reglas de Validaciones; el repositorio verifica antes de guardar. El campo solo muestra el error».

**¿El QR puede cambiar el precio?** «No. Muestra sus datos y busca el producto. La compra usa el precio del catálogo local, no el texto del QR».

**¿Qué pasa si rechazo?** «Solo navega a Pago fallido. No llama a comprar, no crea pedido y no descuenta stock».

**¿Por qué no pides permiso CAMERA?** «Se usa la aplicación de cámara mediante TakePicture y FileProvider. OYL no accede directamente al hardware con CameraX».

**¿Funciona sin internet?** «El catálogo, usuarios, carrito y pedidos funcionan con datos locales. El mapa necesita descargar las zonas y el lector QR puede necesitar descargar su módulo inicialmente».

**¿Se sincroniza con otro teléfono?** «No. Cada teléfono tiene su propia base oyl.db. En esta evaluación no hay backend».

**¿Qué tomaste de clases?** «Los mecanismos de formularios, Room, cámara, mapas, QR y animación. Los adapté a las entidades y operaciones de OYL. La transacción de compra y los roles pertenecen a esa adaptación».

**¿Usaste IA?** «Sí, como apoyo para desarrollar y revisar. Lo que te puedo explicar aquí es cómo se conectan las pantallas, las reglas y la base de datos». Continúa mostrando la función preguntada, sin inventar autoría ni trabajo en equipo.

## Ejemplo de contenido QR

Usa el código que ya probaste. Si necesitas regenerarlo, debe contener texto JSON, no un enlace:

```json
{"nombre":"Almendras naturales","descripcion":"Almendras naturales sin sal.","precio":3490}
```

Sin ID, la app busca el nombre sin distinguir mayúsculas. Con ID, busca ese ID. El precio definitivo de compra sale de SQLite.

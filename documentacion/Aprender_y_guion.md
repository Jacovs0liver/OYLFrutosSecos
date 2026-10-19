> Estado actualizado: consulta `Revision_final.md`. Las verificaciones históricas de este documento no certifican la compilación de la revisión actual. La comparación directa de 101/102/106 está documentada en Comparacion_verificada.md.

# Aprender el código y preparar el video individual

Evaluación 2 · DSY1105 · Sección 004D. Esta guía describe el código que contiene este proyecto. Úsala para practicar y explica únicamente lo que puedas mostrar y comprender. La duración del video depende de AVA: los correos revisados no fijan una duración.

## Primero: entiende un recorrido completo

Abre MainActivity.kt: onCreate prepara Android y setContent dibuja OylApp. El ViewModel conserva el estado. OylApp observa ese estado con collectAsStateWithLifecycle; su when elige la pantalla. Esa es la conexión que debes reconocer antes de memorizar funciones.

Recorre después el login:

1. En Acceso.kt, Login toma los campos del estado y se los entrega a LoginScreen. Los campos muestran value y envían cambios por onValueChange. PasswordVisualTransformation oculta la contraseña en la pantalla; no la cifra.
2. alCambiarAlias y alCambiarClave llaman a cambiarCampo en OylViewModel.kt. CampoFormulario contiene valor y error, siguiendo el ejemplo 102.
3. Entrar ejecuta alIngresar. El ViewModel llama a OylRepository.ingresar con alias, clave y rol.
4. El repositorio valida campos, consulta buscarUsuario en el DAO, comprueba clave y rol y guarda Sesion. Si falla, devuelve un mensaje; si funciona, el ViewModel abre Inicio.
5. En OylDao.kt, buscarUsuario es SELECT sobre usuarios; guardarSesion es INSERT. OylDatabase.kt configura Room con la base oyl.db.

El 025 del profesor demuestra un correo fijo y dos pantallas. OYL cambia ese ejemplo porque el profesor pide alias, dos roles y varios clientes locales. El estado MVVM procede del 102, que es posterior al login básico. No digas que el 025 ya incluía registro, roles o SQLite.

## Qué hace cada parte

| Archivo | Explicación breve |
|---|---|
| MainActivity.kt | Punto de entrada de Android y creación del ViewModel. |
| ui/OylApp.kt | Scaffold, barras, observación del estado y selección de pantalla. |
| ui/OylViewModel.kt | Estado de presentación, eventos, navegación y llamadas al repositorio fuera del hilo de interfaz. |
| ui/FormularioModels.kt | Valor y error de cada campo; formulario y errores de envío. |
| ui/screens/Acceso.kt | Bienvenida, login, registro y perfil. |
| data/Entidades.kt | Datos de usuarios, productos, carrito, pedidos y avisos. |
| data/OylDatabase.kt | Configuración de Room. |
| data/OylDao.kt | Consultas SQL y transacciones de compra/inventario. |
| data/OylRepository.kt | Validaciones y operaciones comerciales de OYL. |
| domain/Validaciones.kt | Reglas de campos, cantidades y estados. |
| domain/Claves.kt | Hash con sal para comparar contraseñas sin guardarlas directamente. Es una adición propia, no código enseñado en los ocho ejemplos. No lo confundas con ocultar texto en pantalla. |
| domain/ProductoQr.kt | Convierte el JSON leído en nombre, descripción, precio e ID opcional. |
| ui/screens/RecursosNativos.kt | Lector de Google y mapa osmdroid, permisos y ciclo de vida. |
| util/Imagenes.kt | Archivo temporal, FileProvider, guardado con MediaStore y carga de imágenes. |
| ui/components/MarcaAnimada.kt | Latido mediante AnimatorSet y ObjectAnimator del ejemplo 106. |

## Guion de explicación y demostración

### Presentación y alcance

Di tu nombre, sección, caso y equipo reales. Explica: “OYL necesita organizar su catálogo, existencias y pedidos. Esta etapa trabaja localmente con Room/SQLite. El cronograma deja la integración con Retrofit y backend para EA3; en la clase 063 el profesor aclara que todavía no hay backend y posterga push al tercer control”.

Muestra la app en el teléfono y el código abierto. No muestres una app distinta ni resultados de pruebas que no hayas ejecutado.

### Navegación y bienvenida

Muestra bienvenida y animación. Abre OylApp.kt y explica que Scaffold organiza barra superior, contenido y barra inferior. El enum Ruta y when deciden qué Composable aparece. El patrón viene del ejemplo de Barras. En MarcaAnimada.kt señala las propiedades SCALE_X y SCALE_Y y la duración de 700 ms del latido.

### Registro y login

Intenta enviar campos inválidos y muestra los errores. Registra un cliente con datos sintéticos. Cierra sesión; intenta una clave incorrecta y después la correcta. Explica el recorrido de eventos descrito al comienzo de esta guía. El administrador usa alias admin y clave 123; no se registra mediante el formulario de clientes.

### Catálogo, carrito y pago

Busca un producto, filtra una categoría, abre su ficha y agrega unidades. Explica que el carrito relaciona usuarioId con productoId y cantidad. Dos clientes pueden usar el mismo teléfono sin compartir su carrito.

En la confirmación elige entrega o retiro y comprueba el contacto. Simula pago rechazado: no debe crearse pedido ni descontarse stock. Luego aprueba. En OylDao.comprar explica @Transaction: revisar artículos, crear pedido y líneas, descontar stock, registrar movimiento y vaciar solo ese carrito. La condición stock >= cantidad evita descontar más unidades que las disponibles. No existe un cobro real.

### Administrador e inventario

Entra como administrador. Edita un producto, toma una foto y guarda. El ejemplo de Cámara usa TakePicture y una URI compartida con FileProvider; el resultado se asocia al producto. Muestra una entrada y una salida de stock. Intenta una salida excesiva y comprueba que se rechaza. Avanza un pedido de estado y verifica que no descuenta stock nuevamente.

### QR y mapa

Prepara un QR externo en modo Texto con un objeto de qr_productos.json, como en la clase 063. Muestra su lectura y los datos. JSONObject convierte el texto en ProductoQr; el cliente abre la ficha y el administrador abre inventario. Prueba también un QR inválido.

Abre el mapa, consulta marcadores y prueba Mi ubicación. Explica que osmdroid ofrece MapView, que AndroidView lo integra en Compose y que los permisos permiten ubicar el teléfono. La app debe poder mostrar las tiendas aunque se deniegue ubicación.

### Persistencia y cierre

Cierra y abre la app; comprueba sesión, fotos y datos. Muestra otro cliente para verificar aislamiento. Explica que los avisos guardados son locales, no push.

Muestra actividad real del equipo en GitHub/Trello y tu contribución real. Si existen fallos pendientes, indícalos. Finaliza con el resultado que efectivamente observaste en el teléfono.

## Preguntas para comprobar que puedes defenderlo

- ¿Qué diferencia hay entre ocultar la contraseña en la pantalla y guardar un hash?
- ¿Dónde cambia el estado cuando escribes en un campo y dónde se consulta SQLite?
- ¿Por qué un carrito necesita usuarioId?
- ¿Qué ocurre si el stock cambia después de agregar un producto al carrito?
- ¿Qué evita @Transaction cuando una operación falla a mitad de la compra?
- ¿Por qué actualizar el estado de un pedido no vuelve a rebajar stock?
- ¿Por qué el QR debe validarse antes de abrir la ficha?
- ¿Qué funciona si el usuario deniega ubicación?

Si no puedes responder una pregunta, vuelve a esa función y sigue sus llamadas con Ctrl+clic. Practica una sección del video cada vez y comprueba el resultado real.

## Entrega

El correo del 15 de octubre pide un único ZIP individual en Evaluación 2 de AVA, con fuente grupal y video individual explicando código y ejecución. No basta con el ZIP del código: agrega tu grabación. Todo el grupo debe asistir a la defensa con la app instalada y el fuente abierto; se graba el feedback para aplicar las correcciones posteriores.

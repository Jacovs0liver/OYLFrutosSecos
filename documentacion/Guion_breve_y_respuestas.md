# Guion breve y defensa

Duración sugerida de ensayo: 4–6 minutos; no es una duración oficial. Adáptala a las instrucciones reales de AVA. Mantén Android Studio abierto y muestra la aplicación del HONOR. Usa tus palabras y señala las funciones mientras explicas.

1. **Presentación.** “Soy Oliver Jacobs, sección 004D. OYL Frutos Secos permite consultar productos y gestionar pedidos e inventario. Esta versión guarda los datos localmente con Room sobre SQLite.”
2. **Estructura.** Abre MainActivity.kt, OylApp.kt y OylViewModel.kt. “Compose dibuja la interfaz. El ViewModel recibe eventos y mantiene EstadoOyl. El repositorio valida las operaciones y el DAO accede a SQLite. La pantalla observa el estado y se actualiza.”
3. **Cliente.** Muestra un error de formulario y luego el acceso correcto. Busca producto, agrega una unidad y revisa el total. “La cantidad se valida contra el stock. El pago es simulado: no existe cobro bancario.” Muestra primero el ejemplo rechazado; vuelve y confirma el pedido.
4. **Compra y persistencia.** Abre OylDao.comprar. “@Transaction agrupa pedido, líneas, descuento, movimiento y vaciado del carrito. Si falla una operación, no se guarda una compra incompleta.” Cierra y abre la app para mostrar el pedido guardado.
5. **Administrador.** Muestra el stock antes/después de la venta y avanza el pedido. “El descuento sucede al comprar, no al cambiar su estado. Stock bajo incluye productos activos con cinco unidades o menos.”
6. **Recursos nativos.** Escanea el QR preparado y muestra el mapa. Si ya comprobaste cámara, muestra captura y guardado. “El lector devuelve texto JSON; OYL lo valida. AndroidView permite mostrar MapView dentro de Compose.”
7. **Cierre.** “Adapté componentes de los ejemplos al negocio OYL. El login original usa credenciales fijas; aquí hay clientes locales y dos roles. Los pedidos y reglas de inventario son la integración del caso.” Describe tu aporte real y cualquier pendiente, sin atribuirte conocimientos que aún no puedes explicar.

## Preguntas probables y respuestas

| Pregunta | Respuesta y lugar del código |
|---|---|
| ¿Room y SQLite son lo mismo? | SQLite es el motor local; Room organiza acceso con entidades, DAO y base. OylDatabase configura oyl.db. |
| ¿Dónde está MVVM? | Las pantallas muestran EstadoOyl y envían eventos al ViewModel; este usa OylRepository y OylDao. |
| ¿Usas Navigation Compose? | No. El proyecto usa enum Ruta, historial y when dentro de OylApp. No atribuir una librería que no está. |
| ¿Por qué no consultar la base en la pantalla? | Se separa presentación y datos; withContext(Dispatchers.IO) evita hacer estas operaciones en el hilo de interfaz. |
| ¿Cómo evitas vender sin stock? | Se valida cantidad y el UPDATE exige activo=1 y stock>=cantidad; se comprueba que actualizó una fila. |
| ¿Qué sucede si falla una compra a mitad? | La excepción dentro de @Transaction provoca rollback de las operaciones de esa transacción. |
| ¿Qué ocurre con pago rechazado? | Solo abre la pantalla de rechazo; no llama a comprar ni descuenta inventario. |
| ¿Qué significa stock bajo? | <=5 en productos activos. Aviso tras compra si el administrador tiene avisos habilitados. Ajustes manuales no crean aviso. |
| ¿El precio del QR manda en la compra? | No. Se muestra el contenido leído, pero se compra con los valores actuales del catálogo local. |
| ¿Hay backend o push? | No. Los datos y avisos están en este dispositivo. Otro teléfono no comparte automáticamente esta base. |
| ¿Ocultar contraseña equivale a cifrarla? | No. PasswordVisualTransformation solo la oculta al dibujar. Claves usa PBKDF2 con sal para comparar hashes; es código propio existente. |
| ¿Qué persiste al cerrar? | Los datos guardados en Room. El estado temporal de navegación no equivale a persistencia en la base. |
| ¿Qué significa adaptar al profesor? | Reutilizar sus mecanismos y explicar los cambios del negocio; no decir que entregó esta app OYL completa. |

Antes de grabar, practica sin leer las respuestas: sigue con Ctrl+clic el recorrido botón → ViewModel → repositorio → DAO → nuevo estado. Si no entiendes un paso, revisa esa función antes de memorizar su nombre.

package cl.oyl.frutossecos.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oyl.frutossecos.data.*
import cl.oyl.frutossecos.domain.ErrorFormulario
import cl.oyl.frutossecos.domain.ProductoQr
import cl.oyl.frutossecos.domain.textoAProducto
import cl.oyl.frutossecos.domain.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Ruta(val titulo: String) {
    Bienvenida("OYL Frutos Secos"), Registro("Crear cuenta"), Login("Iniciar sesión"), Inicio("Inicio"), Mapa("Tiendas y retiro"), Catalogo("Catálogo"), Detalle("Detalle del producto"), Escaner("Escáner QR"), Carrito("Mi carrito"), Confirmacion("Confirmar pedido"), Pago("Pago"), PagoExitoso("Pago exitoso"), PagoFallido("Pago fallido"), Productos("Gestión de productos"), Inventario("Inventario"), Pedidos("Pedidos"), Avisos("Notificaciones"), Perfil("Mi perfil")
}

data class Destino(val ruta: Ruta, val productoId: Long?)

data class EstadoOyl(
    val cargando: Boolean = true,
    val ocupado: Boolean = false,
    val ruta: Ruta = Ruta.Bienvenida,
    val historial: List<Destino> = emptyList(),
    val usuario: Usuario? = null,
    val productos: List<Producto> = emptyList(),
    val carrito: List<ItemCarrito> = emptyList(),
    val pedidos: List<Pedido> = emptyList(),
    val lineas: List<LineaPedido> = emptyList(),
    val avisos: List<Aviso> = emptyList(),
    val puntos: List<PuntoRetiro> = emptyList(),
    val movimientos: List<Movimiento> = emptyList(),
    val productoId: Long? = null,
    val pedidoCreado: Long? = null,
    val productoQr: ProductoQr? = null,
    val modalidadPago: String = "Retiro en tienda",
    val contactoPago: String = "",
    val mensaje: String? = null,
    val errores: Map<String, String> = emptyMap(),
    val formulario: FormularioUiState = FormularioUiState()
) {
    val esAdmin: Boolean get() = usuario?.rol == "Administrador"
    val producto: Producto? get() = productos.firstOrNull { it.id == productoId }
    val total: Long get() = carrito.sumOf { it.subtotal }
}

class OylViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = OylRepository(OylDatabase.obtener(app))
    private val mutable = MutableStateFlow(EstadoOyl())
    val estado = mutable.asStateFlow()

    init {
        ejecutar {
            repo.iniciar()
        }
    }

    private fun datos(actual: EstadoOyl): EstadoOyl {
        val u = repo.usuarioActual()
        val admin = u?.rol == "Administrador"
        return actual.copy(
            usuario = u,
            productos = repo.dao.productos().filter { admin || it.activo },
            carrito = if (u != null && !admin) repo.dao.carrito(u.id) else emptyList(),
            pedidos = u?.let { repo.dao.pedidos(it.id, admin) }.orEmpty(),
            lineas = u?.let { repo.dao.lineas(it.id, admin) }.orEmpty(),
            avisos = u?.let { repo.dao.avisos(it.id) }.orEmpty(),
            puntos = repo.dao.puntos(),
            movimientos = if (admin) repo.dao.movimientos() else emptyList()
        )
    }

    private fun ejecutar(destino: Ruta? = null, exito: String? = null, accion: () -> Unit) {
        if (mutable.value.ocupado) return
        mutable.value = mutable.value.copy(ocupado = true, errores = emptyMap())
        viewModelScope.launch {
            try {
                val nuevo = withContext(Dispatchers.IO) { accion(); datos(mutable.value) }
                val ruta = destino ?: if (mutable.value.cargando && nuevo.usuario != null) Ruta.Inicio else mutable.value.ruta
                val formulario = when {
                    destino in listOf(Ruta.Inicio, Ruta.Bienvenida) -> FormularioUiState()
                    destino != null -> crearFormulario(destino, nuevo.productoId, nuevo)
                    else -> nuevo.formulario
                }
                mutable.value = nuevo.copy(cargando = false, ocupado = false, ruta = ruta, mensaje = exito, historial = if (destino != null) emptyList() else nuevo.historial, formulario = formulario)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val errores = (e as? ErrorFormulario)?.campos.orEmpty()
                mutable.update { it.copy(cargando = false, ocupado = false, mensaje = e.message ?: "No fue posible completar la operación", errores = errores, formulario = it.formulario.conErrores(errores)) }
            }
        }
    }

    fun abrir(ruta: Ruta, productoId: Long? = null) {
        if (mutable.value.ocupado) return
        val s = mutable.value
        if (ruta in listOf(Ruta.Productos, Ruta.Inventario) && !s.esAdmin) { mensaje("Acceso exclusivo del Administrador"); return }
        if (ruta in listOf(Ruta.Inicio, Ruta.Carrito, Ruta.Confirmacion, Ruta.Pago, Ruta.PagoExitoso, Ruta.PagoFallido, Ruta.Pedidos, Ruta.Perfil, Ruta.Avisos) && s.usuario == null) { abrir(Ruta.Login); return }
        mutable.value = s.copy(ruta = ruta, productoId = productoId, errores = emptyMap(), mensaje = null, formulario = crearFormulario(ruta, productoId, s), pedidoCreado = if (ruta == Ruta.Confirmacion) s.pedidoCreado else null, historial = if (ruta == s.ruta) s.historial else (s.historial + Destino(s.ruta, s.productoId)).takeLast(20))
    }

    fun volver() {
        if (mutable.value.ocupado) return
        val s = mutable.value
        val anterior = s.historial.lastOrNull()
        val ruta = anterior?.ruta ?: if (s.usuario != null) Ruta.Inicio else Ruta.Bienvenida
        mutable.value = s.copy(ruta = ruta, historial = s.historial.dropLast(1), errores = emptyMap(), productoId = anterior?.productoId, formulario = crearFormulario(ruta, anterior?.productoId, s))
    }

    private fun crearFormulario(ruta: Ruta, productoId: Long?, s: EstadoOyl): FormularioUiState {
        val valores = when (ruta) {
            Ruta.Login -> mapOf("alias" to "", "clave" to "", "rol" to "Cliente")
            Ruta.Registro -> listOf("nombre", "alias", "correo", "telefono", "direccion", "clave", "repetir").associateWith { "" }
            Ruta.Perfil -> s.usuario?.let { mapOf("nombre" to it.nombre, "alias" to it.alias, "correo" to it.correo, "telefono" to it.telefono, "direccion" to it.direccion, "avisos" to it.notificaciones.toString()) }.orEmpty()
            Ruta.Productos -> {
                val p = s.productos.firstOrNull { it.id == productoId }
                mapOf("nombre" to p?.nombre.orEmpty(), "categoria" to (p?.categoria ?: Validaciones.categorias.first()), "descripcion" to p?.descripcion.orEmpty(), "presentacion" to p?.presentacion.orEmpty(), "precio" to p?.precio?.toString().orEmpty(), "stock" to (p?.stock?.toString() ?: "0"), "imagen" to p?.imagen.orEmpty(), "activo" to (p?.activo ?: true).toString())
            }
            Ruta.Inventario -> mapOf("cantidad" to "", "motivo" to "", "tipo" to "Entrada")
            Ruta.Confirmacion -> mapOf("modalidad" to "Retiro en tienda", "contacto" to "${s.usuario?.telefono.orEmpty()} · ${s.usuario?.direccion.orEmpty()}")
            else -> return s.formulario
        }
        return FormularioUiState(ruta, valores.mapValues { CampoFormulario(it.value) })
    }

    fun cambiarCampo(nombre: String, valor: String) {
        mutable.update { s ->
            val form = s.formulario
            val valores = form.campos.mapValues { it.value.valor }.toMutableMap().apply { put(nombre, valor) }
            fun v(campo: String): String = valores[campo].orEmpty()
            val errores = when (form.tipo) {
                Ruta.Registro -> Validaciones.registro(v("nombre"), v("alias"), v("correo"), v("telefono"), v("direccion"), v("clave"), v("repetir"))
                Ruta.Perfil -> Validaciones.perfil(v("nombre"), v("alias"), v("correo"), v("telefono"), v("direccion"))
                Ruta.Productos -> Validaciones.producto(v("nombre"), v("categoria"), v("descripcion"), v("presentacion"), v("precio"), v("stock"))
                Ruta.Login -> buildMap {
                    if (v("alias").isBlank()) put("alias", "Ingresa tu alias")
                    if (v("clave").isBlank()) put("clave", "Ingresa tu contraseña")
                }
                Ruta.Inventario -> buildMap {
                    val cantidad = v("cantidad").toIntOrNull()
                    if (cantidad == null || cantidad !in 1..100_000) put("cantidad", "Ingresa una cantidad entre 1 y 100000")
                    if (v("motivo").trim().length !in 3..180) put("motivo", "Explica el movimiento con entre 3 y 180 caracteres")
                }
                Ruta.Confirmacion -> if (v("contacto").trim().length !in 5..180) mapOf("contacto" to "Ingresa tus datos de contacto y entrega") else emptyMap()
                else -> emptyMap()
            }
            val campos = form.campos.toMutableMap()
            campos[nombre] = CampoFormulario(valor, errores[nombre])
            if (form.intentoEnviar) campos.replaceAll { clave, campo -> campo.copy(error = errores[clave]) }
            s.copy(formulario = form.copy(campos = campos))
        }
    }

    fun mensaje(texto: String) { mutable.value = mutable.value.copy(mensaje = texto) }
    fun consumirMensaje() { mutable.value = mutable.value.copy(mensaje = null) }
    fun ingresar(alias: String, clave: String, rol: String) = ejecutar(Ruta.Inicio) { repo.ingresar(alias, clave, rol) }
    fun registrar(nombre: String, alias: String, correo: String, telefono: String, direccion: String, clave: String, repetir: String) = ejecutar(Ruta.Inicio, "Cuenta creada") { repo.registrar(nombre, alias, correo, telefono, direccion, clave, repetir) }
    fun guardarPerfil(nombre: String, alias: String, correo: String, telefono: String, direccion: String, avisos: Boolean) = ejecutar(exito = "Perfil actualizado") { repo.guardarPerfil(nombre, alias, correo, telefono, direccion, avisos) }
    fun guardarAvatar(uri: String) = ejecutar(exito = "Fotografía actualizada") { repo.guardarAvatar(uri) }
    fun salir() = ejecutar(Ruta.Bienvenida) { repo.salir() }
    fun cantidad(id: Long, cantidad: Int) = ejecutar { repo.cantidad(id, cantidad) }
    fun agregar(id: Long, cantidad: Int) {
        if (mutable.value.usuario == null) { abrir(Ruta.Login); return }
        val existente = mutable.value.carrito.firstOrNull { it.productoId == id }?.cantidad ?: 0
        ejecutar(exito = "Producto agregado al carrito") { repo.cantidad(id, existente + cantidad) }
    }
    fun prepararPago(modalidad: String, contacto: String) {
        ejecutar(destino = Ruta.Pago) {
            repo.validarConfirmacion(modalidad, contacto)
            mutable.value = mutable.value.copy(modalidadPago = modalidad, contactoPago = contacto)
        }
    }
    fun comprar() {
        val s = mutable.value
        ejecutar(destino = Ruta.PagoExitoso, exito = "Pedido recibido") {
            val id = repo.comprar(s.modalidadPago, s.contactoPago)
            mutable.value = mutable.value.copy(pedidoCreado = id)
        }
    }
    fun guardarProducto(id: Long?, nombre: String, categoria: String, descripcion: String, presentacion: String, precio: String, stock: String, imagen: String, activo: Boolean) = ejecutar(Ruta.Catalogo, "Producto guardado") { repo.guardarProducto(id, nombre, categoria, descripcion, presentacion, precio, stock, imagen, activo) }
    fun moverStock(id: Long, entrada: Boolean, cantidad: String, motivo: String) = ejecutar(exito = "Movimiento registrado") { repo.moverStock(id, entrada, cantidad, motivo) }
    fun avanzarPedido(id: Long) = ejecutar(exito = "Estado actualizado") { repo.avanzarPedido(id) }
    fun leerAviso(id: Long, destino: String) {
        val ruta = Ruta.entries.firstOrNull { it.name == destino } ?: Ruta.Pedidos
        ejecutar(destino = ruta, exito = "Aviso marcado como leído") { repo.leerAviso(id) }
    }
    fun leerQr(texto: String) {
        val producto = textoAProducto(texto)
        if (producto == null) {
            mutable.value = mutable.value.copy(productoQr = null)
            mensaje("El QR debe contener nombre, descripción y precio del producto en JSON")
        } else {
            mutable.value = mutable.value.copy(productoQr = producto)
            mensaje("Código leído correctamente")
        }
    }
}

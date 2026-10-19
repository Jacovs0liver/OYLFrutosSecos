package cl.oyl.frutossecos.data

import androidx.room.*
import cl.oyl.frutossecos.domain.Validaciones

@Dao
abstract class OylDao {
    @Query("SELECT * FROM usuarios WHERE id=:id") abstract fun usuario(id: Long): Usuario?
    @Query("SELECT * FROM usuarios WHERE alias=:alias LIMIT 1") abstract fun buscarUsuario(alias: String): Usuario?
    @Query("SELECT * FROM usuarios WHERE rol='Administrador' LIMIT 1") abstract fun administrador(): Usuario?
    @Insert abstract fun insertarUsuario(usuario: Usuario): Long
    @Update abstract fun actualizarUsuario(usuario: Usuario)
    @Query("SELECT usuarioId FROM sesion WHERE id=1") abstract fun sesion(): Long?
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract fun guardarSesion(sesion: Sesion)
    @Query("DELETE FROM sesion") abstract fun cerrarSesion()
    @Query("SELECT * FROM productos ORDER BY nombre COLLATE NOCASE") abstract fun productos(): List<Producto>
    @Query("SELECT * FROM productos WHERE id=:id") abstract fun producto(id: Long): Producto?
    @Insert abstract fun insertarProducto(producto: Producto): Long
    @Update abstract fun actualizarProducto(producto: Producto)
    @Query("SELECT c.productoId,p.nombre,p.precio,p.stock,c.cantidad,p.imagen,p.activo FROM carrito c JOIN productos p ON p.id=c.productoId WHERE c.usuarioId=:usuarioId ORDER BY p.nombre")
    abstract fun carrito(usuarioId: Long): List<ItemCarrito>
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract fun guardarCarrito(carrito: Carrito)
    @Query("DELETE FROM carrito WHERE usuarioId=:usuarioId AND productoId=:productoId") abstract fun quitarDelCarrito(usuarioId: Long, productoId: Long)
    @Query("DELETE FROM carrito WHERE usuarioId=:usuarioId") abstract fun vaciarCarrito(usuarioId: Long)
    @Query("UPDATE productos SET stock=stock-:cantidad WHERE id=:id AND activo=1 AND stock>=:cantidad") abstract fun descontar(id: Long, cantidad: Int): Int
    @Query("SELECT * FROM pedidos WHERE :admin=1 OR usuarioId=:usuarioId ORDER BY fecha DESC") abstract fun pedidos(usuarioId: Long, admin: Boolean): List<Pedido>
    @Query("SELECT * FROM pedidos WHERE id=:id") abstract fun pedido(id: Long): Pedido?
    @Query("SELECT l.* FROM lineas_pedido l JOIN pedidos p ON p.id=l.pedidoId WHERE :admin=1 OR p.usuarioId=:usuarioId ORDER BY l.id") abstract fun lineas(usuarioId: Long, admin: Boolean): List<LineaPedido>
    @Insert abstract fun insertarPedido(pedido: Pedido): Long
    @Insert abstract fun insertarLinea(linea: LineaPedido)
    @Update abstract fun actualizarPedido(pedido: Pedido)
    @Insert abstract fun guardarAviso(aviso: Aviso)
    fun insertarAviso(aviso: Aviso) {
        if (usuario(aviso.usuarioId)?.notificaciones == true) guardarAviso(aviso)
    }
    @Query("SELECT * FROM avisos WHERE usuarioId=:usuarioId ORDER BY fecha DESC") abstract fun avisos(usuarioId: Long): List<Aviso>
    @Query("UPDATE avisos SET leido=1 WHERE id=:id AND usuarioId=:usuarioId") abstract fun leerAviso(id: Long, usuarioId: Long)
    @Insert abstract fun insertarMovimiento(movimiento: Movimiento)
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC LIMIT 50") abstract fun movimientos(): List<Movimiento>
    @Insert abstract fun insertarPuntos(puntos: List<PuntoRetiro>)
    @Update abstract fun actualizarPunto(punto: PuntoRetiro)
    @Query("SELECT * FROM puntos_retiro ORDER BY id") abstract fun puntos(): List<PuntoRetiro>

    @Transaction
    open fun cambiarCantidad(usuarioId: Long, productoId: Long, cantidad: Int) {
        if (cantidad == 0) { quitarDelCarrito(usuarioId, productoId); return }
        val p = requireNotNull(producto(productoId)) { "El producto no existe" }
        require(p.activo && Validaciones.cantidad(cantidad, p.stock)) { "Cantidad inválida o stock insuficiente para ${p.nombre}" }
        guardarCarrito(Carrito(usuarioId, productoId, cantidad))
    }

    @Transaction
    open fun comprar(usuarioId: Long, modalidad: String, contacto: String): Long {
        val items = carrito(usuarioId)
        require(items.isNotEmpty()) { "El carrito está vacío" }
        items.forEach { require(it.activo && Validaciones.cantidad(it.cantidad, it.stock)) { "Revisa la disponibilidad de ${it.nombre}" } }
        val id = insertarPedido(Pedido(usuarioId = usuarioId, total = items.sumOf { it.subtotal }, modalidad = modalidad, contacto = contacto))
        items.forEach {
            check(descontar(it.productoId, it.cantidad) == 1) { "El stock de ${it.nombre} cambió. Revisa tu carrito" }
            insertarLinea(LineaPedido(pedidoId = id, productoId = it.productoId, nombre = it.nombre, precio = it.precio, cantidad = it.cantidad))
            insertarMovimiento(Movimiento(productoId = it.productoId, usuarioId = usuarioId, cantidad = -it.cantidad, motivo = "Pedido #$id"))
        }
        vaciarCarrito(usuarioId)
        insertarAviso(Aviso(usuarioId = usuarioId, titulo = "Pedido #$id recibido", mensaje = "Tu solicitud está pendiente de validación.", destino = "Pedidos"))
        administrador()?.let { admin ->
            insertarAviso(Aviso(usuarioId = admin.id, titulo = "Nuevo pedido #$id", mensaje = "Hay una solicitud pendiente de validación.", destino = "Pedidos"))
            items.forEach { item ->
                if (item.stock - item.cantidad <= 5) insertarAviso(Aviso(usuarioId = admin.id, titulo = "Stock bajo", mensaje = "${item.nombre}: quedan ${item.stock - item.cantidad} unidades.", destino = "Inventario"))
            }
        }
        return id
    }

    @Transaction
    open fun avanzarPedido(id: Long) {
        val p = requireNotNull(pedido(id)) { "El pedido no existe" }
        val siguiente = requireNotNull(Validaciones.siguienteEstado(p.estado)) { "El pedido ya fue entregado" }
        actualizarPedido(p.copy(estado = siguiente))
        insertarAviso(Aviso(usuarioId = p.usuarioId, titulo = "Pedido #$id: $siguiente", mensaje = "El estado de tu solicitud cambió a $siguiente.", destino = "Pedidos"))
    }

    @Transaction
    open fun moverStock(productoId: Long, usuarioId: Long, cantidad: Int, motivo: String) {
        val p = requireNotNull(producto(productoId)) { "El producto no existe" }
        require(cantidad != 0 && p.stock.toLong() + cantidad in 0L..100_000L) { "El movimiento dejaría un stock inválido" }
        require(motivo.trim().length in 3..180) { "Indica un motivo de entre 3 y 180 caracteres" }
        actualizarProducto(p.copy(stock = p.stock + cantidad))
        insertarMovimiento(Movimiento(productoId = productoId, usuarioId = usuarioId, cantidad = cantidad, motivo = motivo.trim()))
    }
}

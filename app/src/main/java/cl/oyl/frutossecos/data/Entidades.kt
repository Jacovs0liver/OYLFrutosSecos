package cl.oyl.frutossecos.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["alias"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val alias: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val hash: String,
    val sal: String,
    val rol: String = "Cliente",
    val avatar: String = "",
    val notificaciones: Boolean = true
)

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val presentacion: String,
    val precio: Int,
    val stock: Int,
    val imagen: String = "",
    val activo: Boolean = true
)

@Entity(tableName = "sesion", foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE)], indices = [Index("usuarioId")])
data class Sesion(@PrimaryKey val id: Int = 1, val usuarioId: Long)

@Entity(tableName = "carrito", primaryKeys = ["usuarioId", "productoId"], foreignKeys = [
    ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Producto::class, parentColumns = ["id"], childColumns = ["productoId"])
], indices = [Index("productoId")])
data class Carrito(val usuarioId: Long, val productoId: Long, val cantidad: Int)

data class ItemCarrito(val productoId: Long, val nombre: String, val precio: Int, val stock: Int, val cantidad: Int, val imagen: String, val activo: Boolean) {
    val subtotal: Long get() = precio.toLong() * cantidad
}

@Entity(tableName = "pedidos", foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"])], indices = [Index("usuarioId")])
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val fecha: Long = System.currentTimeMillis(),
    val total: Long,
    val estado: String = "Pendiente de Validación",
    val modalidad: String,
    val contacto: String
)

@Entity(tableName = "lineas_pedido", foreignKeys = [ForeignKey(entity = Pedido::class, parentColumns = ["id"], childColumns = ["pedidoId"], onDelete = ForeignKey.CASCADE)], indices = [Index("pedidoId")])
data class LineaPedido(@PrimaryKey(autoGenerate = true) val id: Long = 0, val pedidoId: Long, val productoId: Long, val nombre: String, val precio: Int, val cantidad: Int)

@Entity(tableName = "avisos", foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE)], indices = [Index("usuarioId")])
data class Aviso(@PrimaryKey(autoGenerate = true) val id: Long = 0, val usuarioId: Long, val titulo: String, val mensaje: String, val destino: String, val fecha: Long = System.currentTimeMillis(), val leido: Boolean = false)

@Entity(tableName = "movimientos", foreignKeys = [ForeignKey(entity = Producto::class, parentColumns = ["id"], childColumns = ["productoId"])], indices = [Index("productoId")])
data class Movimiento(@PrimaryKey(autoGenerate = true) val id: Long = 0, val productoId: Long, val usuarioId: Long, val cantidad: Int, val motivo: String, val fecha: Long = System.currentTimeMillis())

@Entity(tableName = "puntos_retiro")
data class PuntoRetiro(@PrimaryKey val id: Int, val nombre: String, val direccion: String, val horario: String, val latitud: Double, val longitud: Double)

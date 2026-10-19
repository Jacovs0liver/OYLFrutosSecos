package cl.oyl.frutossecos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Usuario::class, Producto::class, Sesion::class, Carrito::class, Pedido::class, LineaPedido::class, Aviso::class, Movimiento::class, PuntoRetiro::class], version = 1, exportSchema = false)
abstract class OylDatabase : RoomDatabase() {
    abstract fun dao(): OylDao
    companion object {
        @Volatile private var instancia: OylDatabase? = null
        fun obtener(context: Context): OylDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(context.applicationContext, OylDatabase::class.java, "oyl.db").build().also { instancia = it }
        }
    }
}

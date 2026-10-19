package cl.oyl.frutossecos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.oyl.frutossecos.data.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistenciaTest {
    private lateinit var db: OylDatabase
    private lateinit var dao: OylDao
    private var clienteA = 0L
    private var clienteB = 0L
    private var producto = 0L

    @Before fun preparar() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), OylDatabase::class.java).allowMainThreadQueries().build()
        dao = db.dao()
        clienteA = dao.insertarUsuario(Usuario(nombre = "Cliente A", alias = "cliente.a", correo = "a@example.com", telefono = "123456789", direccion = "Prueba A", hash = "prueba", sal = "prueba"))
        clienteB = dao.insertarUsuario(Usuario(nombre = "Cliente B", alias = "cliente.b", correo = "b@example.com", telefono = "123456789", direccion = "Prueba B", hash = "prueba", sal = "prueba"))
        producto = dao.insertarProducto(Producto(nombre = "Nueces", categoria = "Frutos secos", descripcion = "Producto de prueba", presentacion = "250 g", precio = 1000, stock = 3))
    }

    @After fun cerrar() { db.close() }

    @Test fun cadaClienteConservaSuCarrito() {
        dao.cambiarCantidad(clienteA, producto, 1)
        dao.cambiarCantidad(clienteB, producto, 2)
        assertEquals(1, dao.carrito(clienteA).single().cantidad)
        assertEquals(2, dao.carrito(clienteB).single().cantidad)
        dao.quitarDelCarrito(clienteA, producto)
        assertTrue(dao.carrito(clienteA).isEmpty())
        assertEquals(2, dao.carrito(clienteB).single().cantidad)
    }

    @Test fun compraNoSobrevendeNiBorraCarritoFallido() {
        dao.cambiarCantidad(clienteA, producto, 2)
        dao.cambiarCantidad(clienteB, producto, 2)
        val id = dao.comprar(clienteA, "Retiro en tienda", "Contacto de prueba")
        assertEquals(1, dao.producto(producto)?.stock)
        assertEquals(2000L, dao.pedido(id)?.total)
        assertTrue(dao.carrito(clienteA).isEmpty())
        try {
            dao.comprar(clienteB, "Retiro en tienda", "Contacto de prueba")
            fail("La segunda compra debe rechazarse")
        } catch (_: IllegalArgumentException) { }
        assertEquals(1, dao.producto(producto)?.stock)
        assertEquals(2, dao.carrito(clienteB).single().cantidad)
        assertTrue(dao.pedidos(clienteB, false).isEmpty())
        assertTrue(dao.lineas(clienteB, false).isEmpty())
    }

    @Test fun avanzarEstadoNoDescuentaOtraVez() {
        dao.cambiarCantidad(clienteA, producto, 1)
        val id = dao.comprar(clienteA, "Retiro en tienda", "Contacto de prueba")
        repeat(3) { dao.avanzarPedido(id) }
        assertEquals("Entregado", dao.pedido(id)?.estado)
        assertEquals(2, dao.producto(producto)?.stock)
        assertEquals(1, dao.lineas(clienteA, false).size)
    }

    @Test fun salidaInvalidaNoModificaStockNiAuditoria() {
        try {
            dao.moverStock(producto, clienteA, -4, "Ajuste de prueba")
            fail("El stock no puede ser negativo")
        } catch (_: IllegalArgumentException) { }
        assertEquals(3, dao.producto(producto)?.stock)
        assertTrue(dao.movimientos().isEmpty())
    }
}

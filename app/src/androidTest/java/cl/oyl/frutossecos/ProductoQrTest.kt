package cl.oyl.frutossecos

import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.oyl.frutossecos.domain.textoAProducto
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductoQrTest {
    @Test fun leeDatosDelProductoConIdOpcional() {
        val producto = textoAProducto("""{"nombre":"Almendras","descripcion":"Almendras naturales","precio":5000}""")
        assertNotNull(producto)
        assertEquals("Almendras", producto?.nombre)
        assertEquals(5000, producto?.precio)
        assertNull(producto?.id)
    }

    @Test fun rechazaIdSinLosDatosExplicadosEnClase() {
        assertNull(textoAProducto("""{"id":1}"""))
        assertNull(textoAProducto("""{"nombre":"Almendras","descripcion":"Naturales","precio":-1}"""))
        assertNull(textoAProducto("Esto no es JSON"))
    }
}

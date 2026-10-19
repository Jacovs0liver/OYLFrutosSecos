package cl.oyl.frutossecos

import cl.oyl.frutossecos.domain.Claves
import cl.oyl.frutossecos.domain.Validaciones
import org.junit.Assert.*
import org.junit.Test

class ValidacionesTest {
    @Test fun registroRechazaCamposVaciosYClaveDistinta() {
        val errores = Validaciones.registro("", "a", "correo", "12", "", "123", "456")
        assertEquals(setOf("nombre", "alias", "correo", "telefono", "direccion", "clave", "repetir"), errores.keys)
    }
    @Test fun registroAdmitePerfilSinteticoValido() {
        assertTrue(Validaciones.registro("Cliente Prueba", "cliente.1", "cliente@example.com", "+56912345678", "Santiago prueba", "Prueba123", "Prueba123").isEmpty())
    }
    @Test fun productoNoAdmitePrecioDecimalNiStockNegativo() {
        val errores = Validaciones.producto("Nueces", "Frutos secos", "Producto de prueba", "250 g", "3490.5", "-2")
        assertEquals(setOf("precio", "stock"), errores.keys)
    }
    @Test fun carritoRespetaStockYCantidadMaxima() {
        assertTrue(Validaciones.cantidad(3, 3))
        assertFalse(Validaciones.cantidad(4, 3))
        assertFalse(Validaciones.cantidad(0, 3))
        assertFalse(Validaciones.cantidad(1000, 2000))
    }
    @Test fun pedidoTerminaEnEntregado() {
        assertEquals("Confirmado", Validaciones.siguienteEstado("Pendiente de Validación"))
        assertEquals("Preparado", Validaciones.siguienteEstado("Confirmado"))
        assertEquals("Entregado", Validaciones.siguienteEstado("Preparado"))
        assertNull(Validaciones.siguienteEstado("Entregado"))
        assertNull(Validaciones.siguienteEstado("Estado inexistente"))
    }
    @Test fun clavesConSalesDistintasNoProducenMismoHash() {
        val sal1 = Claves.crearSal()
        val sal2 = Claves.crearSal()
        val hash = Claves.calcular("Prueba123", sal1)
        assertNotEquals(hash, Claves.calcular("Prueba123", sal2))
        assertTrue(Claves.coincide("Prueba123", sal1, hash))
        assertFalse(Claves.coincide("OtraClave", sal1, hash))
    }
}

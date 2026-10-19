package cl.oyl.frutossecos.domain

object Validaciones {
    val categorias = listOf("Frutos secos", "Semillas", "Deshidratados")
    val estados = listOf("Pendiente de Validación", "Confirmado", "Preparado", "Entregado")

    fun perfil(nombre: String, alias: String, correo: String, telefono: String, direccion: String): Map<String, String> = buildMap {
        if (nombre.trim().length !in 2..80) put("nombre", "Ingresa entre 2 y 80 caracteres")
        if (!Regex("[a-z0-9._-]{3,30}").matches(alias.trim().lowercase())) put("alias", "Usa entre 3 y 30 letras, números, puntos o guiones")
        if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(correo.trim())) put("correo", "Ingresa un correo válido")
        if (telefono.filter(Char::isDigit).length !in 8..15) put("telefono", "Ingresa un teléfono de entre 8 y 15 dígitos")
        if (direccion.trim().length !in 4..180) put("direccion", "Ingresa una dirección")
    }

    fun registro(nombre: String, alias: String, correo: String, telefono: String, direccion: String, clave: String, repetir: String): Map<String, String> =
        perfil(nombre, alias, correo, telefono, direccion).toMutableMap().apply {
            if (clave.length !in 6..64) put("clave", "Usa entre 6 y 64 caracteres")
            if (clave != repetir) put("repetir", "Las contraseñas no coinciden")
        }

    fun producto(nombre: String, categoria: String, descripcion: String, presentacion: String, precio: String, stock: String): Map<String, String> = buildMap {
        if (nombre.trim().length !in 2..80) put("nombre", "Ingresa un nombre de entre 2 y 80 caracteres")
        if (categoria !in categorias) put("categoria", "Selecciona una categoría")
        if (descripcion.trim().length !in 10..500) put("descripcion", "Describe el producto con entre 10 y 500 caracteres")
        if (presentacion.trim().length !in 2..60) put("presentacion", "Indica el peso o formato de venta")
        val precioEntero = precio.toIntOrNull()
        val stockEntero = stock.toIntOrNull()
        if (precioEntero == null || precioEntero !in 1..10_000_000) put("precio", "Ingresa un precio entero mayor que cero")
        if (stockEntero == null || stockEntero !in 0..100_000) put("stock", "Ingresa un stock entero entre 0 y 100000")
    }

    fun cantidad(cantidad: Int, stock: Int): Boolean = cantidad in 1..999 && cantidad <= stock
    fun siguienteEstado(estado: String): String? = estados.getOrNull(estados.indexOf(estado).takeIf { it >= 0 }?.plus(1) ?: estados.size)
}

class ErrorFormulario(val campos: Map<String, String>) : IllegalArgumentException("Revisa los campos indicados")

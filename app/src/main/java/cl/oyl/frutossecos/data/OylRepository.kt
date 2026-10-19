package cl.oyl.frutossecos.data

import cl.oyl.frutossecos.domain.Claves
import cl.oyl.frutossecos.domain.ErrorFormulario
import cl.oyl.frutossecos.domain.Validaciones

class OylRepository(private val db: OylDatabase) {
    val dao = db.dao()

    fun iniciar() = db.runInTransaction {
        if (dao.administrador() == null) {
            val sal = Claves.crearSal()
            dao.insertarUsuario(Usuario(nombre = "Administrador", alias = "admin", correo = "admin@admin.cl", telefono = "+56 9 1234 1234", direccion = "Santiago", hash = Claves.calcular("123", sal), sal = sal, rol = "Administrador"))
        }
        if (dao.productos().isEmpty()) {
            listOf(
                Producto(nombre = "Almendras naturales", categoria = "Frutos secos", descripcion = "Almendras naturales sin sal.", presentacion = "Bolsa de 250 g", precio = 3490, stock = 25),
                Producto(nombre = "Nueces mariposa", categoria = "Frutos secos", descripcion = "Nueces peladas para colaciones y preparaciones.", presentacion = "Bolsa de 250 g", precio = 3990, stock = 18),
                Producto(nombre = "Maní tostado", categoria = "Frutos secos", descripcion = "Maní tostado sin sal para compartir.", presentacion = "Bolsa de 500 g", precio = 2490, stock = 32),
                Producto(nombre = "Pistachos", categoria = "Frutos secos", descripcion = "Pistachos con cáscara.", presentacion = "Bolsa de 200 g", precio = 4590, stock = 4),
                Producto(nombre = "Semillas de chía", categoria = "Semillas", descripcion = "Semillas de chía para incorporar a tus preparaciones.", presentacion = "Bolsa de 250 g", precio = 2290, stock = 20),
                Producto(nombre = "Semillas de zapallo", categoria = "Semillas", descripcion = "Semillas de zapallo peladas y sin sal.", presentacion = "Bolsa de 250 g", precio = 2990, stock = 12),
                Producto(nombre = "Linaza", categoria = "Semillas", descripcion = "Semillas de linaza para recetas y preparaciones.", presentacion = "Bolsa de 250 g", precio = 1490, stock = 16),
                Producto(nombre = "Pasas rubias", categoria = "Deshidratados", descripcion = "Pasas rubias para colaciones y repostería.", presentacion = "Bolsa de 250 g", precio = 1990, stock = 24),
                Producto(nombre = "Cranberries", categoria = "Deshidratados", descripcion = "Arándanos deshidratados para mezclar y compartir.", presentacion = "Bolsa de 200 g", precio = 2690, stock = 14)
            ).forEach(dao::insertarProducto)
        }
        val descripcionesOriginales = mapOf(
            "Almendras naturales sin sal. Producto sintético para la demostración académica." to "Almendras naturales sin sal.",
            "Nueces peladas para colaciones y preparaciones. Datos de prueba." to "Nueces peladas para colaciones y preparaciones.",
            "Maní tostado sin sal para compartir. Datos de prueba." to "Maní tostado sin sal para compartir.",
            "Pistachos con cáscara. Referencia ficticia para el catálogo." to "Pistachos con cáscara.",
            "Semillas de chía para incorporar a tus preparaciones. Datos sintéticos." to "Semillas de chía para incorporar a tus preparaciones.",
            "Semillas de zapallo peladas y sin sal. Datos sintéticos." to "Semillas de zapallo peladas y sin sal.",
            "Semillas de linaza para recetas y preparaciones. Datos sintéticos." to "Semillas de linaza para recetas y preparaciones.",
            "Pasas rubias para colaciones y repostería. Datos de prueba." to "Pasas rubias para colaciones y repostería.",
            "Arándanos deshidratados para mezclar y compartir. Datos de prueba." to "Arándanos deshidratados para mezclar y compartir."
        )
        dao.productos().forEach { producto ->
            descripcionesOriginales[producto.descripcion]?.let { descripcion ->
                dao.actualizarProducto(producto.copy(descripcion = descripcion))
            }
        }
        if (dao.puntos().isEmpty()) dao.insertarPuntos(listOf(
            PuntoRetiro(1, "Tienda Santiago Centro", "Santiago Centro", "Lunes a viernes, 10:00–18:00", -33.4489, -70.6693),
            PuntoRetiro(2, "Punto de retiro Providencia", "Providencia", "Sábado, 10:00–14:00", -33.4280, -70.6110)
        ))
        dao.puntos().forEach { punto ->
            val nombre = when (punto.nombre) {
                "Tienda de demostración" -> "Tienda Santiago Centro"
                "Punto de retiro de demostración" -> "Punto de retiro Providencia"
                else -> punto.nombre
            }
            val direccion = when (punto.direccion) {
                "Santiago Centro · dirección ficticia" -> "Santiago Centro"
                "Providencia · dirección ficticia" -> "Providencia"
                else -> punto.direccion
            }
            if (nombre != punto.nombre || direccion != punto.direccion) {
                dao.actualizarPunto(punto.copy(nombre = nombre, direccion = direccion))
            }
        }
    }

    fun usuarioActual(): Usuario? = dao.sesion()?.let(dao::usuario)
    private fun cliente(): Usuario = requireNotNull(usuarioActual()) { "Inicia sesión para continuar" }.also { require(it.rol == "Cliente") { "Esta función corresponde al perfil Cliente" } }
    private fun admin(): Usuario = requireNotNull(usuarioActual()) { "Inicia sesión para continuar" }.also { require(it.rol == "Administrador") { "Solo el Administrador puede realizar esta operación" } }

    fun ingresar(alias: String, clave: String, rol: String) {
        val campos = buildMap {
            if (alias.isBlank()) put("alias", "Ingresa tu alias")
            if (clave.isBlank()) put("clave", "Ingresa tu contraseña")
        }
        if (campos.isNotEmpty()) throw ErrorFormulario(campos)
        val u = dao.buscarUsuario(alias.trim().lowercase())
        require(u != null && u.rol == rol && Claves.coincide(clave, u.sal, u.hash)) { "Alias, contraseña o tipo de usuario incorrectos" }
        dao.guardarSesion(Sesion(usuarioId = u.id))
    }

    fun registrar(nombre: String, alias: String, correo: String, telefono: String, direccion: String, clave: String, repetir: String) {
        val errores = Validaciones.registro(nombre, alias, correo, telefono, direccion, clave, repetir).toMutableMap()
        if (dao.buscarUsuario(alias.trim().lowercase()) != null) errores["alias"] = "Este alias ya está registrado"
        if (errores.isNotEmpty()) throw ErrorFormulario(errores)
        val sal = Claves.crearSal()
        val id = dao.insertarUsuario(Usuario(nombre = nombre.trim(), alias = alias.trim().lowercase(), correo = correo.trim(), telefono = telefono.trim(), direccion = direccion.trim(), hash = Claves.calcular(clave, sal), sal = sal))
        dao.guardarSesion(Sesion(usuarioId = id))
    }

    fun guardarPerfil(nombre: String, alias: String, correo: String, telefono: String, direccion: String, avisos: Boolean) {
        val u = requireNotNull(usuarioActual()) { "Inicia sesión para continuar" }
        val errores = Validaciones.perfil(nombre, alias, correo, telefono, direccion).toMutableMap()
        val otro = dao.buscarUsuario(alias.trim().lowercase())
        if (otro != null && otro.id != u.id) errores["alias"] = "Este alias ya está registrado"
        if (errores.isNotEmpty()) throw ErrorFormulario(errores)
        dao.actualizarUsuario(u.copy(nombre = nombre.trim(), alias = alias.trim().lowercase(), correo = correo.trim(), telefono = telefono.trim(), direccion = direccion.trim(), notificaciones = avisos))
    }

    fun guardarAvatar(uri: String) {
        val u = requireNotNull(usuarioActual()) { "Inicia sesión para continuar" }
        dao.actualizarUsuario(u.copy(avatar = uri))
    }

    fun cantidad(productoId: Long, cantidad: Int) = dao.cambiarCantidad(cliente().id, productoId, cantidad)
    fun comprar(modalidad: String, contacto: String): Long {
        validarConfirmacion(modalidad, contacto)
        return dao.comprar(cliente().id, modalidad, contacto.trim())
    }

    fun validarConfirmacion(modalidad: String, contacto: String) {
        require(modalidad in listOf("Retiro en tienda", "Despacho a domicilio")) { "Selecciona una modalidad" }
        if (contacto.trim().length !in 5..180) throw ErrorFormulario(mapOf("contacto" to "Ingresa tus datos de contacto y entrega"))
        require(dao.carrito(cliente().id).isNotEmpty()) { "El carrito está vacío" }
    }

    fun guardarProducto(id: Long?, nombre: String, categoria: String, descripcion: String, presentacion: String, precio: String, stock: String, imagen: String, activo: Boolean) {
        admin()
        val anterior = id?.let { requireNotNull(dao.producto(it)) { "El producto no existe" } }
        val errores = Validaciones.producto(nombre, categoria, descripcion, presentacion, precio, stock)
        if (errores.isNotEmpty()) throw ErrorFormulario(errores)
        val p = Producto(id = id ?: 0, nombre = nombre.trim(), categoria = categoria, descripcion = descripcion.trim(), presentacion = presentacion.trim(), precio = precio.toInt(), stock = anterior?.stock ?: stock.toInt(), imagen = imagen, activo = activo)
        if (anterior == null) dao.insertarProducto(p) else dao.actualizarProducto(p)
    }

    fun moverStock(id: Long, entrada: Boolean, cantidad: String, motivo: String) {
        val u = admin()
        val n = cantidad.toIntOrNull()
        val errores = buildMap {
            if (n == null || n !in 1..100_000) put("cantidad", "Ingresa una cantidad entre 1 y 100000")
            if (motivo.trim().length !in 3..180) put("motivo", "Explica el movimiento con entre 3 y 180 caracteres")
        }
        if (errores.isNotEmpty()) throw ErrorFormulario(errores)
        dao.moverStock(id, u.id, if (entrada) n!! else -n!!, motivo)
    }

    fun avanzarPedido(id: Long) { admin(); dao.avanzarPedido(id) }
    fun leerAviso(id: Long) { usuarioActual()?.let { dao.leerAviso(id, it.id) } }
    fun salir() = dao.cerrarSesion()
}

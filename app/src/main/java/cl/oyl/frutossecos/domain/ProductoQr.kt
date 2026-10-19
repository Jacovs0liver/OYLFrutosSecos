package cl.oyl.frutossecos.domain

import org.json.JSONException
import org.json.JSONObject

data class ProductoQr(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val precio: Int
)

fun textoAProducto(texto: String): ProductoQr? {
    return try {
        val json = JSONObject(texto)
        val nombre = json.optString("nombre").trim()
        val descripcion = json.optString("descripcion").trim()
        val precio = json.optString("precio").toIntOrNull()
        if (nombre.isBlank() || descripcion.isBlank() || precio == null || precio <= 0) null
        else ProductoQr(
            id = json.optString("id").toLongOrNull()?.takeIf { it > 0 },
            nombre = nombre,
            descripcion = descripcion,
            precio = precio
        )
    } catch (_: JSONException) {
        null
    }
}

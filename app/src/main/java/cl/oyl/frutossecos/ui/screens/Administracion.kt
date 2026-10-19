package cl.oyl.frutossecos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oyl.frutossecos.domain.Validaciones
import cl.oyl.frutossecos.ui.*
import cl.oyl.frutossecos.ui.components.*

@Composable
fun GestionProducto(s: EstadoOyl, vm: OylViewModel) {
    if (!s.esAdmin) return
    val p = s.producto
    val formulario = s.formulario
    val nombre = formulario.valor("nombre")
    val categoria = formulario.valor("categoria")
    val descripcion = formulario.valor("descripcion")
    val presentacion = formulario.valor("presentacion")
    val precio = formulario.valor("precio")
    val stock = formulario.valor("stock")
    val imagen = formulario.valor("imagen")
    val activo = formulario.valor("activo").toBoolean()
    Pagina {
        Text(if (p == null) "Nuevo producto" else "Editar ${p.nombre}", style = MaterialTheme.typography.headlineSmall)
        Foto(imagen, nombre.ifBlank { "Nuevo producto" }, Modifier.fillMaxWidth().height(180.dp))
        BotonFoto({ vm.cambiarCampo("imagen", it) }, vm::mensaje)
        CampoFormularioTexto("Nombre", "nombre", s, vm)
        Text("Categoría", style = MaterialTheme.typography.titleSmall)
        Opciones(Validaciones.categorias, categoria) { vm.cambiarCampo("categoria", it) }
        CampoFormularioTexto("Descripción", "descripcion", s, vm)
        CampoFormularioTexto("Presentación o peso", "presentacion", s, vm)
        CampoFormularioTexto("Precio en pesos", "precio", s, vm, numero = true)
        CampoFormularioTexto(if (p == null) "Stock inicial" else "Stock actual", "stock", s, vm, numero = true, habilitado = p == null)
        if (p != null) Text("Registra entradas y salidas desde Inventario.", style = MaterialTheme.typography.bodySmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Visible en el catálogo")
            Switch(checked = activo, onCheckedChange = { vm.cambiarCampo("activo", it.toString()) })
        }
        Button(onClick = { vm.guardarProducto(p?.id, nombre, categoria, descripcion, presentacion, precio, stock, imagen, activo) }, enabled = !s.ocupado, modifier = Modifier.fillMaxWidth()) { Text("Guardar producto") }
    }
}

@Composable
fun Inventario(s: EstadoOyl, vm: OylViewModel) {
    if (!s.esAdmin) return
    var elegido by rememberSaveable(s.productoId) { mutableStateOf(s.productoId ?: s.productos.firstOrNull()?.id) }
    var menu by remember { mutableStateOf(false) }
    val tipo = s.formulario.valor("tipo")
    val cantidad = s.formulario.valor("cantidad")
    val motivo = s.formulario.valor("motivo")
    val p = s.productos.firstOrNull { it.id == elegido }
    Pagina {
        Text("Control de existencias", style = MaterialTheme.typography.headlineSmall)
        Text("Stock bajo: ${s.productos.count { it.activo && it.stock <= 5 }} productos")
        Text("Productos activos con 5 unidades o menos, incluidos los agotados.", style = MaterialTheme.typography.bodySmall)
        Box {
            OutlinedButton(onClick = { menu = true }, modifier = Modifier.fillMaxWidth()) { Text(p?.nombre ?: "Seleccionar producto") }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                s.productos.forEach { producto -> DropdownMenuItem(text = { Text("${producto.nombre} · ${producto.stock}") }, onClick = { elegido = producto.id; menu = false }) }
            }
        }
        OutlinedButton(onClick = { vm.abrir(Ruta.Escaner) }) { Text("Seleccionar mediante QR") }
        if (p != null) {
            Text("Stock actual: ${p.stock}", style = MaterialTheme.typography.titleLarge)
            Opciones(listOf("Entrada", "Salida"), tipo) { vm.cambiarCampo("tipo", it) }
            CampoFormularioTexto("Cantidad de unidades", "cantidad", s, vm, numero = true)
            CampoFormularioTexto("Motivo del movimiento", "motivo", s, vm)
            Button(onClick = { vm.moverStock(p.id, tipo == "Entrada", cantidad, motivo) }, enabled = !s.ocupado, modifier = Modifier.fillMaxWidth()) { Text("Registrar movimiento") }
            HorizontalDivider()
            Text("Últimos movimientos", style = MaterialTheme.typography.titleMedium)
            s.movimientos.filter { it.productoId == p.id }.take(12).forEach {
                ListItem(headlineContent = { Text("${if (it.cantidad > 0) "+" else ""}${it.cantidad} · ${it.motivo}") }, supportingContent = { Text(fecha(it.fecha)) })
            }
        }
    }
}

package cl.oyl.frutossecos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.oyl.frutossecos.data.Producto
import cl.oyl.frutossecos.domain.Validaciones
import cl.oyl.frutossecos.ui.*
import cl.oyl.frutossecos.ui.components.*

@Composable
fun Inicio(s: EstadoOyl, vm: OylViewModel) {
    Pagina {
        Text("Hola, ${s.usuario?.nombre.orEmpty()}", style = MaterialTheme.typography.headlineMedium)
        Text(if (s.esAdmin) "Todo listo para organizar tu tienda." else "Encuentra algo natural para tu día.")
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (s.esAdmin) "Resumen de la tienda" else "Tu compra, a un paso", style = MaterialTheme.typography.titleLarge)
                if (s.esAdmin) {
                    Text("${s.pedidos.count { it.estado == Validaciones.estados.first() }} pedidos pendientes")
                    Text("${s.productos.count { it.activo && it.stock <= 5 }} productos con stock bajo")
                    Button(onClick = { vm.abrir(Ruta.Productos) }) { Text("Crear producto") }
                    OutlinedButton(onClick = { vm.abrir(Ruta.Inventario) }) { Text("Gestionar inventario") }
                } else {
                    Text("${s.carrito.sumOf { it.cantidad }} unidades en tu carrito")
                    Text("Total: ${pesos(s.total)}")
                    OutlinedButton(onClick = { vm.abrir(Ruta.Carrito) }) { Text("Ver mi carrito") }
                }
                Button(onClick = { vm.abrir(Ruta.Catalogo) }) { Text(if (s.esAdmin) "Consultar y editar catálogo" else "Explorar catálogo") }
            }
        }
        Text("Selección OYL", style = MaterialTheme.typography.titleLarge)
        s.productos.filter { it.activo }.take(3).forEach { TarjetaProducto(it) { vm.abrir(Ruta.Detalle, it.id) } }
        if (s.pedidos.isNotEmpty()) OutlinedButton(onClick = { vm.abrir(Ruta.Pedidos) }, modifier = Modifier.fillMaxWidth()) { Text("Revisar pedidos recientes") }
    }
}

@Composable
fun Catalogo(s: EstadoOyl, vm: OylViewModel) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("Todas") }
    val lista = s.productos.filter { (categoria == "Todas" || it.categoria == categoria) && it.nombre.contains(busqueda.trim(), ignoreCase = true) }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Campo("Buscar productos", busqueda, { busqueda = it })
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Todas", "Frutos secos", "Semillas", "Deshidratados").forEach { cat ->
                FilterChip(selected = categoria == cat, onClick = { categoria = cat }, label = { Text(if (cat == "Deshidratados") "Deshid." else cat, style = MaterialTheme.typography.labelSmall) })
            }
        }
        if (s.esAdmin) TextButton(onClick = { vm.abrir(Ruta.Productos) }) { Text("+ Nuevo producto") }
        Text("${lista.size} productos", style = MaterialTheme.typography.labelLarge)
        if (lista.isEmpty()) MensajeVacio("Sin coincidencias", "Prueba otro nombre o categoría.")
        LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(lista, key = { it.id }) { p -> TarjetaProducto(p) { vm.abrir(Ruta.Detalle, p.id) } }
        }
    }
}

@Composable
private fun TarjetaProducto(p: Producto, abrir: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().clickable(onClick = abrir)) {
        Foto(p.imagen, p.nombre, Modifier.fillMaxWidth().height(130.dp))
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(p.categoria, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(p.nombre, fontWeight = FontWeight.SemiBold)
            Text(p.presentacion, style = MaterialTheme.typography.bodySmall)
            Text(pesos(p.precio.toLong()), style = MaterialTheme.typography.titleMedium)
            Text(if (!p.activo) "Inactivo" else if (p.stock == 0) "Agotado" else "${p.stock} disponibles", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun Detalle(s: EstadoOyl, vm: OylViewModel) {
    val p = s.producto
    if (p == null) { Pagina { MensajeVacio("Producto no disponible", "Vuelve al catálogo para elegir otro artículo.") }; return }
    var cantidad by rememberSaveable(p.id) { mutableIntStateOf(1) }
    Pagina {
        Foto(p.imagen, p.nombre, Modifier.fillMaxWidth().height(240.dp))
        Text(p.categoria, color = MaterialTheme.colorScheme.primary)
        Text(p.nombre, style = MaterialTheme.typography.headlineMedium)
        Text(p.descripcion)
        Text(p.presentacion, style = MaterialTheme.typography.titleMedium)
        Text(pesos(p.precio.toLong()), style = MaterialTheme.typography.headlineSmall)
        Text(if (p.activo) "Disponibles: ${p.stock}" else "Producto inactivo")
        if (s.esAdmin) {
            Button(onClick = { vm.abrir(Ruta.Productos, p.id) }, modifier = Modifier.fillMaxWidth()) { Text("Editar producto") }
            OutlinedButton(onClick = { vm.abrir(Ruta.Inventario, p.id) }, modifier = Modifier.fillMaxWidth()) { Text("Ajustar inventario") }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { cantidad-- }, enabled = cantidad > 1) { Text("−") }
                Text(cantidad.toString(), style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = { cantidad++ }, enabled = cantidad < minOf(p.stock, 999)) { Text("+") }
            }
            Button(onClick = { vm.agregar(p.id, cantidad) }, enabled = p.activo && p.stock > 0, modifier = Modifier.fillMaxWidth()) { Text("Agregar · ${pesos(p.precio.toLong() * cantidad)}") }
        }
    }
}

@Composable
fun Carrito(s: EstadoOyl, vm: OylViewModel) {
    Pagina {
        if (s.carrito.isEmpty()) {
            MensajeVacio("Tu carrito está vacío", "Explora el catálogo y agrega tus productos favoritos.")
            Button(onClick = { vm.abrir(Ruta.Catalogo) }) { Text("Ir al catálogo") }
        }
        s.carrito.forEach { item ->
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Foto(item.imagen, item.nombre, Modifier.size(72.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${pesos(item.precio.toLong())} por unidad")
                            Text("Subtotal: ${pesos(item.subtotal)}")
                        }
                    }
                    if (!item.activo || item.cantidad > item.stock) Text("Revisa este producto: su disponibilidad cambió", color = MaterialTheme.colorScheme.error)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { vm.cantidad(item.productoId, item.cantidad - 1) }) { Text("−") }
                        Text(item.cantidad.toString())
                        OutlinedButton(onClick = { vm.cantidad(item.productoId, item.cantidad + 1) }, enabled = item.activo && item.cantidad < minOf(item.stock, 999)) { Text("+") }
                        TextButton(onClick = { vm.cantidad(item.productoId, 0) }) { Text("Quitar") }
                    }
                }
            }
        }
        if (s.carrito.isNotEmpty()) {
            HorizontalDivider()
            Text("Total: ${pesos(s.total)}", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { vm.abrir(Ruta.Confirmacion) }, modifier = Modifier.fillMaxWidth()) { Text("Continuar con mi pedido") }
        }
    }
}

@Composable
fun Confirmacion(s: EstadoOyl, vm: OylViewModel) {
    val modalidad = s.formulario.valor("modalidad")
    val contacto = s.formulario.valor("contacto")
    Pagina {
        if (s.carrito.isNotEmpty()) {
            Text("Revisa tu pedido", style = MaterialTheme.typography.headlineSmall)
            s.carrito.forEach { Text("${it.cantidad} × ${it.nombre} · ${pesos(it.subtotal)}") }
            Text("Total: ${pesos(s.total)}", style = MaterialTheme.typography.titleLarge)
            Opciones(listOf("Retiro en tienda", "Despacho a domicilio"), modalidad) { vm.cambiarCampo("modalidad", it) }
            CampoFormularioTexto("Contacto y dirección", "contacto", s, vm)
            Button(onClick = { vm.prepararPago(modalidad, contacto) }, enabled = !s.ocupado, modifier = Modifier.fillMaxWidth()) { Text("Continuar al pago") }
        } else MensajeVacio("Sin artículos", "Agrega productos al carrito para realizar un pedido.")
    }
}

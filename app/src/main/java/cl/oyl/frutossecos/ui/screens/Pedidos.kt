package cl.oyl.frutossecos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.oyl.frutossecos.domain.Validaciones
import cl.oyl.frutossecos.ui.*
import cl.oyl.frutossecos.ui.components.*

@Composable
fun Pedidos(s: EstadoOyl, vm: OylViewModel) {
    var filtro by rememberSaveable { mutableStateOf("Todos") }
    Pagina {
        Text(if (s.esAdmin) "Solicitudes de la tienda" else "Mis pedidos", style = MaterialTheme.typography.headlineSmall)
        var desplegado by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { desplegado = true }) { Text("Estado: $filtro") }
            DropdownMenu(expanded = desplegado, onDismissRequest = { desplegado = false }) {
                (listOf("Todos") + Validaciones.estados).forEach { estado -> DropdownMenuItem(text = { Text(estado) }, onClick = { filtro = estado; desplegado = false }) }
            }
        }
        val pedidos = s.pedidos.filter { filtro == "Todos" || it.estado == filtro }
        if (pedidos.isEmpty()) MensajeVacio("Sin pedidos para mostrar", "Aquí aparecerán las solicitudes y su estado.")
        pedidos.forEach { p ->
            var expandido by rememberSaveable(p.id) { mutableStateOf(false) }
            ElevatedCard(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pedido #${p.id}", style = MaterialTheme.typography.titleLarge)
                    Text(fecha(p.fecha))
                    Text(p.estado, color = MaterialTheme.colorScheme.primary)
                    Text(pesos(p.total), style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { expandido = !expandido }) { Text(if (expandido) "Ocultar detalle" else "Ver detalle") }
                    AnimatedVisibility(expandido) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            s.lineas.filter { it.pedidoId == p.id }.forEach { Text("${it.cantidad} × ${it.nombre} · ${pesos(it.precio.toLong() * it.cantidad)}") }
                            Text(p.modalidad)
                            Text(p.contacto)
                            if (s.esAdmin) Text("Cliente #${p.usuarioId}")
                        }
                    }
                    if (s.esAdmin) Validaciones.siguienteEstado(p.estado)?.let { siguiente ->
                        Button(onClick = { vm.avanzarPedido(p.id) }, enabled = !s.ocupado) { Text("Marcar: $siguiente") }
                    }
                }
            }
        }
    }
}

@Composable
fun Avisos(s: EstadoOyl, vm: OylViewModel) {
    Pagina {
        Text("Tus novedades", style = MaterialTheme.typography.headlineSmall)
        Text("Avisos de pedidos e inventario guardados en este dispositivo.", style = MaterialTheme.typography.bodyMedium)
        if (s.avisos.isEmpty()) MensajeVacio("Todo al día", "Todavía no tienes notificaciones.")
        s.avisos.forEach { aviso ->
            Card(Modifier.fillMaxWidth().clickable { vm.leerAviso(aviso.id, aviso.destino) }, colors = CardDefaults.cardColors(containerColor = if (aviso.leido) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(aviso.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(aviso.mensaje)
                    Text(fecha(aviso.fecha), style = MaterialTheme.typography.labelSmall)
                    Text(if (aviso.leido) "Leído" else "Toca para revisar", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
